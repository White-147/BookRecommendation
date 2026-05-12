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
@ApiModel(value = "Book对象", description = "图书对象")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "M_CALL_NO")
    @ApiModelProperty("图书编号")
    private String mCallNo;

    @ApiModelProperty("书名")
    private String mTitle;

    @ApiModelProperty("作者")
    private String mAuthor;

    @ApiModelProperty("出版社")
    private String mPublisher;

    @ApiModelProperty("出版年份")
    private String mPubYear;

    @ApiModelProperty("书籍借出状况")
    private String status;

    @ApiModelProperty("图书封面")
    private String img;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getmPubYear() {
        return mPubYear;
    }

    public void setmPubYear(String mPubYear) {
        this.mPubYear = mPubYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Book{" +
                "mCallNo=" + mCallNo +
                ", mTitle=" + mTitle +
                ", mAuthor=" + mAuthor +
                ", mPublisher=" + mPublisher +
                ", mPubYear=" + mPubYear +
                ", status=" + status +
                "}";
    }
}
