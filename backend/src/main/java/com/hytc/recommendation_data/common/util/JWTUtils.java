package com.hytc.recommendation_data.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hytc.recommendation_data.common.constant.SystemConstant;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: White Jiang
 * @Date: 2023/2/4 15:56
 * @Description: JWT操作的工具类
 */

// 生成Token信息
public class JWTUtils {
    public static String getToken(Map<String, String> map) {
        // 设置PayLoad
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        // 设置过期时间
        Calendar calendar = Calendar.getInstance();
        // 默认七天过期
        calendar.add(Calendar.DATE, 7);
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        return builder.withHeader(header)
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(SystemConstant.SYSTEM_TOKEN_PREFIX));
    }

    /*
    校验token是否合法
        如果非法会抛出异常信息
        如果合法会返回 DecodeJET 对象
     */
    public static DecodedJWT verify(String token) {
        // 如果不抛出异常说明验证通过，否则验证失败
        DecodedJWT verify = null;
        try {
            verify = JWT.require(Algorithm.HMAC256(SystemConstant.SYSTEM_TOKEN_PREFIX)).build().verify(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verify;
    }
}