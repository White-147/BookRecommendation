package com.hytc.recommendation_data.config;

import com.hytc.recommendation_data.sys.filter.TokenLoginFilter;
import com.hytc.recommendation_data.sys.filter.TokenVerifyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

/**
 * @Author: White Jiang
 * @Date: 2023/2/4 16:58
 * @Description: SpringSecurity配置
 */
@Configuration
@EnableWebSecurity
public class MySpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, "/**");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] whiteInterfaces = {
                "/library/reader/checkCertId",
                "/sys/user/save",
                "/sys/user/selectByCertId"};
        String[] permitAllInterfaces = {"/sys/user/selectByAccount", "/sys/user/update"};
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(whiteInterfaces).anonymous()
                .antMatchers(permitAllInterfaces).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                // 设置跨域处理
                .cors()
                .configurationSource(configurationSource())
                .and()
                .addFilter(new TokenLoginFilter(super.authenticationManager()))
                .addFilter(new TokenVerifyFilter(super.authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 设置跨域拦截的信息
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}