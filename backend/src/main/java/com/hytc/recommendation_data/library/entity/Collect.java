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
 * @since 2023-02-09
 */
@Data
@ApiModel(value = "Collect对象", description = "用户收集对象")
public class Collect implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("学号")
    private String certId;

    @ApiModelProperty("图书编号")
    private String callNo;

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

    @Override
    public String toString() {
        return "Collect{" +
            "certId=" + certId +
            ", callNo=" + callNo +
        "}";
    }
}
