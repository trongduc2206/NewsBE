package com.ducvt.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringBootNewsProjectApplication {

	public static void main(String[] args) {
    SpringApplication.run(SpringBootNewsProjectApplication.class, args);
	}

}
