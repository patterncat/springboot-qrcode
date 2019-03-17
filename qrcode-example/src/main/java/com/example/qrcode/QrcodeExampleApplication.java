package com.example.qrcode;

import cn.patterncat.qrcode.EnableQrCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableQrCode
public class QrcodeExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrcodeExampleApplication.class, args);
	}
}
