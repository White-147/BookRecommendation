package com.hytc.recommendation_data.library.model;

import com.hytc.recommendation_data.common.model.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: White Jiang
 * @Date: 2023/2/22 15:36
 * @Description: 推荐数据分页查询条件
 */
@Data
public class RecommendDTO extends PageDTO {
    @ApiModelProperty("学号")
    private String certId;
}
