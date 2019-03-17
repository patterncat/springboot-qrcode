package cn.patterncat.qrcode;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by patterncat on 2019-03-17.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QrCodeCoreConfig.class, QrCodeMvcConfig.class})
public @interface EnableQrCode {
}
