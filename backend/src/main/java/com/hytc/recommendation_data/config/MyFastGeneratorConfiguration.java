package com.hytc.recommendation_data.config;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @Author: White Jiang
 * @Date: 2023/1/18 14:08
 * @Description: 快速生成表结构对应的模板代码的配置
 */
public class MyFastGeneratorConfiguration {
    public static void main(String[] args) {
        final String url = "jdbc:mysql://localhost:3306/library?" +
                "serverTimezone=UTC&" +
                "useUnicode=true&" +
                "characterEncoding=utf-8&" +
                "useSSL=true&" +
                "nullCatalogMeansCurrent=true";
        FastAutoGenerator.create(url, "root", "root")
                .globalConfig(builder -> {
                    builder.author("White Jiang") //设置作者
                            .enableSwagger() //开启swagger模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\code\\Java\\recommendation_data\\src\\main\\resources\\code"); //指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.hytc.recommendation_data")
                            .moduleName("library")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\code\\Java\\recommendation_data\\src\\main\\resources\\code"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("newbook");  // 需要生成的表名
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
