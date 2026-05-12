package com.hytc.recommendation_data.library.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 学校学生信息
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-26
 */
@Data
@ApiModel(value = "Reader对象", description = "读者对象")
public class Reader implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "CERT_ID")
    @ApiModelProperty("学号")
    private String certId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("学院班级")
    private String dept;

    @ApiModelProperty("注册时间")
    private String redrRegD;

    @ApiModelProperty("注册类型")
    private String redrType;

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getRedrRegD() {
        return redrRegD;
    }

    public void setRedrRegD(String redrRegD) {
        this.redrRegD = redrRegD;
    }

    public String getRedrType() {
        return redrType;
    }

    public void setRedrType(String redrType) {
        this.redrType = redrType;
    }

    @Override
    public String toString() {
        return "Reader{" +
                "certId=" + certId +
                ", name=" + name +
                ", dept=" + dept +
                ", redrRegD=" + redrRegD +
                ", redrType=" + redrType + "}";
    }
}