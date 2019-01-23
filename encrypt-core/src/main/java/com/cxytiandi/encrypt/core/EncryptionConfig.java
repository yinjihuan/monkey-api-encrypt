package com.cxytiandi.encrypt.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加解密配置类
 * 
 * @author yinjihuan
 * 
 * @date 2019-01-12
 * 
 * @about http://cxytiandi.com/about
 *
 */
@ConfigurationProperties(prefix = "spring.encrypt")
public class EncryptionConfig {

	/**
	 * AES加密Key
	 */
	private String key = "d7b85f6e214abcda";
	
	/**
	 * 需要对响应内容进行加密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	private List<String> responseEncryptUriList = new ArrayList<String>();
	
	/**
	 * 需要对请求内容进行解密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	private List<String> requestDecyptUriList = new ArrayList<String>();

	/**
	 * 响应数据编码
	 */
	private String responseCharset = "UTF-8";
	
	/**
	 * 开启调试模式，调试模式下不进行加解密操作，用于像Swagger这种在线API测试场景
	 */
	private boolean debug = false;
	
	/**
	 * 过滤器拦截模式
	 */
	private String[] urlPatterns = new String[] { "/*" };
	
	/**
	 * 过滤器执行顺序
	 */
	private int order = 1;
	
	public EncryptionConfig() {
		super();
	}
	
	public EncryptionConfig(String key, List<String> responseEncryptUriList, List<String> requestDecyptUriList,
			String responseCharset, boolean debug) {
		super();
		this.key = key;
		this.responseEncryptUriList = responseEncryptUriList;
		this.requestDecyptUriList = requestDecyptUriList;
		this.responseCharset = responseCharset;
		this.debug = debug;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getResponseEncryptUriList() {
		return responseEncryptUriList;
	}

	public void setResponseEncryptUriList(List<String> responseEncryptUriList) {
		this.responseEncryptUriList = responseEncryptUriList;
	}

	public List<String> getRequestDecyptUriList() {
		return requestDecyptUriList;
	}

	public void setRequestDecyptUriList(List<String> requestDecyptUriList) {
		this.requestDecyptUriList = requestDecyptUriList;
	}

	public String getResponseCharset() {
		return responseCharset;
	}

	public void setResponseCharset(String responseCharset) {
		this.responseCharset = responseCharset;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setUrlPatterns(String[] urlPatterns) {
		this.urlPatterns = urlPatterns;
	}
	
	public String[] getUrlPatterns() {
		return urlPatterns;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
}
