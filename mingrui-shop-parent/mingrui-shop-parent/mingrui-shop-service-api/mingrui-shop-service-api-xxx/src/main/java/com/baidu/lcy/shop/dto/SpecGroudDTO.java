package com.baidu.lcy.shop.dto;

import com.baidu.lcy.shop.entity.SpecParamEntity;
import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName SpecGroudDTO
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/3
 * @Version V1.0
 **/
@Data
@ApiModel(value = "规格组数据传输DTO")
public class SpecGroudDTO {

    @ApiModelProperty(value = "主键", example = "1")
    @NotNull(message = "主键不能为空",groups = {MingRuiOperation.Update.class})
    private Integer id;
    @ApiModelProperty(value = "类型id", example = "1")
    @NotNull(message = "类型id不能为空",groups = {MingRuiOperation.Add.class})
    private Integer cid;
    @ApiModelProperty(value = "规格组名称")
    @NotNull(message = "规格组名称不能为空",groups = {MingRuiOperation.Add.class})
    private String name;
    @ApiModelProperty(hidden = true)
    private List<SpecParamEntity> paramList;

}
