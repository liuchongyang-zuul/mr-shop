package com.baidu.shop.business.impl;

import com.baidu.lcy.shop.utils.BCryptUtil;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserOauthMapper;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName UserOauthServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/15
 * @Version V1.0
 **/
@Service
public class UserOauthServiceImpl implements UserOauthService {

    @Resource
    private UserOauthMapper userOauthMapper;

    @Override
    public String login(UserEntity userEntity, JwtConfig jwtConfig) throws Exception {

        String token = null;

        Example example = new Example(UserEntity.class);
        example.createCriteria().andEqualTo("username",userEntity.getUsername());
        List<UserEntity> list = userOauthMapper.selectByExample(example);

        if(list.size() == 1){
            UserEntity user = list.get(0);
            if (BCryptUtil.checkpw(userEntity.getPassword(),user.getPassword())) {

                token = JwtUtils.generateToken(new UserInfo(user.getId(),user.getUsername()),jwtConfig.getPrivateKey(),jwtConfig.getExpire());
            }
        }

        return token;
    }
}
