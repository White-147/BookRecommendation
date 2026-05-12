package com.hytc.recommendation_data.sys.filter;

import com.alibaba.fastjson.JSON;
import com.hytc.recommendation_data.common.constant.SystemConstant;
import com.hytc.recommendation_data.common.util.JWTUtils;
import com.hytc.recommendation_data.sys.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: White Jiang
 * @Date: 2023/2/4 20:45
 * @Description: 登陆过滤器
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public TokenLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /*
    认证的方法
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        User user = null;
        UsernamePasswordAuthenticationToken authenticationToken = null;
        try {
            String loginInfo = getRequestJSON(request);
            user = JSON.parseObject(loginInfo, User.class);
            authenticationToken = new UsernamePasswordAuthenticationToken(user.getAccount(), user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authenticationManager.authenticate(authenticationToken);
    }

    private String getRequestJSON(HttpServletRequest request) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String inputStr = null;
        while ((inputStr = streamReader.readLine()) != null)
            stringBuilder.append(inputStr);
        return stringBuilder.toString();
    }

    /*
    登陆成功的方法
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        Map<String, String> map = new HashMap<>();
        map.put("account", authResult.getName());
        // 生成对应的Token信息
        String token = JWTUtils.getToken(map);
        // 把生成的Token信息响应给用户
        response.addHeader("Authorization", SystemConstant.SYSTEM_TOKEN_PREFIX + token);
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", HttpServletResponse.SC_OK);
        resultMap.put("msg", "认证通过");
        writer.write(JSON.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    /*
    登陆失败的方法
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        resultMap.put("msg", "用户名或密码错误");
        writer.write(JSON.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
}