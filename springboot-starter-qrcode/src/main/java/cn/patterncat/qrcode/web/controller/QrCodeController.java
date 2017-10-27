package cn.patterncat.qrcode.web.controller;

import cn.patterncat.qrcode.core.bean.ImageType;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.coder.DefaultEnDeCoder;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by patterncat on 2017-10-27.
 */
@Controller
@RequestMapping("/qrcode")
public class QrCodeController {

    QrCodeEnDeCoder enDeCoder = new DefaultEnDeCoder();

    @RequestMapping(value = "",method = RequestMethod.GET)
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
        enDeCoder.write(config,response.getOutputStream());
    }
}
