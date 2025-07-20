package com.timesheetspro_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = { "com.timesheetspro_api.*" })
@EntityScan(basePackages = { "com.timesheetspro_api" })
@EnableJpaRepositories(basePackages = { "com.timesheetspro_api.common.repository" })
public class TimesheetsproApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimesheetsproApplication.class, args);
		System.out.println("======================================== Server started ==================================");
	}

}
