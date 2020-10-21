package com.baidu.lcy.shop.service.impl;

import com.baidu.lcy.shop.config.JwtConfig;
import com.baidu.lcy.shop.utils.ObjectUtil;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.Car;
import com.baidu.lcy.shop.dto.UserInfo;
import com.baidu.lcy.shop.entity.SkuEntity;
import com.baidu.lcy.shop.fegin.GoodsFeign;
import com.baidu.lcy.shop.redis.repository.RedisRepository;
import com.baidu.lcy.shop.service.CarService;
import com.baidu.lcy.shop.utils.JSONUtil;
import com.baidu.lcy.shop.utils.JwtUtils;
import com.baidu.lcy.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {
    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private GoodsFeign goodsFeign;

    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Car hash = redisRepository.getHash(userInfo.getId() + "", car.getSkuId() + "", Car.class);

            Car saveCar = null;
            System.out.println("通过key : {} ,skuid : {} 获取到的数据为 : {}" + userInfo.getId() + car.getSkuId() + hash);

            if(ObjectUtil.isNotNull(hash)){
                hash.setNum(hash.getNum() + car.getNum());
                saveCar = hash;
                System.out.println("当前用户购物车中有将要新增的商品，重新设置num : {}" + hash.getNum());
            }else{
                Result<SkuEntity> skuResult = goodsFeign.getSkuById(car.getSkuId());
                if(skuResult.getCode() == 200){
                    SkuEntity skuEntity = skuResult.getData();
                    car.setTitle(skuEntity.getTitle());
                    car.setImage(StringUtil.isNotEmpty(skuEntity.getImages()) ? skuEntity.getImages().split(",")[0] : "");

                    Map<String, Object> map = JSONUtil.toMap(skuEntity.getOwnSpec());

                    car.setOwnSpec(skuEntity.getOwnSpec());
                    car.setPrice(Long.valueOf(skuEntity.getPrice()));
                    car.setUserId(userInfo.getId());
                    saveCar = car;
                    log.debug("新增商品到购物车redis,KEY : {} , skuid : {} , car : {}",userInfo.getId(),car.getSkuId(),JSONUtil.toJsonString(car));
                }
            }
            redisRepository.setHash(userInfo.getId() + "",car.getSkuId() + "", JSONUtil.toJsonString(saveCar));
            System.out.println("新增到redis数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {

        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);

        List<Car> list = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.getJSONArray("clientCarList").toString(), Car.class);

        list.stream().forEach(car -> {
            this.addCar(car,token);
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getUserGoodsCar(String token) {
        List<Car> list = new ArrayList<>();

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String, String> hash = redisRepository.getHash(userInfo.getId() + "");
            hash.forEach((key,value) ->{
                list.add(JSONUtil.toBean(value,Car.class));
            });
            return this.setResultSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("内部");
    }

    @Override
    public Result<List<Car>> carNumUpdate(Long skuId, Integer type, String token) {

        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Car hash = redisRepository.getHash(info.getId() + "", skuId + "", Car.class);

            if(hash != null){
                hash.setNum(type == 1 ? hash.getNum() +1 : hash.getNum() -1);
            }
            redisRepository.setHash(info.getId()+"",hash.getSkuId() +"" ,JSONUtil.toJsonString(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }
}
