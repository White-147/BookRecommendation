package com.hytc.recommendation_data.library.model;

import com.hytc.recommendation_data.common.model.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: White Jiang
 * @Date: 2023/2/9 19:38
 * @Description: 用户收藏分页查询条件
 */
@Data
public class CollectDTO extends PageDTO {
    @ApiModelProperty("学号")
    private String certId;
}
