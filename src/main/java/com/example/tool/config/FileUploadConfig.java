package com.example.tool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 文件上传配置
 */
@Configuration
public class FileUploadConfig {
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        // 设置最大上传文件大小 10MB
        resolver.setMaxUploadSize(10 * 1024 * 1024);
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }
}
