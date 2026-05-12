package com.hytc.recommendation_data.library.model;

import com.hytc.recommendation_data.common.model.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: White Jiang
 * @Date: 2023/1/30 16:55
 * @Description: 借阅分页查询条件
 */
@Data
public class LendQueryDTO extends PageDTO {
    @ApiModelProperty("学号")
    private String certId;
}
