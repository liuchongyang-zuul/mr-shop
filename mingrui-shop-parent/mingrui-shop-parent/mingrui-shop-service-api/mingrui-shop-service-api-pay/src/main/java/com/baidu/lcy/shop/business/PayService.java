package com.baidu.lcy.shop.business;

import com.baidu.lcy.shop.dto.PayInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName PayService
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/22
 * @Version V1.0
 **/
@Api(tags = "支付接口")
public interface PayService {


    @ApiOperation(value = "请求支付")
    @GetMapping(value = "requestPay")
    void requestPay(PayInfoDTO payInfoDTO, HttpServletResponse response);

    @ApiOperation(value = "通知接口,这个可能暂时测试不了")
    @GetMapping(value = "returnNotify")
    void returnNotify(HttpServletRequest httpServletRequest);

    @ApiOperation(value = "跳转成功页面接口")
    @GetMapping(value = "returnURL")
    void returnURL(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse);
}
