package com.cxytiandi.encrypt.springboot.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cxytiandi.encrypt.algorithm.EncryptAlgorithm;
import com.cxytiandi.encrypt.core.EncryptionConfig;
import com.cxytiandi.encrypt.core.EncryptionFilter;
import com.cxytiandi.encrypt.springboot.init.ApiEncryptDataInit;


/**
 * 加解密自动配置
 * 
 * @author yinjihuan
 * 
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(EncryptionConfig.class)
public class EncryptAutoConfiguration {

	@Autowired
	private EncryptionConfig encryptionConfig;
	
	@Autowired(required=false)
	private EncryptAlgorithm encryptAlgorithm;
	
	/**
	 * 不要用泛型注册Filter,泛型在Spring Boot 2.x版本中才有
	 * @return 过滤器
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        if (encryptAlgorithm != null) {
        	registration.setFilter(new EncryptionFilter(encryptionConfig, encryptAlgorithm));
		} else {
			registration.setFilter(new EncryptionFilter(encryptionConfig));
		}
        registration.addUrlPatterns(encryptionConfig.getUrlPatterns());
        registration.setName("EncryptionFilter");
        registration.setOrder(encryptionConfig.getOrder());
        return registration;
    }
	
	@Bean
	public ApiEncryptDataInit apiEncryptDataInit() {
		return new ApiEncryptDataInit();
	}
}
