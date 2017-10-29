package cn.patterncat.qrcode.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.patterncat.qrcode.web","cn.patterncat.qrcode.example"})
public class QrcodeExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrcodeExampleApplication.class, args);
	}
}
