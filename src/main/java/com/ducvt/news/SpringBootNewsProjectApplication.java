package com.ducvt.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class SpringBootNewsProjectApplication {

	public static void main(String[] args) {
    SpringApplication.run(SpringBootNewsProjectApplication.class, args);
	}

}
