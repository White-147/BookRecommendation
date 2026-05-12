package com.hytc.recommendation_data.library.model;

import com.hytc.recommendation_data.common.model.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: White Jiang
 * @Date: 2023/1/29 18:50
 * @Description: 图书分页查询条件
 */
@Data
public class BookQueryDTO extends PageDTO {
    @ApiModelProperty("书名")
    private String mTitle;

    @ApiModelProperty("图书类别")
    private String mCallNo;
}
