package cn.patterncat.qrcode.core;

import cn.patterncat.qrcode.core.bean.ImageType;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.coder.DefaultEnDeCoder;
import cn.patterncat.qrcode.core.coder.QrCodeEnDeCoder;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
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
                .offColor("0xffffffff")
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
//                .offColor("0xffffffff")
//                .onColor("0xffff0000")
//                .logoRadius(20)
                .logoRoundCorner(true)
                .logoBorder(true)
                .logoBorderColor("0xffff0000")
                .logo("http://www.36588.com.cn:8080/ImageResourceMongo/UploadedFile/dimension/big/58e52e26-4664-4f48-8891-23223d9cd787.png")
//                .logo(this.getClass().getClassLoader().getResource("logo.jpeg").getPath())
//                .logoRadius(5)
                .paddingStrict(true)
                .imageType(ImageType.jpg)
                .errorCorrectionLevel(ErrorCorrectionLevel.H)
                .build();
        enDeCoder.encodeAsFile(config,"testWithLogo.jpg");
    }

    @Test
    public void testAsString() throws IOException, WriterException, FormatException, ChecksumException, NotFoundException {
        QrCodeConfig config = QrCodeConfig.builder()
                .msg("hello world")
                .build();
        String base64 = enDeCoder.encodeAsBase64(config);
        System.out.println(base64);

        System.out.println(enDeCoder.decodeFromBase64(base64));

    }

    @Test
    public void testDetectColor() throws IOException, WriterException {
        QrCodeConfig config = QrCodeConfig.builder()
                .msg("http://mvnrepository.com/artifact/org.apache.commons/commons-lang3")
                .size(400)
                .onColor("0xFFCD96CD")
                .offColor("0x2687CEFF")
                .detectInColor("0xFFFF6A6A")
                .detectOutColor("0xFFC0FF3E")
                .logoRoundCorner(true)
                .logoBorder(true)
                .logoBorderColor("0xffff0000")
                .logo("http://www.36588.com.cn:8080/ImageResourceMongo/UploadedFile/dimension/big/58e52e26-4664-4f48-8891-23223d9cd787.png")
                .build();
        enDeCoder.encodeAsFile(config,"out.jpg");
    }

    @Test
    public void testBgImg() throws IOException, WriterException {
        QrCodeConfig config = QrCodeConfig.builder()
                .msg("http://www.36588.com.cn:8080/ImageResourceMongo/UploadedFile/dimension/big/58e52e26-4664-4f48-8891-23223d9cd787.png")
                .size(300)
                .bgImage("https://b-ssl.duitang.com/uploads/item/201406/05/20140605221928_s2Gyx.jpeg")
                .onColor("0xff0000ff")
                .offColor("0x80ffffff")
                .detectInColor("0xffff0000")
                .detectOutColor("0xff00FF00")
                .padding(1)
                .paddingStrict(true)
                .logoSizeRatio(3)
                .logoRoundCorner(true)
                .logoBorder(false)
//                .logoRadius(45)
                .logoBorderColor("0xffff0000")
//                .logo("http://www.36588.com.cn:8080/ImageResourceMongo/UploadedFile/dimension/big/58e52e26-4664-4f48-8891-23223d9cd787.png")
                .logo("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1509853713&di=681de473c5a9510909cf963a3e716c48&imgtype=jpg&er=1&src=http%3A%2F%2Fm.qqzhi.com%2Fupload%2Fimg_1_2823257972D3596592729_23.jpg")
                .build();
        enDeCoder.encodeAsFile(config,"out.jpg");
    }
}
