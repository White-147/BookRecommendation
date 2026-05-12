package com.hytc.recommendation_data.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.sys.entity.User;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-25
 */
public interface IUserService extends IService<User> {
    User queryByAccount(String account);

    User queryByCertId(String certId);

    void updateUser(User user);

    void updateUsername(String username,String account);

    void updateUserPic(String imgName,String account);

    void saveUser(User user);
}