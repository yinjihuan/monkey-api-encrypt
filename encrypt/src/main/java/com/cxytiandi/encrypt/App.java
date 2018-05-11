package com.cxytiandi.encrypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cxytiandi.encrypt.anno.EnableEncrypt;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEncrypt
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
