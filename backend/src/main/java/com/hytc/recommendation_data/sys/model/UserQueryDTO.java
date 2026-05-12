package com.hytc.recommendation_data.sys.model;

import com.hytc.recommendation_data.common.model.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: White Jiang
 * @Date: 2023/2/1 11:20
 * @Description: 用户查询条件
 */
@Data
public class UserQueryDTO extends PageDTO {
    @ApiModelProperty("账号")
    private String account;
}
