package com.hytc.recommendation_data.sys.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hytc.recommendation_data.common.constant.SystemConstant;
import com.hytc.recommendation_data.common.util.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: White Jiang
 * @Date: 2023/2/4 22:19
 * @Description: Token合法校验
 */
public class TokenVerifyFilter extends BasicAuthenticationFilter {
    public TokenVerifyFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /*
    校验提交的Token是否合法
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取请求携带的Token信息
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(SystemConstant.SYSTEM_TOKEN_PREFIX)) {
            // 获取到正常的Token
            String token = authorization.replace(SystemConstant.SYSTEM_TOKEN_PREFIX, "");
            // 校验Token信息是否合法
            // 如果未抛出异常则说明Token合法
            DecodedJWT verify = JWTUtils.verify(token);
            if (verify == null) {
                // 校验失败 提示用户先登录
                responseLogin(response);
            }
            // 获取当前登陆的账号信息
            String account = verify.getClaim("account").asString();
            List<GrantedAuthority> list = new ArrayList<>();
            list.add(new SimpleGrantedAuthority("ADMIN"));
            // 根据账号信息获取相关权限
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(account, "", list);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 放开请求
            chain.doFilter(request, response);
        } else {
            // 非法请求
            if (request.getRequestURI().equals("/book_recommendation/library/reader/checkCertId")
                    || request.getRequestURI().equals("/book_recommendation/sys/user/save")
                    || request.getRequestURI().equals("/book_recommendation/sys/user/update")
                    || request.getRequestURI().equals("/book_recommendation/sys/user/selectByAccount")
                    || request.getRequestURI().equals("/book_recommendation/sys/user/selectByCertId")) {
                chain.doFilter(request, response);
            }
//            responseLogin(response);
        }
    }

    private void responseLogin(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", HttpServletResponse.SC_FORBIDDEN);
        resultMap.put("msg", "请先登录");
        writer.write(JSON.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
}