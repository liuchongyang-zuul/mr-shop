package com.baidu.lcy.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.UserDTO;
import com.baidu.lcy.shop.entity.UserEntity;
import com.baidu.lcy.shop.mapper.UserMapper;
import com.baidu.lcy.shop.redis.repository.RedisRepository;
import com.baidu.lcy.shop.service.UserService;
import com.baidu.lcy.shop.utils.BCryptUtil;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
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
}
