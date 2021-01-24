package cn.itcast.haoke.dubbo.api.config;

import cn.itcast.haoke.dubbo.api.interceptor.RedisCacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器到Spring容器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RedisCacheInterceptor redisCacheInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //把拦截器注册到springmvc中 并设置映射的路径为/** 表示这个拦截器拦截所有请求
        registry.addInterceptor(this.redisCacheInterceptor).addPathPatterns("/**");
    }
}