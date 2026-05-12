package com.hytc.recommendation_data.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.sys.entity.User;
import com.hytc.recommendation_data.sys.mapper.UserMapper;
import com.hytc.recommendation_data.sys.service.IUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User queryByAccount(String account) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        wrapper.eq("account", account);
        return this.getOne(wrapper);
    }

    @Override
    public User queryByCertId(String certId) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("CERT_ID", certId);
        return this.getOne(wrapper);
    }

    @Override
    public void updateUser(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("account", user.getAccount());
        this.update(user, wrapper);
    }

    @Override
    public void updateUsername(String username,String account) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("account",account);
        User user = this.baseMapper.selectOne(wrapper);
        user.setUsername(username);
        this.update(user,wrapper);
    }

    @Override
    public void updateUserPic(String imgName,String account) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("account",account);
        User user = this.baseMapper.selectOne(wrapper);
        user.setHead(imgName);
        this.update(user,wrapper);
    }

    @Override
    public void saveUser(User user) {
        user.setHead("profile.jpeg");
        user.setTime(LocalDate.now().toString());
        user.setStatus(1);
        this.save(user);
    }
}
