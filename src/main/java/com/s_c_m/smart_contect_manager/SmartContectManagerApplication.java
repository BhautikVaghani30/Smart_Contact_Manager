package com.s_c_m.smart_contect_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class SmartContectManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartContectManagerApplication.class, args);
	}

}
