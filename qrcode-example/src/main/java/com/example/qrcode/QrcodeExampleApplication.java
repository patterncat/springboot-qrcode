package com.example.qrcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.patterncat.qrcode","com.example.qrcode"})
public class QrcodeExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrcodeExampleApplication.class, args);
	}
}
