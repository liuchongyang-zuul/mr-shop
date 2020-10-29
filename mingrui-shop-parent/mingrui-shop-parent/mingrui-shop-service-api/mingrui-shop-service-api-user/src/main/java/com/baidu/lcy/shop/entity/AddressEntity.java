package com.baidu.lcy.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName AddressEntity
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/28
 * @Version V1.0
 **/
@Table(name = "tb_address")
@Data
public class AddressEntity {
    @Id
    private Integer id;

    private Integer userId;

    private String name;

    private Integer phone;

    private String state;

    private String city;

    private String district;

    private String address;

    private Integer zipCode;

    private Boolean moren;
}
