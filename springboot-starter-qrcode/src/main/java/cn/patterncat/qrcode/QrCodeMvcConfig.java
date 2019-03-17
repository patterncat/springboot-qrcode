package cn.patterncat.qrcode;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by patterncat on 2019-03-17.
 */
@Configuration
@ConditionalOnProperty(name = "qrcode.web.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan("cn.patterncat.qrcode.controller")
public class QrCodeMvcConfig {
}
