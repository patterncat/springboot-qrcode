package cn.patterncat.qrcode.core.coder;

import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.util.ColorUtil;
import cn.patterncat.qrcode.core.util.ImgUtil;
import cn.patterncat.qrcode.core.writer.StrictQuietZoneWriter;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by patterncat on 2017-10-26.
 */
public abstract class AbstractEnDeCoder implements QrCodeEnDeCoder {

    public static final Map<DecodeHintType, Object> decodeHints = new HashMap<DecodeHintType, Object>(){{
        put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
    }};

    Writer defaultWriter = new QRCodeWriter();

    Writer strictQuietZoneWriter = new StrictQuietZoneWriter();

    @Override
    public BufferedImage encodeAsBufferedImage(QrCodeConfig config) throws WriterException, IOException {
        BitMatrix bitMatrix = encodeRawMsg(config);
        return decorate(config,bitMatrix);
    }

    @Override
    public String decode(BufferedImage image) throws FormatException, ChecksumException, NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader qrCodeReader = new QRCodeReader();
        Result result = qrCodeReader.decode(bitmap,decodeHints);
        return result.getText();
    }

    @Override
    public void write(QrCodeConfig config, OutputStream outputStream) throws IOException, WriterException {
        BufferedImage bufferedImage = encodeAsBufferedImage(config);
        if (!ImageIO.write(bufferedImage,config.getImageType().name(), outputStream)) {
            throw new IOException("Could not write an image of format " + config.getImageType().name());
        }
    }

    protected BitMatrix encodeRawMsg(QrCodeConfig config) throws WriterException {
//        QRCode code = Encoder.encode(config.getMsg(), config.getErrorCorrectionLevel(),config.buildEncodeHints());
        if(config.isPaddingStrict()){
            return strictQuietZoneWriter.encode(config.getMsg(), BarcodeFormat.QR_CODE,config.getSize(),config.getSize(),config.buildEncodeHints());
        }
        return defaultWriter.encode(config.getMsg(), BarcodeFormat.QR_CODE,config.getSize(),config.getSize(),config.buildEncodeHints());
    }

    /**
     * 根据config的配置来装饰原始的二维码信息
     * 比如设置圆角\添加logo等
     * @param config
     * @param bitMatrix
     * @return
     */
    protected BufferedImage decorate(QrCodeConfig config,BitMatrix bitMatrix) throws IOException {
        int qrCodeWidth = bitMatrix.getWidth();
        int qrCodeHeight = bitMatrix.getHeight();

        MatrixToImageConfig matrixConfig = config.buildMatrixToImageConfig();
        //绘制qrcode的前景色及背景色
        BufferedImage qrCodeImg = drawQrCode(bitMatrix,matrixConfig);


        //判断图片大小与设置的是否一致,不一致则缩放
        int neededWidth = config.getSize();
        int neededHeight = config.getSize();
        if (qrCodeWidth != neededWidth || qrCodeHeight != neededHeight) {
            BufferedImage tmp = new BufferedImage(neededWidth, neededHeight, BufferedImage.TYPE_INT_RGB);
            tmp.getGraphics().drawImage(
                    qrCodeImg.getScaledInstance(neededWidth, neededHeight,
                            Image.SCALE_SMOOTH), 0, 0, null);
            qrCodeImg = tmp;
        }

        String logoLocation = config.getLogo();
        if(logoLocation != null && logoLocation.length() > 0){
            //添加logo
            drawLogo(qrCodeImg,config);
        }


        return qrCodeImg;
    }

    protected BufferedImage drawQrCode(BitMatrix bitMatrix,MatrixToImageConfig matrixConfig){
        return MatrixToImageWriter.toBufferedImage(bitMatrix,matrixConfig);
    }

    protected void drawLogo(BufferedImage qrCode,QrCodeConfig config) throws IOException {
        BufferedImage logoImg = ImgUtil.fromPathOrUrl(config.getLogo());
        //是否圆角
        if(config.isRoundLogoCorner()){
            logoImg = ImgUtil.roundImageCorner(logoImg,config.getLogoRadius());
        }
        //是否边框
        if(config.isLogoBorder()){
            Color logoBorderColor = ColorUtil.argb2Color(config.getLogoBorderColor());
            int borderSize = calLogoBorderSize(logoImg,config);
            logoImg = ImgUtil.addRoundedBorder(logoImg,calLogoRadius(logoImg,config),borderSize,logoBorderColor);
        }
        ImgUtil.coverImage(logoImg,qrCode,config.getLogoSizeRatio(),config.getLogoSizeRatio());
    }

    /**
     * 计算logo的边框粗细
     * @return
     */
    protected int calLogoBorderSize(BufferedImage logoImg,QrCodeConfig config){
        return logoImg.getWidth() / config.getLogoBroderSizeRatio();
    }

    protected int calLogoRadius(BufferedImage logoImg,QrCodeConfig config){
        if(!config.isRoundLogoCorner()){
            return QrCodeConfig.RECT_RADIUS;
        }
        //如果没有设置,则自动计算
        if(QrCodeConfig.RECT_RADIUS == config.getLogoRadius()){
            //默认取logo长度的1/4
            return logoImg.getWidth() / 4;
        }
        return config.getLogoRadius();
    }
}
