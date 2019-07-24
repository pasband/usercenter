package net.ltsoftware.usercenter.config;

import net.ltsoftware.usercenter.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthenticationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/**","/token/**","/error","/phone/**","/wxmp/**","/pay/**");

    }

    @Bean
    public AuthenticationInterceptor getAuthenticationInterceptor(){
//        List<String> passPhoneBindUrls =
//                asList("/user/info","/phone/");
        AuthenticationInterceptor au = new AuthenticationInterceptor();
//        au.setPassPhoneBind(passPhoneBindUrls);
        return au;
    }

}