package com.cxytiandi.encrypt.controller;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cxytiandi.encrypt.anno.Decrypt;
import com.cxytiandi.encrypt.anno.Encrypt;
import com.cxytiandi.encrypt.dto.RsaKeyInfoDto;
import com.cxytiandi.encrypt.praram.StuInfo;
import com.cxytiandi.encrypt.util.AESUtils;
import com.cxytiandi.encrypt.util.RSAUtils;

@Controller
public class IndexController {
	
	private static Map<String, RsaKeyInfoDto> RSA_KEY_MAP = new ConcurrentHashMap<String, RsaKeyInfoDto>();
	private static Map<String, String> DES_KEY_MAP = new ConcurrentHashMap<String, String>();
	
	@GetMapping("/")
	public String indexPage() {
		return "index";
	}
	
	@PostMapping("/save")
	@ResponseBody
	@Decrypt
	@Encrypt
	public Object save(@RequestBody StuInfo info) {
		System.out.println(info.getName());
		return info;
	}
	
	@GetMapping("/get")
	@ResponseBody
	@Encrypt
	public Object get() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dd", new Date());
		map.put("s", false);
		return map;
	}
	/**
	 * 第一步：返回pk1给客户端加密客户端生成的pk2
	 * @return
	 */
	@GetMapping("/encrypt/pk")
	@ResponseBody
	public Object getPublicKeyOne() {
		Map<String, String> result = new HashMap<String, String>();
		try {
			HashMap<String, Object> map = RSAUtils.getKeys();
			//生成公钥和私钥
	        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
	        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
	        //模
	        String modulus = publicKey.getModulus().toString();
	        System.err.println("modulus:" + modulus);
	        //公钥指数
	        String publicExponent = publicKey.getPublicExponent().toString();
	        System.err.println("public_exponent:" + publicExponent);
	        //私钥指数
	        String privateExponent = privateKey.getPrivateExponent().toString();
	        System.err.println("private_exponent:" + privateExponent);
	        RsaKeyInfoDto key = new RsaKeyInfoDto(modulus, publicExponent, privateExponent);
	        RSA_KEY_MAP.put(modulus, key);
	        result.put("modulus", modulus);
	        result.put("publicExponent", publicExponent);
	        return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
	/**
	 * prik1解密客户端的数据，得到pk2,用pk2加密AesKey返回给客户端进行加密操作
	 * @param data
	 * @return
	 */
	public Object getAesKey(String modulus, String data) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			RsaKeyInfoDto info = RSA_KEY_MAP.get(modulus);
			String pk2 = RSAUtils.decryptByPrivateKey(data, modulus, info.getPrivateExponent());
			String modulus2 = pk2.split("_")[0];
			String exponent = pk2.split("_")[1];
			String pass = "";
			if (DES_KEY_MAP.containsKey(modulus)) {
				pass = DES_KEY_MAP.get(modulus);
			} else {
				// 生成AES秘钥
				pass = AESUtils.generatePass();
				DES_KEY_MAP.put(modulus, pass);
			}
			result.put("pass", RSAUtils.encryptByPublicKey(pass, modulus2, exponent));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
}
