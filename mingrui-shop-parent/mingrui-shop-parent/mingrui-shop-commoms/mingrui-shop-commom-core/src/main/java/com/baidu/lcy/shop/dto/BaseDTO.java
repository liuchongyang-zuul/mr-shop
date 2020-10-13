package com.baidu.lcy.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName BaseDTO
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@ApiModel(value = "用于品牌DTO")
@Data
public class BaseDTO {
    @ApiModelProperty(value = "当前页", example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页显示多少条",example = "5")
    private Integer rows;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "是否升序")
    private String order;
    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){

        if(!StringUtils.isEmpty(sort)) return sort + " " + order.replace("false","asc").replace("true","desc");
        return "";
    }

}
