package com.baidu.lcy.shop.business.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.business.PayService;
import com.baidu.lcy.shop.config.AlipayConfig;
import com.baidu.lcy.shop.dto.OrderInfo;
import com.baidu.lcy.shop.dto.PayInfoDTO;
import com.baidu.lcy.shop.feign.OrderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName PayServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/22
 * @Version V1.0
 **/
@RestController
public class PayServiceImpl extends BaseApiService implements PayService {

    @Autowired
    private OrderFeign orderFeign;

    @Override
    public void requestPay(PayInfoDTO payInfoDTO, HttpServletResponse response) {

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,
                AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json",
                AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        Result<OrderInfo> orderResult = orderFeign.getOrderInfoByOrderId(payInfoDTO.getOrderId());

        if(orderResult.getCode() == 200){
            OrderInfo orderInfo = orderResult.getData();

            List<String> titleList = orderInfo.getOrderDetailList().stream().map(orderDetail -> orderDetail.getTitle()).collect(Collectors.toList());

            String titleStr  = String.join(",", titleList);
            titleStr = titleStr.length() > 10 ? titleStr .substring(0,10) : titleStr ;

            //商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = payInfoDTO.getOrderId() + "";
            //付款金额，必填
            String total_amount = Double.valueOf(orderInfo.getActualPay()) / 100 + "";
            //订单名称，必填
            String subject = titleStr;
            //商品描述，可空
            String body = "";
            alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                    + "\"total_amount\":\""+ total_amount +"\","
                    + "\"subject\":\""+ subject +"\","
                    + "\"body\":\""+ body +"\","
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

            //请求
            String result = null;

            try {
                result= alipayClient.pageExecute(alipayRequest).getBody();
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out =null;

            try {
                out = response.getWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }

            orderFeign.state(payInfoDTO.getOrderId());

            out.println(result);
        }
    }

    @Override
    public void returnNotify(HttpServletRequest httpServletRequest) {
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = httpServletRequest.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            String trade_status = "";
            try {
                String out_trade_no = new String(httpServletRequest.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
                //支付宝交易号
                String trade_no = new String(httpServletRequest.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
                //付款金额
                trade_status = new String(httpServletRequest.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            }else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }

            //out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
        }else {
            //out.println("验签失败");
        }
    }

    @Override
    public void returnURL(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Map<String,String> params = new HashMap<String,String>();
        Set<Object> objects = new HashSet<>();
        Map<String,String[]> requestParams = httpServletRequest.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);


        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            try {
                String out_trade_no = new String(httpServletRequest.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
                //支付宝交易号
                String trade_no = new String(httpServletRequest.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //付款金额
                String total_amount = new String(httpServletRequest.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

                //修改订单状态
                try {
                    httpServletResponse.sendRedirect("http://www.mrshop.com/success.html?orderId=" + out_trade_no + "&totalPay=" + total_amount);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            //out.println("验签失败");
            //打印错误日志
            //返回错误页面
        }
    }
}
