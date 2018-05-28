package com.cxytiandi.encrypt.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
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
	
	private static ThreadLocal<Boolean> encryptLocal = new ThreadLocal<Boolean>();
	
	public static void setEncryptStatus(boolean status) {
		encryptLocal.set(status);
	}
	
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		// 可以通过调用EncryptResponseBodyAdvice.setEncryptStatus(false);来动态设置不加密操作
		Boolean status = encryptLocal.get();
		if (status != null && status == false) {
			encryptLocal.remove();
			return body;
		}
		
		long startTime = System.currentTimeMillis();
		boolean encrypt = false;
		if (returnType.getMethod().isAnnotationPresent(Encrypt.class) && !encryptProperties.isDebug()) {
			encrypt = true;
		}
		if (encrypt) {
			try {
				String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
				if (!StringUtils.hasText(encryptProperties.getKey())) {
					throw new NullPointerException("请配置spring.encrypt.key");
				}
				String result =  AesEncryptUtils.aesEncrypt(content, encryptProperties.getKey());
				long endTime = System.currentTimeMillis();
				logger.debug("Encrypt Time:" + (endTime - startTime));
				return result;
			} catch (Exception e) {
				logger.error("加密数据异常", e);
			}
		}
		
		return body;
	}

}
