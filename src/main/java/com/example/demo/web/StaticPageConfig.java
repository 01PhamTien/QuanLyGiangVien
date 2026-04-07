package com.example.demo.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticPageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/admin/");
        registry.addResourceHandler("/user/**")
                .addResourceLocations("classpath:/user/");
        registry.addResourceHandler("/layout/**")
                .addResourceLocations("classpath:/layout/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/user");
        registry.addRedirectViewController("/user", "/user/index.html");
        registry.addRedirectViewController("/admin", "/admin/index.html");
    }
}
