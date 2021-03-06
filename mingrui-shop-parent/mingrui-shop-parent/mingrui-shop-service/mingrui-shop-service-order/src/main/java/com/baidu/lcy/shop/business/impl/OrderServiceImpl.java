package com.baidu.lcy.shop.business.impl;

import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.business.OrderService;
import com.baidu.lcy.shop.config.JwtConfig;
import com.baidu.lcy.shop.dto.Car;
import com.baidu.lcy.shop.dto.OrderDTO;
import com.baidu.lcy.shop.dto.OrderInfo;
import com.baidu.lcy.shop.dto.UserInfo;
import com.baidu.lcy.shop.entity.OrderDetailEntity;
import com.baidu.lcy.shop.entity.OrderEntity;
import com.baidu.lcy.shop.entity.OrderStatusEntity;
import com.baidu.lcy.shop.mapper.OrderDetailMapper;
import com.baidu.lcy.shop.mapper.OrderMapper;
import com.baidu.lcy.shop.mapper.OrderStatusMapper;
import com.baidu.lcy.shop.redis.repository.RedisRepository;
import com.baidu.lcy.shop.status.HTTPStatus;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
import com.baidu.lcy.shop.utils.IdWorker;
import com.baidu.lcy.shop.utils.JwtUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

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
@Slf4j
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

            orderDetailList.stream().forEach(orderDetailEntity -> {
                orderDetailMapper.updataEntity(orderDetailEntity.getSkuId(),orderDetailEntity.getNum());
            });

            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuidStr -> {
                redisRepository.delHash(userInfo.getId() + "",skuidStr);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResult(HTTPStatus.OK,"",nextId + "");
    }

    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(Long orderId) {
        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BaiduBeanUtil.copyProperties(orderEntity, OrderInfo.class);

        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId",orderInfo.getOrderId());

        List<OrderDetailEntity> orderDetailList = orderDetailMapper.selectByExample(example);
        orderInfo.setOrderDetailList(orderDetailList);

        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderInfo.getOrderId());
        orderInfo.setOrderStatusEntity(orderStatusEntity);

        return this.setResultSuccess(orderInfo);
    }

    @Override
    public Result<OrderInfo> state(OrderStatusEntity orderStatusEntity) {
        if(orderStatusEntity.getStatus() == 2){
            OrderStatusEntity entity = new OrderStatusEntity();
            entity.setOrderId(orderStatusEntity.getOrderId());
            entity.setStatus(3);
            orderStatusMapper.updateByPrimaryKeySelective(entity);
            return this.setResultSuccess();
        }else if(orderStatusEntity.getStatus() == 3){
            OrderStatusEntity entity = new OrderStatusEntity();
            entity.setOrderId(orderStatusEntity.getOrderId());
            entity.setStatus(4);
            orderStatusMapper.updateByPrimaryKeySelective(entity);
            return this.setResultSuccess();
        }else if(orderStatusEntity.getStatus() == 4){
            OrderStatusEntity entity = new OrderStatusEntity();
            entity.setOrderId(orderStatusEntity.getOrderId());
            entity.setStatus(5);
            orderStatusMapper.updateByPrimaryKeySelective(entity);
            return this.setResultSuccess();
        }else if(orderStatusEntity.getStatus() == 5){
            OrderStatusEntity entity = new OrderStatusEntity();
            entity.setOrderId(orderStatusEntity.getOrderId());
            entity.setStatus(6);
            orderStatusMapper.updateByPrimaryKeySelective(entity);
            return this.setResultSuccess();
        }
        OrderStatusEntity entity = new OrderStatusEntity();
        entity.setOrderId(orderStatusEntity.getOrderId());
        entity.setStatus(2);
        orderStatusMapper.updateByPrimaryKeySelective(entity);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> list(Integer page,Integer row,String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Example example = new Example(OrderEntity.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",info.getId() + "");
            if(page != null && row != null){
                PageHelper.startPage(page,row);
            }
            List<OrderEntity> orderEntities = orderMapper.selectByExample(example);
            PageInfo<OrderEntity> pageInfo = new PageInfo<>(orderEntities);
            log.debug(String.valueOf(pageInfo));
            List<OrderInfo> collect = orderEntities.stream().map(orderEntity -> {
                OrderInfo orderInfo = new OrderInfo();

                Example detailExample = new Example(OrderDetailEntity.class);
                Example.Criteria detailCriteria = detailExample.createCriteria();
                detailCriteria.andEqualTo("orderId", orderEntity.getOrderId());
                List<OrderDetailEntity> orderDetailEntities = orderDetailMapper.selectByExample(detailExample);
                orderInfo.setOrderDetailList(orderDetailEntities);

                Example statusExample = new Example(OrderStatusEntity.class);
                Example.Criteria statusCriteria = statusExample.createCriteria();
                statusCriteria.andEqualTo("orderId", orderEntity.getOrderId());
                List<OrderStatusEntity> orderStatusEntities = orderStatusMapper.selectByExample(statusExample);
                orderInfo.setOrderStatusEntity(orderStatusEntities.get(0));

                orderInfo.setOrderId(orderEntity.getOrderId());
                orderInfo.setOrderStringId(orderEntity.getOrderId()+"");
                orderInfo.setTotalPay(orderEntity.getTotalPay());
                orderInfo.setActualPay(orderEntity.getActualPay());
                orderInfo.setPromotionIds(orderEntity.getPromotionIds());
                orderInfo.setPaymentType(orderEntity.getPaymentType());
                orderInfo.setCreateTime(orderEntity.getCreateTime());
                orderInfo.setUserId(orderEntity.getUserId());
                orderInfo.setBuyerMessage(orderEntity.getBuyerMessage());
                orderInfo.setBuyerNick(orderEntity.getBuyerNick());
                orderInfo.setBuyerRate(orderEntity.getBuyerRate());
                orderInfo.setInvoiceType(orderEntity.getInvoiceType());
                orderInfo.setSourceType(orderEntity.getSourceType());
                return orderInfo;
            }).collect(Collectors.toList());

            return this.setResult(200,pageInfo.getPages()+"",collect);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> sales(Integer id) {

        Example detailExample = new Example(OrderDetailEntity.class);
        Example.Criteria detailCriteria = detailExample.createCriteria();
        detailCriteria.andEqualTo("id",id);
        List<OrderDetailEntity> orderDetailEntitie = orderDetailMapper.selectByExample(detailExample);

        Example example1 = new Example(OrderDetailEntity.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("orderId",orderDetailEntitie.get(0).getOrderId());
        List<OrderDetailEntity> orderDetailEntities = orderDetailMapper.selectByExample(example1);

        if(orderDetailEntities.size() <=1){
            orderDetailMapper.deleteByExample(detailExample);

            Example example = new Example(OrderEntity.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId",orderDetailEntities.get(0).getOrderId());
            orderMapper.deleteByExample(example);

            Example statusExample = new Example(OrderStatusEntity.class);
            Example.Criteria statusCriteria = statusExample.createCriteria();
            statusCriteria.andEqualTo("orderId",orderDetailEntities.get(0).getOrderId());
            orderStatusMapper.deleteByExample(statusExample);
            return this.setResultSuccess();
        }
        orderDetailMapper.deleteByExample(detailExample);
        return this.setResultSuccess();
    }
}
