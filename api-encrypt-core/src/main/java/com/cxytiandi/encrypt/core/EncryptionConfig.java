package com.cxytiandi.encrypt.core;

import java.util.ArrayList;
import java.util.List;

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
	
}
