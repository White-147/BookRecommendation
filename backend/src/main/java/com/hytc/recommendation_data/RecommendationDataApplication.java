package com.hytc.recommendation_data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.hytc.recommendation_data.library.mapper", "com.hytc.recommendation_data.sys.mapper"})
public class RecommendationDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendationDataApplication.class, args);
    }
}