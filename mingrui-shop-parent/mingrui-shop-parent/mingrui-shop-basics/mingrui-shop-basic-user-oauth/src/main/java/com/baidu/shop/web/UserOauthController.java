package com.baidu.shop.web;

import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.utils.ObjectUtil;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
@Api(tags = "用户认证接口")
public class UserOauthController extends BaseApiService {

    @Autowired
    private UserOauthService userOauthService;

    @Autowired
    private JwtConfig jwtConfig;


    @PostMapping(value = "login")
    @ApiOperation(value = "用户登陆")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity, HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse) throws Exception {

        String token = userOauthService.login(userEntity,jwtConfig);

        if (ObjectUtil.isNull(token)) {
            return this.setResultError("用户名或密码错误");
        }

        CookieUtils.setCookie(httpServletRequest,httpServletResponse,

        jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge());

        return this.setResultSuccess();
    }

    @GetMapping(value = "verify")
    public Result<UserInfo> verify(@CookieValue(value = "MRSHOP_TOKEN") String token,
                                   HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            String s = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());

            CookieUtils.setCookie(httpServletRequest,httpServletResponse,jwtConfig.getCookieName(),s,jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {//如果有异常 说明token有问题
            //e.printStackTrace();
            //应该新建http状态为用户验证失败,状态码为403
            return this.setResultError(403,"用户失效");
        }
    }
}
