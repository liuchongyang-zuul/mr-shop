package com.baidu.lcy.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/22
 * @Version V1.0
 **/
public class AlipayConfig {
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766866";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCuPwN86ckgT2avTqRfmOm6sy8hEYLaZIZBx2l9i77QvxUbwGnF9iSoQV2hF450VLEOrfmQli5ph67+vy5eFTUmtGEfqJsc1XJ5GnKbQucCyfBGifytLSGJqrKB+bo4DTtVErlbUcil0QHU0jWQXypATlbWEbbHBhMT9luR4yyOJFVTk+wcj52iBMCEiLyGiPshPJz/8ifryG9fft1TiPtdqhkOinHZXk3plCZGvg+3vGegkYPcKg+sZ+6Q9QdnXm7+MtKXA10nhWN7kwHnqJ98fGqkF1IOq/nIFhmfjMWDEckY4+KWOSLrIp4opKVfTIgbDqptThguZcdnv8iWcqSVAgMBAAECggEBAIMa+KXhRirgc3POaVFgveRzdC+efGWUo4wU3ePP5Rgt3lUONsfzx2suG6PdFJulKb/Q2WmYBn3y0JHvCZVSwbZInJd0hplwZPyIENmPwj/P79EzfAJVZdmurTeszhfkpNMDDqKOa7mQBJ+8Nv9VC3ZtO1tA4tTnVi17/TRMyTZ9Ec+HDQR1ZmeDYTeIMkL3P2m5Wi+cx4bchbA4hkJykqya64MICAiLW/gJPfzRaF/gbtCV7zyVdMM98dk3UXl4FbhlHagc0bV7qbqJ5tBBC/rz0cC0Rl4UoEAmqgXpasrpJw9rap5CWFkKv3/YGZNN75cuWmthjjdnFAgrdjNo4IECgYEA7v7D1BxnxOlMwm9XYtTsXRP00W4+2vVdY9pq/hnhX0B4EICKfWHlc+E0NQbEJ/Qjj/YOGU4fgR3qFjQTZq0cK1PlsrjNOI43yFN06WukN/Xa+TV1brJuYdp8+Bu4yxSSBbLK8QNy2DLF4o3uXb7WTayElQhl2dM2n2IxYKZD92ECgYEAuqTcp4nxL4D3yEiHnocPLVf86F86eirLmtK/RKWzsJ1Qedrni0Mo+315Wtce4OOT8swFVd57BPYsX7XqV0INEM0d2EkvlkQaVVWriDicaVUissklj45/+y0KCnIqBOgN1x0CKwzxK7wsHc9DS59H26OM7OgfnQop29RShQmW3bUCgYAB1KW/XDBXS8owuxWaRjNBS0PWztdgafy7JRMoux4E0sOYrMNiznspdhImTdT20WHsWmAlRXnsTURbwy5a3PyYQF5EDjJUOpKm0CzXFOH/I7z3p7csj+VUjo3igKPbul6b+JChY/tfOJRXderbgPi3Q5kIYzYUgw+qDOB/8wYWQQKBgHfP2O2buT27VlQ1awmyvWVoNCue5dUCn8z1OF8C18SqV7zBbx2QsUAvnbVYnrPSo2ia21nyBZp7xKR0DMiS1/xPD7eF04xsj1L0ObaqUI21Xx7RzNDIsxM0KFvoSVz48dmC/59PIYnns5I3OIXU67Xs2UxC/fg7S/+X3pkUPMp5AoGBAKfCpD98++ueK4A3GkmLiinhdDgYsxoTkZ7MVGm+nPlVF8crGa5Okxq7FVraib49qBWAaS7i3gVEEh3YA7CBlISDmlc1Z4T9pzBQAuAml3fQ4nLhAEpdJFxsx4YAFXqEOYu/kb+SNJlRUcUCyyS/rzVEFb/+N6b5dQj/UAnSyu9M";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAouKYfXADyQgVA83mrWrAxvmhONVzHA87Wj9eyTHyEoOd8K7UelxShQDvVh5zy2pwLMvxfEsGnUP6T5ZbrFfxUGsB4hYz3E6y+dc0p3EL8j43yCBN5nJn4bG27LPlMGeoNgb/qwg7qE0bMJ1upKM9ueUNNdkf8fEtoX1BTXxBBs2/ov+VnpbiHRSUZ3jsXICt46sTkLVUIj0VB/wLtYAjcoYNGqe0AWZUMk8hQD/PnGyYUyQNf1PLEwuEtpQ256DsfzQ1wygESYFK9xDgOZxfavB4x/1wCvrT9RmA7JltifPIN8KGMbdIxyhXwoU6F/iy3QamrGuHQ+cwe6v/WSTZEQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/returnURL";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
