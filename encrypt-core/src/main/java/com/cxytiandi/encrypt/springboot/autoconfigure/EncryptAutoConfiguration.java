package com.cxytiandi.encrypt.springboot.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cxytiandi.encrypt.core.EncryptionConfig;
import com.cxytiandi.encrypt.core.EncryptionFilter;


/**
 * 加解密自动配置
 * 
 * @author yinjihuan
 * 
 * @about http://cxytiandi.com/about
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(EncryptionConfig.class)
public class EncryptAutoConfiguration {

	@Autowired
	private EncryptionConfig encryptionConfig;
	
	/**
	 * 不要用泛型注册Filter,泛型在Spring Boot 2.x版本中才有
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
    public FilterRegistrationBean filterRegistration() {
    	EncryptionConfig config = new EncryptionConfig();
    	config.setKey(encryptionConfig.getKey());
    	config.setRequestDecyptUriList(encryptionConfig.getRequestDecyptUriList());
    	config.setResponseEncryptUriList(encryptionConfig.getResponseEncryptUriList());
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new EncryptionFilter(config));
        registration.addUrlPatterns(encryptionConfig.getUrlPatterns());
        registration.setName("EncryptionFilter");
        registration.setOrder(encryptionConfig.getOrder());
        return registration;
    }
	
}
