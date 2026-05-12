package com.hytc.recommendation_data.library.model;

import com.hytc.recommendation_data.common.model.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: White Jiang
 * @Date: 2023/2/13 14:14
 * @Description:
 */
@Data
public class BookExtendDTO extends PageDTO {
    @ApiModelProperty("书名")
    private String mTitle;

    @ApiModelProperty("学号")
    private String certId;

}
