package cn.patterncat.qrcode.web.controller;

import cn.patterncat.qrcode.core.bean.ImageType;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Generated;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by patterncat on 2017-10-27.
 */
@Controller
@RequestMapping("/qrcode")
//@ConditionalOnProperty(
//        prefix = "qrcode",
//        name = "web",
//        havingValue = "true",
//        matchIfMissing = false
//)
public class QrCodeController {

    @Autowired
    QrCodeEnDeCoder qrCodeEnDeCoder;

    @Generated(value = "")
    public void generate(@RequestParam String content,
                         @RequestParam(required = false) Integer size,
                         @RequestParam(required = false) String logoUrl,
                         HttpServletResponse response) throws IOException, WriterException {
        response.setContentType("image/png");
        response.setHeader("Content-Disposition","inline");
        QrCodeConfig config = QrCodeConfig.builder().msg(content)
                .size(size == null ? 300 : size)
                .logo(logoUrl)
                .imageType(ImageType.png)
                .errorCorrectionLevel(ErrorCorrectionLevel.H)
                .build();
        qrCodeEnDeCoder.write(config,response.getOutputStream());
    }
}
