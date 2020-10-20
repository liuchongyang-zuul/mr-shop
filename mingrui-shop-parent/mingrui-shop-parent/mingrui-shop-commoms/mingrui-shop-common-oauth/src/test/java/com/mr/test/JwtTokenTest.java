package com.mr.test;

import com.baidu.lcy.shop.dto.UserInfo;
import com.baidu.lcy.shop.utils.JwtUtils;
import com.baidu.lcy.shop.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @ClassName JwtTokenTest
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/15
 * @Version V1.0
 **/
public class JwtTokenTest {
    //公钥位置
    private static final String pubKeyPath = "D:\\res\\rea.pub";
    //私钥位置
    private static final String priKeyPath = "D:\\res\\rea.pri";
    //公钥对象
    private PublicKey publicKey;
    //私钥对象
    private PrivateKey privateKey;


    /**
     * 生成公钥私钥 根据密文
     * @throws Exception
     */
    @Test
    public void genRsaKey() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "mingrui");
    }


    /**
     * 从文件中读取公钥私钥
     * @throws Exception
     */
    @Before
    public void getKeyByRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 根据用户信息结合私钥生成token
     * @throws Exception
     */
    @Test
    public void genToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1, "zhaojunhao"), privateKey, 2);
        System.out.println("user-token = " + token);
    }


    /**
     * 结合公钥解析token
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ6aGFvanVuaGFvIiwiZXhwIjoxNjAyNzQ0NTc3fQ.AERzQpjhGzPawPGwePmy1n39CTVct-N1a5QszqWvzRviABfWOg37Prn-BSnNGWsXB3EbkHXkl6QnbWM63U8YdpBYrb_9L33t6T0XWHzrYOU1_R9doP76LBdw4ijBPmBvek837-qoz6uMee9romthgoU1-tuGB6-pkVP3XlwMz1o";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}