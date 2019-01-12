package com.cxytiandi.encrypt_springboot_example.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cxytiandi.encrypt.core.EncryptionConfig;
import com.cxytiandi.encrypt.core.EncryptionFilter;
import com.cxytiandi.encrypt_springboot_example.algorithm.RsaEncryptAlgorithm;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<EncryptionFilter> filterRegistration() {
    	EncryptionConfig config = new EncryptionConfig();
    	config.setRequestDecyptUriList(Arrays.asList("/save", "/decryptEntityXml"));
    	config.setResponseEncryptUriList(Arrays.asList("/encryptStr", "/encryptEntity", "/save", "/encryptEntityXml", "/decryptEntityXml"));
        FilterRegistrationBean<EncryptionFilter> registration = new FilterRegistrationBean<EncryptionFilter>();
        registration.setFilter(new EncryptionFilter(config));
        //registration.setFilter(new EncryptionFilter(config, new RsaEncryptAlgorithm()));
        registration.addUrlPatterns("/*");
        registration.setName("EncryptionFilter");
        registration.setOrder(1);
        return registration;
    }

}