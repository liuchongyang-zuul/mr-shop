package com.baidu.lcy.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName CityEntity
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/28
 * @Version V1.0
 **/
@Table(name = "t_city")
@Data
public class CityEntity {
    @Id
    private Integer id;

    private String name;

    private Integer parentId;

    private Integer level;
}
