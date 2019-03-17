package cn.patterncat.qrcode;

import cn.patterncat.qrcode.core.coder.DefaultEnDeCoder;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by patterncat on 2019-03-17.
 */
@Configuration
public class QrCodeCoreConfig {

    @Bean
    @ConditionalOnProperty( name = "qrcode.core.enabled", havingValue = "true", matchIfMissing = true)
    public QrCodeEnDeCoder qrCodeEnDeCoder(){
        return new DefaultEnDeCoder();
    }

}
