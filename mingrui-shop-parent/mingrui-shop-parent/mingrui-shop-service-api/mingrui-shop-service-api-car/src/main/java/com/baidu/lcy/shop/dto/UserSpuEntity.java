package com.baidu.lcy.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName UserSpuEntity
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Table(name = "tb_user_spu")
@ApiModel(value = "品牌实体类")
@Data
public class UserSpuEntity {

    @Id
    @ApiModelProperty(value = "关联主键")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "品牌id")
    private Integer brandId;
}

