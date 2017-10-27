package cn.patterncat.qrcode.core.coder;

import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by patterncat on 2017-10-26.
 */
public interface QrCodeEnDeCoder {

    public BufferedImage encodeAsBufferedImage(QrCodeConfig config) throws WriterException, IOException;

    public String encodeAsString(QrCodeConfig config) throws IOException, WriterException;

    public void write(QrCodeConfig config, OutputStream outputStream) throws IOException, WriterException;

    public File encodeAsFile(QrCodeConfig config,String imgPath) throws IOException, WriterException;

    public String decode(BufferedImage image) throws FormatException, ChecksumException, NotFoundException;

    public String decode(String imgPath) throws IOException, FormatException, ChecksumException, NotFoundException;
}
