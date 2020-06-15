package com.cxytiandi.encrypt.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.cxytiandi.encrypt.springboot.init.ApiEncryptDataInit;
import org.springframework.util.CollectionUtils;

/**
 * 加解密配置类
 * 
 * @author yinjihuan
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
	private List<String> requestDecryptUriList = new ArrayList<String>();
	
	/**
	 * 忽略加密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	private List<String> responseEncryptUriIgnoreList = new ArrayList<String>();
	
	/**
	 * 忽略对请求内容进行解密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	private List<String> requestDecryptUriIgnoreList = new ArrayList<String>();

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
	
	public EncryptionConfig(String key, List<String> responseEncryptUriList, List<String> requestDecryptUriList,
			String responseCharset, boolean debug) {
		super();
		this.key = key;
		this.responseEncryptUriList = responseEncryptUriList;
		this.requestDecryptUriList = requestDecryptUriList;
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
		return Stream.of(responseEncryptUriList, ApiEncryptDataInit.responseEncryptUriList).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public void setResponseEncryptUriList(List<String> responseEncryptUriList) {
		this.responseEncryptUriList = responseEncryptUriList;
	}

	public List<String> getRequestDecryptUriList() {
		return Stream.of(requestDecryptUriList, ApiEncryptDataInit.requestDecryptUriList).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public void setRequestDecryptUriList(List<String> requestDecryptUriList) {
		this.requestDecryptUriList = requestDecryptUriList;
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

	public List<String> getResponseEncryptUriIgnoreList() {
		// 配置和注解两种方式合并
		return Stream.of(responseEncryptUriIgnoreList, ApiEncryptDataInit.responseEncryptUriIgnoreList).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public void setResponseEncryptUriIgnoreList(List<String> responseEncryptUriIgnoreList) {
		this.responseEncryptUriIgnoreList = responseEncryptUriIgnoreList;
	}

	public List<String> getRequestDecryptUriIgnoreList() {
		// 配置和注解两种方式合并
		return Stream.of(requestDecryptUriIgnoreList, ApiEncryptDataInit.requestDecryptUriIgnoreList).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public void setRequestDecryptUriIgnoreList(List<String> requestDecyptUriIgnoreList) {
		this.requestDecryptUriIgnoreList = requestDecyptUriIgnoreList;
	}

	public List<String> getRequestDecryptParams(String uri) {
		List<String> params = ApiEncryptDataInit.requestDecryptParamMap.get(uri);
		if (CollectionUtils.isEmpty(params)) {
			return new ArrayList<>();
		}

		return params;
	}

	
}
