package cn.patterncat.qrcode.core.coder;

import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.util.ImgUtil;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by patterncat on 2017-10-26.
 */
public class DefaultEnDeCoder extends AbstractEnDeCoder{

    @Override
    public String encodeAsBase64(QrCodeConfig config) throws IOException, WriterException {
        BufferedImage bufferedImage = encodeAsBufferedImage(config);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage,config.getImageType().name(), outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    @Override
    public File encodeAsFile(QrCodeConfig config, String imgPath) throws IOException, WriterException {
        File file = new File(imgPath);
        BufferedImage bufferedImage = encodeAsBufferedImage(config);
        boolean success = ImageIO.write(bufferedImage,config.getImageType().name(), file);
        if(!success){
            throw new IOException("no appropriate writer is found");
        }
        return file;
    }

    @Override
    public String decodeFromPath(String imgPath) throws IOException, FormatException, ChecksumException, NotFoundException {
        BufferedImage bufferedImage = ImgUtil.fromPathOrUrl(imgPath);
        return decode(bufferedImage);
    }

    @Override
    public String decodeFromBase64(String base64Img) throws IOException, FormatException, ChecksumException, NotFoundException {
        byte[] data = Base64.getDecoder().decode(base64Img.getBytes());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        return decode(bufferedImage);
    }
}
