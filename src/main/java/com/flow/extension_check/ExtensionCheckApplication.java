package com.flow.extension_check;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.flow.extension_check.mapper")
public class ExtensionCheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtensionCheckApplication.class, args);
	}

}
