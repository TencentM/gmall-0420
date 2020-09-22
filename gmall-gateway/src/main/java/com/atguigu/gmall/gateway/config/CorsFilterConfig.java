package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsWebFilter getCorsWebFilter(){

        /*允许跨域访问的域名*/
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://api.gmall.com");
        corsConfiguration.addAllowedOrigin("http://www.gmall.com");
        corsConfiguration.addAllowedOrigin("http://manager.gmall.com");
        /*是否允许携带cookie*/
        corsConfiguration.setAllowCredentials(true);
        /*允许跨域访问的请求方式*/
        corsConfiguration.addAllowedMethod("*");
        /*允许携带的头信息*/
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(configurationSource);
    }
}
