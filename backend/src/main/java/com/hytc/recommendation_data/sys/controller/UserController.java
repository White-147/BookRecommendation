package com.hytc.recommendation_data.sys.controller;

import com.hytc.recommendation_data.sys.entity.User;
import com.hytc.recommendation_data.sys.service.IUserService;
import com.hytc.recommendation_data.sys.service.impl.UserDetailServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-25
 */
@Api(tags = "系统用户", value = "User")
@RestController
@RequestMapping("/sys/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailServiceImpl detailService;

    @ApiOperation(value = "查询系统用户列表", notes = "查询用户列表")
    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @ApiOperation(value = "根据账号查询系统用户", notes = "账号查询用户")
    @GetMapping("/selectByAccount")
    public User selectByAccount(@RequestParam("account") String account) {
        User user = userService.queryByAccount(account);
        try {
            user.setPassword(detailService.loadUserByUsername(account).getPassword());
        } catch (NullPointerException ignored) {
        }
        return user;
    }

    @ApiOperation(value = "根据学号查询系统用户", notes = "学号查询用户")
    @GetMapping("/selectByCertId")
    public User selectByCertId(@RequestParam("certId") String certId) {
        return userService.queryByCertId(certId);
    }

    @ApiOperation(value = "更新用户信息", notes = "更新用户")
    @PostMapping("/update")
    public String update(@RequestBody User user) {
        userService.updateUser(user);
        return "success";
    }

    @ApiOperation(value = "更新用户名", notes = "更新用户名")
    @PostMapping("/updateUserName")
    public String updateUsername(@RequestParam("username") String username, @RequestParam("account") String account) {
        userService.updateUsername(username, account);
        return "success";
    }

    @ApiOperation(value = "更新用户头像", notes = "更新用户头像")
    @PostMapping("/updateUserPic")
    public String updateUserPic(@RequestParam("img") String img, @RequestParam("account") String account) {
        userService.updateUserPic(img, account);
        return "success";
    }

    @ApiOperation(value = "新增用户信息", notes = "新增用户")
    @PostMapping("/save")
    public String save(@RequestBody User user) {
        userService.saveUser(user);
        return "success";
    }

    @ApiOperation(value = "上传用户头像", notes = "上传图片")
    @PostMapping("/upload")
    public String imgUpLoad(MultipartFile file, HttpServletRequest req) {
        String filePath = "D:\\code\\VueProjects\\book_recommendation\\src\\assets\\image\\head\\";
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String account = req.getHeader("Account");
        String newName = account + "_head.jpg";
        try {
            file.transferTo(new File(folder, newName));
            userService.updateUserPic(newName, account);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}