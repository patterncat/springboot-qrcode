package cn.patterncat.qrcode.web;

import cn.patterncat.qrcode.core.coder.DefaultEnDeCoder;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import cn.patterncat.qrcode.web.controller.QrCodeController;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by patterncat on 2017-10-30.
 */
@Configuration
@ConditionalOnProperty(
        prefix = "qrcode",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
@ComponentScan( basePackages = {"com.patterncat.qrcode.web"} )
@EnableAutoConfiguration
public class QrCodeAutoConfiguration {

    @Bean
    public QrCodeEnDeCoder qrCodeEnDeCoder(){
        return new DefaultEnDeCoder();
    }

    @Bean
    public QrCodeController qrCodeController(){
        return new QrCodeController();
    }
}
