package cn.patterncat.qrcode.core;

import cn.patterncat.qrcode.core.bean.ImageType;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.coder.DefaultEnDeCoder;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by patterncat on 2017-10-27.
 */
public class QrCodeTest {

    QrCodeEnDeCoder enDeCoder = new DefaultEnDeCoder();

    @Test
    public void testGen() throws IOException, WriterException {
        //使用zxing默认的方法,如果msg不够长,则padding效果不明显,显示出来还是间距比较大
        QrCodeConfig config = QrCodeConfig.builder()
                .msg("http://mvnrepository.com/artifact/org.apache.commons/commons-lang3")
                .size(300)
                .padding(30)
                .paddingStrict(true)
                .bgColor("0xffffffff")
                .onColor("0xffff0000")
                .imageType(ImageType.jpg)
                .errorCorrectionLevel(ErrorCorrectionLevel.H)
                .build();
        enDeCoder.encodeAsFile(config,"testWithoutLogo.jpg");
    }

    @Test
    public void testWithLogo() throws IOException, WriterException {
        //使用zxing默认的方法,如果msg不够长,则padding效果不明显,显示出来还是间距比较大
        QrCodeConfig config = QrCodeConfig.builder()
                .msg("http://mvnrepository.com/artifact/org.apache.commons/commons-lang3")
                .size(300)
                .padding(1)
//                .bgColor("0xffffffff")
//                .onColor("0xffff0000")
                .logoRadius(20)
                .logoBorder(true)
                .logoBorderColor("0xFF98F5FF")
                .logo("http://www.36588.com.cn:8080/ImageResourceMongo/UploadedFile/dimension/big/58e52e26-4664-4f48-8891-23223d9cd787.png")
//                .logo(this.getClass().getClassLoader().getResource("logo.jpeg").getPath())
//                .logoRadius(5)
                .paddingStrict(true)
                .imageType(ImageType.jpg)
                .errorCorrectionLevel(ErrorCorrectionLevel.H)
                .build();
        enDeCoder.encodeAsFile(config,"testWithLogo.jpg");
    }
}
