package com.cxytiandi.encrypt_springboot_example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.cxytiandi.encrypt.springboot.annotation.Decrypt;
import com.cxytiandi.encrypt.springboot.annotation.DecryptIgnore;
import com.cxytiandi.encrypt.springboot.annotation.Encrypt;
import com.cxytiandi.encrypt.springboot.annotation.EncryptIgnore;
import com.cxytiandi.encrypt_springboot_example.dto.UserDto;
import com.cxytiandi.encrypt_springboot_example.dto.UserXmlDto;
import org.springframework.web.servlet.DispatcherServlet;

@RestController
public class UserController {


	//@Encrypt
	@Decrypt(decyptParam = "age,name")
	@GetMapping("/encryptStr")
	public String encryptStr(String name) {
		System.out.println(name);
		return "加密字符串";
	}


	@PostMapping("/encryptStr")
	public String encryptStr2(String name) {
		System.out.println(name);
		return "加密字符串";
	}
	
	@Encrypt
	@GetMapping("/encryptEntity")
	public UserDto encryptEntity() {
		UserDto dto = new UserDto();
		dto.setId(1);
		dto.setName("加密实体对象");
		return dto;
	}

	@Encrypt
	@GetMapping("/encryptEntity2/{id}")
	public UserDto encryptEntity2(@PathVariable int id) {
		UserDto dto = new UserDto();
		dto.setId(id);
		dto.setName("加密实体对象");
		return dto;
	}
	
	@Encrypt
	@Decrypt
	//@DecryptIgnore
	//@EncryptIgnore
	@PostMapping("/save")
	public UserDto save(@RequestBody UserDto dto) {
		System.err.println(dto.getId() + "\t" + dto.getName());
		return dto;
	}

	@Decrypt
	@PostMapping("/save/{id}")
	public UserDto save(@RequestBody UserDto dto, @PathVariable int id) {
		System.err.println(dto.getId() + "\t" + dto.getName());
		return dto;
	}
	
	@RequestMapping(value="encryptEntityXml",produces= {MediaType.APPLICATION_XML_VALUE})
	public UserXmlDto encryptEntityXml() {
		UserXmlDto dto = new UserXmlDto();
		dto.setId(1);
		dto.setName("加密实体对象XML");
		return dto;
	}
	
	@RequestMapping(value="decryptEntityXml",consumes = {MediaType.APPLICATION_XML_VALUE})
	public String decryptEntityXml(@RequestBody UserXmlDto dto) {
		System.err.println(dto.getId() + "\t" + dto.getName());
		return dto.getName();
	}
	
}
