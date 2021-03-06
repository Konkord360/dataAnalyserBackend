package com.koko.restServiceForDataAnalyser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class InterceptorRegistry implements WebMvcConfigurer {


    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry){
        registry.addInterceptor(new RestInterceptor());
    }


}
