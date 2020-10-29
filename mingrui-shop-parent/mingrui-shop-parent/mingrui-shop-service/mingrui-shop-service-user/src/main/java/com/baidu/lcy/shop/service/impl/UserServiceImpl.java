package com.baidu.lcy.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.config.JwtConfig;
import com.baidu.lcy.shop.dto.UserDTO;
import com.baidu.lcy.shop.dto.UserInfo;
import com.baidu.lcy.shop.entity.AddressEntity;
import com.baidu.lcy.shop.entity.CityEntity;
import com.baidu.lcy.shop.entity.UserEntity;
import com.baidu.lcy.shop.mapper.AddressMapper;
import com.baidu.lcy.shop.mapper.CityMapper;
import com.baidu.lcy.shop.mapper.UserMapper;
import com.baidu.lcy.shop.redis.repository.RedisRepository;
import com.baidu.lcy.shop.service.UserService;
import com.baidu.lcy.shop.utils.BCryptUtil;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
import com.baidu.lcy.shop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/13
 * @Version V1.0
 **/
@RestController
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Resource
    private CityMapper cityMapper;

    @Resource
    private AddressMapper addressMapper;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {
        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);

        userEntity.setCreated(new Date());
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));

        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(type == UserConstont.USER_TYPE_USERNAME){
            criteria.andEqualTo("username",value);
        }else if(type == UserConstont.USER_TYPE_PHONT){
            criteria.andEqualTo("phone",value);
        }
        List<UserEntity> user = userMapper.selectByExample(example);

        return this.setResultSuccess(user);
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {

        String phone = userDTO.getPhone();

        String code = (int)((Math.random() * 9 + 1) * 100000) + "";

        System.out.println(code);

        //LuosimaoDuanxinUtil.SendCode(phone,code);
        redisRepository.set(phone,code);
        redisRepository.expire(phone,120);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> checkCode(String phone, String validcode) {

        String s = redisRepository.get(phone);

        if(!validcode.equals(s))return this.setResultError("验证码输入错误");

        return this.setResultSuccess();
    }

    @Override
    public Result<List<CityEntity>> oneCity(Integer id) {

        Example example = new Example(CityEntity.class);
        if(id == 0){
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("parentId",0);
            List<CityEntity> cityEntities = cityMapper.selectByExample(example);
            return this.setResultSuccess(cityEntities);
        }else{
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("parentId",id);
            List<CityEntity> cityEntities = cityMapper.selectByExample(example);
            return this.setResultSuccess(cityEntities);
        }
    }

    @Override
    public Result<JSONObject> add(AddressEntity addressEntity, String token) {

        if(addressEntity.getId() != null){
            addressMapper.updateByPrimaryKeySelective(addressEntity);
            return this.setResultSuccess();
        }
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            addressEntity.setUserId(info.getId());
            addressEntity.setMoren(false);
            addressMapper.insertSelective(addressEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> list(String token) {

        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Example example = new Example(AddressEntity.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",info.getId());
            List<AddressEntity> addressEntities = addressMapper.selectByExample(example);
            return this.setResultSuccess(addressEntities);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("数据错误");
    }

    @Override
    public Result<JSONObject> del(Integer id, String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Example example = new Example(AddressEntity.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",info.getId());
            criteria.andEqualTo("id",id);
            addressMapper.deleteByExample(example);
            return this.setResultSuccess();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("删除错误");
    }

    @Override
    public Result<JSONObject> updateDefault(AddressEntity address, String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Example example = new Example(AddressEntity.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",info.getId());
            criteria.andEqualTo("moren",true);

            List<AddressEntity> addressEntities = addressMapper.selectByExample(example);

            if(addressEntities.size()>0){
                AddressEntity addressEntity = addressEntities.get(0);
                addressEntity.setMoren(false);
                addressMapper.updateByPrimaryKeySelective(addressEntity);

                AddressEntity entity = new AddressEntity();
                entity.setId(address.getId());
                entity.setMoren(true);
                addressMapper.updateByPrimaryKeySelective(entity);
                return this.setResultSuccess();
            }else{
                AddressEntity entity = new AddressEntity();
                entity.setId(address.getId());
                entity.setMoren(true);
                addressMapper.updateByPrimaryKeySelective(entity);
                return this.setResultSuccess();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("修改默认地址错误");
    }

    @Override
    public Result<JSONObject> onComplie(Integer id, String token) {
        AddressEntity addressEntity = addressMapper.selectByPrimaryKey(id);
        return this.setResultSuccess(addressEntity);
    }
}
