package com.hytc.recommendation_data.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author White Jiang
 * @since 2023-03-01
 */
@Data
@ApiModel(value = "RelatedBook对象", description = "相似图书对象")
public class RelatedBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("被推荐图书编号")
    private String callNo1;

    @ApiModelProperty("推荐图书编号")
    private String callNo2;

    public String getCallNo1() {
        return callNo1;
    }

    public void setCallNo1(String callNo1) {
        this.callNo1 = callNo1;
    }
    public String getCallNo2() {
        return callNo2;
    }

    public void setCallNo2(String callNo2) {
        this.callNo2 = callNo2;
    }

    @Override
    public String toString() {
        return "Relatedbook{" +
            "callNo1=" + callNo1 +
            ", callNo2=" + callNo2 +
        "}";
    }
}
