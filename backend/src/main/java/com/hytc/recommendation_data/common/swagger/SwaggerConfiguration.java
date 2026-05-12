package com.hytc.recommendation_data.common.swagger;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger配置
 */
@Slf4j
@Configuration
@EnableOpenApi
public class SwaggerConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description(applicationName + "接口文档")
                .contact(new Contact("White Jiang", "", "1004522750@qq.com"))
                .version("3.1")
                .build();
    }
    
    @Bean
    public EnumPropertyBuilderPlugin enumPropertyBuilderPlugin() {
        return new EnumPropertyBuilderPlugin();
    }

    @Bean
    public ValidatePropertyBuilderPlugin validatePropertyBuilderPlugin() {
        return new ValidatePropertyBuilderPlugin();
    }

    @Bean
    public SwaggerConsole swaggerConsole() {
        return new SwaggerConsole();
    }
}
