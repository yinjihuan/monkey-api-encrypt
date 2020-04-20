package com.cxytiandi.encrypt_springboot_example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/test")
	public String reqTestPage() {
		return "index";
	}
}
