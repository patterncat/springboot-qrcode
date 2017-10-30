package cn.patterncat.qrcode;

import cn.patterncat.qrcode.core.coder.DefaultEnDeCoder;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by patterncat on 2017-10-30.
 */
@Configuration
@ConditionalOnProperty(
        name = "qrcode.enabled",
        havingValue = "true",
        matchIfMissing = false
)
@EnableAutoConfiguration
@ComponentScan
public class QrCodeAutoConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public QrCodeEnDeCoder qrCodeEnDeCoder(){
        return new DefaultEnDeCoder();
    }
}
