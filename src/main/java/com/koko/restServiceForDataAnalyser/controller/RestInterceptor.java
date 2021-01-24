package com.koko.restServiceForDataAnalyser.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RestInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(RestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String ipAddress = request.getHeader("X-Forward-For");

        if (ipAddress == null)
            ipAddress = request.getRemoteAddr();

        System.out.println(ipAddress);
        logger.info("Incoming request from: " + ipAddress);
        logger.info("Request for: " + request.getMethod() + " " +  request.getRequestURI());
        return true;
    }
}
