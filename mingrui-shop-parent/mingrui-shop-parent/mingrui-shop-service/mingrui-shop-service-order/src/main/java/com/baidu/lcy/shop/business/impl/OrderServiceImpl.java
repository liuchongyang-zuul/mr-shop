package com.baidu.lcy.shop.business.impl;

import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.business.OrderService;
import com.baidu.lcy.shop.config.JwtConfig;
import com.baidu.lcy.shop.dto.Car;
import com.baidu.lcy.shop.dto.OrderDTO;
import com.baidu.lcy.shop.dto.UserInfo;
import com.baidu.lcy.shop.entity.OrderDetailEntity;
import com.baidu.lcy.shop.entity.OrderEntity;
import com.baidu.lcy.shop.entity.OrderStatusEntity;
import com.baidu.lcy.shop.mapper.OrderDetailMapper;
import com.baidu.lcy.shop.mapper.OrderMapper;
import com.baidu.lcy.shop.mapper.OrderStatusMapper;
import com.baidu.lcy.shop.redis.repository.RedisRepository;
import com.baidu.lcy.shop.status.HTTPStatus;
import com.baidu.lcy.shop.utils.IdWorker;
import com.baidu.lcy.shop.utils.JwtUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/21
 * @Version V1.0
 **/
@RestController
public class OrderServiceImpl  extends BaseApiService implements OrderService {
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private IdWorker idWorker;

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private RedisRepository redisRepository;

    @Override
    public Result<String> createOrder(OrderDTO orderDTO,String token) {

        long nextId = idWorker.nextId();

        try {
            UserInfo userInfo  = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            OrderEntity orderEntity = new OrderEntity();
            Date date = new Date();
            orderEntity.setOrderId(nextId);
            orderEntity.setUserId(userInfo.getId() + "");
            orderEntity.setSourceType(1);
            orderEntity.setInvoiceType(1);
            orderEntity.setBuyerRate(1);
            orderEntity.setBuyerNick(userInfo.getUsername());
            orderEntity.setBuyerMessage("ZBC");
            orderEntity.setPaymentType(orderDTO.getPayType());
            orderEntity.setCreateTime(date);

            List<Long> longs = Arrays.asList(0L);

            List<OrderDetailEntity> orderDetailList  = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuId -> {
                Car hash = redisRepository.getHash(userInfo.getId() + "", skuId, Car.class);
                if(hash == null){
                    throw new RuntimeException("数据异常");
                }
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setSkuId(Long.valueOf(skuId));
                orderDetailEntity.setOrderId(nextId);
                orderDetailEntity.setNum(hash.getNum());
                orderDetailEntity.setPrice(hash.getPrice());
                orderDetailEntity.setTitle(hash.getTitle());
                orderDetailEntity.setImage(hash.getImage());
                longs.set(0, hash.getPrice() * hash.getNum() + longs.get(0));
                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setActualPay(longs.get(0));
            orderEntity.setTotalPay(longs.get(0));

            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date);
            orderStatusEntity.setOrderId(nextId);
            orderStatusEntity.setStatus(1);//已经创建订单,但是没有支付

            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuidStr -> {
                redisRepository.delHash(userInfo.getId() + "",skuidStr);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResult(HTTPStatus.OK,"",nextId + "");
    }
}
