package com.cxytiandi.encrypt.core;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cxytiandi.encrypt.util.AesEncryptUtils;

/**
 * 数据加解密过滤器
 * 
 * @author yinjihuan
 * 
 * @date 2019-01-12
 * 
 * @about http://cxytiandi.com/about
 *
 */
public class EncryptionFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(EncryptionFilter.class);
	
	private EncryptionConfig encryptionConfig;
	
	public EncryptionFilter() {
		this.encryptionConfig = new EncryptionConfig();
	}
	
	public EncryptionFilter(EncryptionConfig config) {
		this.encryptionConfig = config;
	}
	
	public EncryptionFilter(String key, List<String> responseEncryptUriList, List<String> requestDecyptUriList,
			String responseCharset, boolean debug) {
		this.encryptionConfig = new EncryptionConfig(key, responseEncryptUriList, requestDecyptUriList, responseCharset, debug);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String uri = req.getRequestURI();
		logger.debug("RequestURI: {}", uri);
		
		// 调试模式不加解密
		if (encryptionConfig.isDebug()) {
			chain.doFilter(req, resp);
			return;
		}
		
		boolean decryptionStatus = encryptionConfig.getRequestDecyptUriList().contains(uri);
		boolean encryptionStatus = encryptionConfig.getResponseEncryptUriList().contains(uri);
		
		// 没有配置具体加解密的URI默认全部都开启加解密
		if (encryptionConfig.getRequestDecyptUriList().size() == 0 
				&& encryptionConfig.getResponseEncryptUriList().size() == 0) {
			decryptionStatus = true;
			encryptionStatus = true;
		}
		
		// 没有加解密操作
		if (decryptionStatus == false && encryptionStatus == false) {
			chain.doFilter(req, resp);
			return;
		}
		
		
		EncryptionResponseWrapper responseWrapper = null;
		EncryptionReqestWrapper reqestWrapper = null;
		// 配置了需要解密才处理
		if (decryptionStatus) {
			reqestWrapper = new EncryptionReqestWrapper(req);
			String requestData = reqestWrapper.getRequestData();
			logger.debug("RequestData: {}", requestData);
			try {
				String decyptRequestData = AesEncryptUtils.aesDecrypt(requestData, encryptionConfig.getKey());
				logger.debug("DecyptRequestData: {}", decyptRequestData);
				reqestWrapper.setRequestData(decyptRequestData);
			} catch (Exception e) {
				logger.error("请求数据解密失败", e);
				throw new RuntimeException(e);
			}
		}
		
		if (encryptionStatus) {
			responseWrapper = new EncryptionResponseWrapper(resp);
		}
		
		// 同时需要加解密
		if (encryptionStatus && decryptionStatus) {
			chain.doFilter(reqestWrapper, responseWrapper);
		} else if (encryptionStatus) { //只需要响应加密
			chain.doFilter(req, responseWrapper);
		} else if (decryptionStatus) { //只需要请求解密
			chain.doFilter(reqestWrapper, resp);
		}
		
		// 配置了需要加密才处理
		if (encryptionStatus) {
			String responeData = responseWrapper.getResponseData();
			logger.debug("ResponeData: {}", responeData);
			ServletOutputStream out = null;
			try {
				responeData = AesEncryptUtils.aesEncrypt(responeData, encryptionConfig.getKey());
				logger.debug("EncryptResponeData: {}", responeData);
				response.setCharacterEncoding(encryptionConfig.getResponseCharset());
		        out = response.getOutputStream();
		        out.write(responeData.getBytes(encryptionConfig.getResponseCharset()));
			} catch (Exception e) {
				logger.error("响应数据加密失败", e);
				throw new RuntimeException(e);
			} finally {
				if (out != null) {
					out.flush();
				    out.close();
				}
			}
			
		} 
		
	}

	@Override
	public void destroy() {
		
	}
}
