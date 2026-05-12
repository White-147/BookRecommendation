package com.hytc.recommendation_data.sys.service.impl;

import com.hytc.recommendation_data.sys.entity.User;
import com.hytc.recommendation_data.sys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/2/4 19:57
 * @Description: 认证校验的方法
 */

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    IUserService userService;

    //完成账号的校验
    @Override
    public UserDetails loadUserByUsername(String account)
            throws UsernameNotFoundException {
        // 1.需要根据账号查询
        User user = userService.queryByAccount(account);
        if (user != null) {
            List<GrantedAuthority> list = new ArrayList<>();
            list.add(new SimpleGrantedAuthority("ADMIN"));
            return new org.springframework.security.core.userdetails.User
                    (user.getAccount(),
                            new BCryptPasswordEncoder().encode(user.getPassword()), list);
        }
        return null;
    }
}