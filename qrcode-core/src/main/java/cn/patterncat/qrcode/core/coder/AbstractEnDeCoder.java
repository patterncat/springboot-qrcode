package cn.patterncat.qrcode.core.coder;

import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.util.ColorUtil;
import cn.patterncat.qrcode.core.util.ImgUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    QRCodeWriter writer = new QRCodeWriter();

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

    protected BitMatrix encodeRawMsg(QrCodeConfig config) throws WriterException {
//        QRCode code = Encoder.encode(config.getMsg(), config.getErrorCorrectionLevel(),config.buildEncodeHints());
        return writer.encode(config.getMsg(), BarcodeFormat.QR_CODE,config.getSize(),config.getSize());
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

        MatrixToImageConfig matrixConfig = new MatrixToImageConfig(config.getOnColorIntValue(),config.getBgColorIntValue());
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
            logoImg = ImgUtil.addRoundedBorder(logoImg,config.getLogoRadius(),calLogoBorderSize(),logoBorderColor);
        }
        ImgUtil.coverImage(logoImg,qrCode,config.getLogoSizeRatio(),config.getLogoSizeRatio());
    }

    /**
     * 计算logo的边框粗细
     * @return
     */
    protected int calLogoBorderSize(){
        return 1;
    }
}
