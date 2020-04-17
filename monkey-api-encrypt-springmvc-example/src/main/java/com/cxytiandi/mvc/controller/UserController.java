package com.cxytiandi.mvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cxytiandi.encrypt.springboot.annotation.Decrypt;
import com.cxytiandi.encrypt.springboot.annotation.Encrypt;
import com.cxytiandi.mvc.dto.UserDto;

@RestController
public class UserController {

	//@Encrypt
	@GetMapping("/encryptEntity")
	public UserDto encryptEntity() {
		UserDto dto = new UserDto();
		dto.setId(1);
		dto.setName("加密实体对象");
		return dto;
	}
	
	//@Decrypt
	@PostMapping("/save")
	public UserDto save(@RequestBody UserDto dto) {
		System.err.println(dto.getId() + "\t" + dto.getName());
		return dto;
	}
	
}
