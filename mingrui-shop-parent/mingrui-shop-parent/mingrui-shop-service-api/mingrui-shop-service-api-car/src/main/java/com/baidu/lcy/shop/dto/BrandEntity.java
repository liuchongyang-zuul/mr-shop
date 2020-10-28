package com.baidu.lcy.shop.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName BrandEntity
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Table(name = "tb_brand")
@ApiModel(value = "品牌实体类")
@Data
public class BrandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String image;

    private Character letter;
}
