package com.cxytiandi.encrypt_springboot_example.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cxytiandi.encrypt_springboot_example.dto.UserDto;
import com.cxytiandi.encrypt_springboot_example.dto.UserXmlDto;

@RestController
public class UserController {

	@GetMapping("/encryptStr")
	public String encryptStr() {
		return "加密字符串";
	}
	
	@GetMapping("/encryptEntity")
	public UserDto encryptEntity() {
		UserDto dto = new UserDto();
		dto.setId(1);
		dto.setName("加密实体对象");
		return dto;
	}
	
	@PostMapping("/save")
	public String save(@RequestBody UserDto dto) {
		System.err.println(dto.getId() + "\t" + dto.getName());
		return dto.getName();
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
