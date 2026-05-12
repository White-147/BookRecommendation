package com.hytc.recommendation_data.sys.entity;

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
 * @since 2023-02-05
 */
@Data
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "account")
    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("登陆时间")
    private String time;

    @ApiModelProperty("头像")
    private String head;

    @ApiModelProperty("用户学号")
    private String certId;

    @ApiModelProperty("登录状态")
    private Integer status;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getCertId() {
        return certId;
    }

    public String getHead() {
        return head;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "account=" + account +
                ", password=" + password +
                ", username=" + username +
                ", time=" + time +
                ", head=" + head +
                ", certId=" + certId +
                ", status=" + status +
                "}";
    }
}
