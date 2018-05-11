package com.cxytiandi.encrypt.auto;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.cxytiandi.encrypt.advice.EncryptResponseBodyAdvice;

@Configuration
@Component
@EnableAutoConfiguration
@EnableConfigurationProperties(EncryptProperties.class)
public class EncryptAutoConfiguration {

	@Bean
	public EncryptResponseBodyAdvice encryptResponseBodyAdvice() {
		return new EncryptResponseBodyAdvice();
	}
	
}
