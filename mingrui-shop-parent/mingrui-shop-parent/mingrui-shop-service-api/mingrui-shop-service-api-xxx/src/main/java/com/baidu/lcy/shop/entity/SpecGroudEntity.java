package com.baidu.lcy.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName SpecGroudEntity
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/3
 * @Version V1.0
 **/

@Data
@Table(name = "tb_spec_group")
public class SpecGroudEntity {
    @Id
    private Integer id;

    private Integer cid;

    private String name;
}
