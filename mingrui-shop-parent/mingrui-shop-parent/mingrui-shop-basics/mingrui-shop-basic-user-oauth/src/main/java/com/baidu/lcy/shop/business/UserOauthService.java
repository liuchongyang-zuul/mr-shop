package com.baidu.lcy.shop.business;

import com.baidu.lcy.shop.entity.UserEntity;
import com.baidu.lcy.shop.config.JwtConfig;

public interface UserOauthService {
    String login(UserEntity userEntity, JwtConfig jwtConfig) throws Exception;
}
