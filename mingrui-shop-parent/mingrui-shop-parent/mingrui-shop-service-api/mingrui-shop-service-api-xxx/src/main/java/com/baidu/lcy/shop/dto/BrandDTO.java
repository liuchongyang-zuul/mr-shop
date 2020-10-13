package com.baidu.lcy.shop.dto;

import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandDTO
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@ApiModel(value = "品牌DTO")
@Data
public class BrandDTO extends BaseDTO{
    @Id
    @ApiModelProperty(value = "品牌主键")
    @NotNull(message = "主键不能为空",groups = {MingRuiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotEmpty(message = "名称不能为空",groups = {MingRuiOperation.Add.class})
    private String name;

    @ApiModelProperty(value = "品牌image")
    private String image;

    @ApiModelProperty(value = "品牌letter")
    private Character letter;

    @ApiModelProperty(value = "品牌分类信息")
    @NotEmpty(message = "品牌分类信息不能为空", groups = {MingRuiOperation.Add.class})
    private String category;

}
