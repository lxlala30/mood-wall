package com.example.tool.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置（让前端能访问上传的图片）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射/uploads目录为静态资源
        String uploadPath = "file:" + System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }

    // 通过实现 WebMvcConfigurer 接口自定义 MVC 配置，适合需要批量配置多个规则的场景
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        logger.info("addViewControllers() -> index path:{}", "http://118.25.94.106:8082/index.html");
        // 把根路径 "/" 映射到 tool_server.html
        registry.addViewController("/no").setViewName("forward:/nono.html");
        // 可选：设置优先级（数字越小优先级越高）
        registry.setOrder(0);
    }
}
