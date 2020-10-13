package com.baidu.lcy.shop.dto;

import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName SpecParamDTO
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/3
 * @Version V1.0
 **/
@Data
@ApiModel(value = "规格参DTO")
public class SpecParamDTO {
    @ApiModelProperty(value = "主键", example = "1")
    @NotNull(message = "主键不能为空", groups = {MingRuiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "分类id", example = "1")
    private Integer cid;

    @ApiModelProperty(value = "规格组id", example = "1")
    private Integer groupId;

    @ApiModelProperty(value = "规格参数名称")
    private String name;

    @ApiModelProperty(value = "是否是数字类型参数", example = "0")
    @NotNull(message = "是否是数字类型参数不能为空",groups = {MingRuiOperation.Add.class,MingRuiOperation.Update.class})
    private Boolean numeric;

    @ApiModelProperty(value = "非数字类型可以为空")
    private String unit;

    @ApiModelProperty(value = "是否是sku通用属性", example = "0")
    @NotNull(message = "是否是sku通用属性不能为空",groups = {MingRuiOperation.Add.class,MingRuiOperation.Update.class})
    private Boolean generic;

    @ApiModelProperty(value = "是否用于搜索过滤", example = "0")
    @NotNull(message = "是否用于搜索过滤不能为空",groups = {MingRuiOperation.Add.class,MingRuiOperation.Update.class})
    private Boolean searching;

    @ApiModelProperty(value = "数值类型参数,如果需要搜索,则添加分段间隔值,如CPU频率间隔：1-2")
    private String segments;
}
