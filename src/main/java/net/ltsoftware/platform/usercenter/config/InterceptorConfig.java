package net.ltsoftware.platform.usercenter.config;

import net.ltsoftware.platform.usercenter.interceptor.WebSecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebSecurityInterceptor()).addPathPatterns("/**");

    }
}