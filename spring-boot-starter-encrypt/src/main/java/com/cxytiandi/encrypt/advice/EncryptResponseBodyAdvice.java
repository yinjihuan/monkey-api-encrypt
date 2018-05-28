package com.cxytiandi.encrypt.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.cxytiandi.encrypt.anno.Encrypt;
import com.cxytiandi.encrypt.auto.EncryptProperties;
import com.cxytiandi.encrypt.util.AesEncryptUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 请求响应处理类<br>
 * 
 * 对加了@Encrypt的方法的数据进行加密操作
 * 
 * @author yinjihuan
 * 
 * @about http://cxytiandi.com/about
 *
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	private Logger logger = LoggerFactory.getLogger(EncryptResponseBodyAdvice.class);
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private EncryptProperties encryptProperties;
	
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		boolean encrypt = false;
		if (returnType.getMethod().isAnnotationPresent(Encrypt.class) && !encryptProperties.isDebug()) {
			encrypt = true;
		}

		if (encrypt) {
			try {
				String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
				return AesEncryptUtils.aesEncrypt(content, encryptProperties.getKey());
			} catch (Exception e) {
				logger.error("加密数据异常", e);
			}
		}
		
		return body;
	}

}
