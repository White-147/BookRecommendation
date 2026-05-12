package com.hytc.recommendation_data.library.entity;

import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-01-29
 */
@Data
@ApiModel(value = "Lend对象", description = "图书借阅对象")
public class Lend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "CERT_ID")
    @ApiModelProperty("学号")
    private String certId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("图书编号")
    private String mCallNo;

    @ApiModelProperty("书名")
    private String mTitle;

    @ApiModelProperty("作者")
    private String mAuthor;

    @ApiModelProperty("出版社")
    private String mPublisher;

    @ApiModelProperty("借阅日期")
    private String lendDate;

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

    public String getmCallNo() {
        return mCallNo;
    }

    public void setmCallNo(String mCallNo) {
        this.mCallNo = mCallNo;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmPublisher() {
        return mPublisher;
    }

    public void setmPublisher(String mPublisher) {
        this.mPublisher = mPublisher;
    }

    public String getLendDate() {
        return lendDate;
    }

    public void setLendDate(String lendDate) {
        this.lendDate = lendDate;
    }

    @Override
    public String toString() {
        return "Lend{" +
                "certId=" + certId +
                ", name=" + name +
                ", mCallNo=" + mCallNo +
                ", mTitle=" + mTitle +
                ", mAuthor=" + mAuthor +
                ", mPublisher=" + mPublisher +
                ", lendDate=" + lendDate +
                "}";
    }
}
