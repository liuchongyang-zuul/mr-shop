package com.baidu.lcy.shop.dto;


import com.baidu.lcy.shop.validate.group.MingRuiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/10/13
 * @Version V1.0
 **/
@Data
@ApiModel(value = "用户DTO")
public class UserDTO {

    @ApiModelProperty(value = "用户主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingRuiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "帐号")
    @NotNull(message = "帐号不能为空",groups = {MingRuiOperation.Add.class})
    private String username;

    @ApiModelProperty(value = "密码")

    @NotNull(message = "密码不能为空",groups = {MingRuiOperation.Add.class})
    private String password;

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空",groups = {MingRuiOperation.Add.class})
    private String phone;

    private Date created;

    private String salt;
}
