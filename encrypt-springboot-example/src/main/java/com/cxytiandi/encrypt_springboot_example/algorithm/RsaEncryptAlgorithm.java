package com.cxytiandi.encrypt_springboot_example.algorithm;

import com.cxytiandi.encrypt.algorithm.EncryptAlgorithm;
import com.cxytiandi.encrypt_springboot_example.util.RSAUtils;
/**
 * 自定义RSA算法
 * 
 * @author yinjihuan
 * 
 * @date 2019-01-12
 * 
 * @about http://cxytiandi.com/about
 *
 */
public class RsaEncryptAlgorithm implements EncryptAlgorithm {

	public String encrypt(String content, String encryptKey) throws Exception {
		return RSAUtils.encryptByPublicKey(content);
	}

	public String decrypt(String encryptStr, String decryptKey) throws Exception {
		return RSAUtils.decryptByPrivateKey(encryptStr);
	}

}
