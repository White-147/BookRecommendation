package com.hytc.recommendation_data.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author White Jiang
 * @since 2023-02-22
 */
@Data
@ApiModel(value = "Recommend对象", description = "")
public class Recommend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户学号")
    private String certId;
    @ApiModelProperty("推荐图书编号")
    private String callNo;
    @ApiModelProperty("推荐值")
    private Double recommend;

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public Double getRecommend() {
        return recommend;
    }

    public void setRecommend(Double recommend) {
        this.recommend = recommend;
    }

    @Override
    public String toString() {
        return "Recommend{" +
            "certId=" + certId +
            ", callNo=" + callNo +
            ", recommend=" + recommend + "}";
    }
}