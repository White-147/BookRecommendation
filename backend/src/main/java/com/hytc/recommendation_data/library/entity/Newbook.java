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
 * @since 2023-03-02
 */
@Data
@ApiModel(value = "NewBook对象", description = "新图速递对象")
public class Newbook implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("图书编号")
    private String callNo;

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    @Override
    public String toString() {
        return "Newbook{" +
                "callNo=" + callNo + "}";
    }
}