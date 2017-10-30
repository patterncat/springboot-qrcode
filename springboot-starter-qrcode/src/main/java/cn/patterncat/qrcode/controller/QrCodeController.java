package cn.patterncat.qrcode.controller;

import cn.patterncat.qrcode.core.bean.ImageType;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by patterncat on 2017-10-27.
 */
@ConditionalOnProperty(
        name = "qrcode.web.enabled",
        havingValue = "true",
        matchIfMissing = false
)
@RestController
@RequestMapping("/qrcode")
public class QrCodeController {

    @Autowired
    QrCodeEnDeCoder qrCodeEnDeCoder;

    @GetMapping(value = "")
    public void generate(@RequestParam String content,
                         @RequestParam(required = false) ImageType imageType,
                         @RequestParam(required = false) Integer size,
                         @RequestParam(required = false) Integer padding,
                         @RequestParam(required = false,defaultValue = "true") Boolean paddingStrict,
                         @RequestParam(required = false) String logoUrl,
                         @RequestParam(required = false) Integer logoSizeRatio,
                         @RequestParam(required = false,defaultValue = "true") Boolean logoRound,
                         @RequestParam(required = false) Integer logoRoundRadius,
                         @RequestParam(required = false) Integer logoBorderSizeRatio,
                         @RequestParam(required = false, defaultValue ="true") Boolean logoBorder,
                         @RequestParam(required = false) String logoBorderColor,
                         HttpServletResponse response) throws IOException, WriterException {
        ImageType type = imageType == null ? ImageType.png : imageType;
        response.setContentType("image/" + type.name());
        response.setHeader("Content-Disposition","inline");
        QrCodeConfig config = QrCodeConfig.builder().msg(content)
                .size(size == null ? 300 : size)
                .imageType(type)
                .logo(logoUrl)
                .logoRoundCorner(logoRound)
                .errorCorrectionLevel(ErrorCorrectionLevel.H)
                .build();
        if(padding != null){
            config.setPadding(padding);
            config.setPaddingStrict(paddingStrict);
        }
        if(logoSizeRatio != null){
            config.setLogoSizeRatio(logoSizeRatio);
        }
        if(!logoBorder){
            config.setLogoBorder(false);
        }else{
            config.setLogoBorder(true);
            if(logoBorderSizeRatio != null){
                config.setLogoBroderSizeRatio(logoBorderSizeRatio);
            }
            if(StringUtils.isNotBlank(logoBorderColor)){
                config.setLogoBorderColor(logoBorderColor);
            }
        }
        if(logoRound && logoRoundRadius != null){
            config.setLogoRadius(logoRoundRadius);
        }
        qrCodeEnDeCoder.write(config,response.getOutputStream());
    }
}
