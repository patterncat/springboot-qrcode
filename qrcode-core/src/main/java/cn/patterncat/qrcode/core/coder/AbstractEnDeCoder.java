package cn.patterncat.qrcode.core.coder;

import cn.patterncat.qrcode.core.bean.BitMatrixInfo;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.util.ColorUtil;
import cn.patterncat.qrcode.core.util.ImgUtil;
import cn.patterncat.qrcode.core.util.BitMatrixUtil;
import cn.patterncat.qrcode.core.writer.DefaultQrCodeWriterQrCode;
import cn.patterncat.qrcode.core.writer.QrCodeMatrixWriter;
import cn.patterncat.qrcode.core.writer.StrictQuietZoneWriterQrCode;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

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

    public static final Map<DecodeHintType, Object> DECODE_HINTS = new HashMap<DecodeHintType, Object>(){{
        put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
    }};

    QrCodeMatrixWriter defaultWriter = new DefaultQrCodeWriterQrCode();

    QrCodeMatrixWriter strictQuietZoneWriter = new StrictQuietZoneWriterQrCode();

    @Override
    public BufferedImage encodeAsBufferedImage(QrCodeConfig config) throws WriterException, IOException {
        BitMatrixInfo bitMatrixInfo = encodeMsgToMatrix(config);
        return decorate(config,bitMatrixInfo);
    }

    @Override
    public String decode(BufferedImage image) throws FormatException, ChecksumException, NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader qrCodeReader = new QRCodeReader();
        Result result = qrCodeReader.decode(bitmap,DECODE_HINTS);
        return result.getText();
    }

    @Override
    public void write(QrCodeConfig config, OutputStream outputStream) throws IOException, WriterException {
        BufferedImage bufferedImage = encodeAsBufferedImage(config);
        if (!ImageIO.write(bufferedImage,config.getImageType().name(), outputStream)) {
            throw new IOException("Could not write an image of format " + config.getImageType().name());
        }
    }

    protected BitMatrixInfo encodeMsgToMatrix(QrCodeConfig config) throws WriterException {
//        QRCode qrCode = Encoder.encode(config.getMsg(), config.getErrorCorrectionLevel(),config.buildEncodeHints());
        if(config.isPaddingStrict()){
//            bitMatrix = strictQuietZoneWriter.renderResult(qrCode,config.getSize(),config.getSize(),config.getPadding());
            return strictQuietZoneWriter.encode(config.getMsg(), BarcodeFormat.QR_CODE,config.getSize(),config.getSize(),config.buildEncodeHints());
        }
        return defaultWriter.encode(config.getMsg(), BarcodeFormat.QR_CODE,config.getSize(),config.getSize(),config.buildEncodeHints());
    }

    /**
     * 根据config的配置来装饰原始的二维码信息
     * 比如设置圆角\添加logo等
     * @param config
     * @param bitMatrixInfo
     * @return
     */
    protected BufferedImage decorate(QrCodeConfig config,BitMatrixInfo bitMatrixInfo) throws IOException {
        BitMatrix bitMatrix = bitMatrixInfo.getBitMatrix();
        int qrCodeWidth = bitMatrix.getWidth();
        int qrCodeHeight = bitMatrix.getHeight();

        //绘制qrcode的前景色及背景色
        BufferedImage qrCodeImg = drawQrCode(bitMatrixInfo,config);

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

        if(config.hasLogo()){
            //添加logo
            drawLogo(qrCodeImg,config);
        }


        return qrCodeImg;
    }

    /**
     * 修改MatrixToImageWriter.toBufferedImage(BitMatrix matrix, MatrixToImageConfig config)方法
     * @param bitMatrixInfo
     * @param config
     * @return
     */
    protected BufferedImage drawQrCode(BitMatrixInfo bitMatrixInfo, QrCodeConfig config){
        int onColor = config.getOnColorIntValue();
        int offColor = config.getOffColorIntValue();
        //如果有logo的话,则在可以使用binary的情况下,不使用binary,不然logo会变成黑色
        boolean useBinaryIfMatch = true;
        if(config.hasLogo()
                || config.getDetectInColorIntValue() != MatrixToImageConfig.BLACK
                || config.getDetectOutColorIntValue() != MatrixToImageConfig.BLACK){
            useBinaryIfMatch = false;
        }
        int colorModel = BitMatrixUtil.getBufferedImageColorModel(onColor,offColor,useBinaryIfMatch);
        return BitMatrixUtil.toBufferedImage(bitMatrixInfo,onColor,offColor,
                config.getDetectOutColorIntValue(),config.getDetectInColorIntValue(),
                colorModel);
    }

    protected void drawLogo(BufferedImage qrCode,QrCodeConfig config) throws IOException {
        BufferedImage logoImg = ImgUtil.fromPathOrUrl(config.getLogo());
        int roundRadius = calLogoRadius(logoImg,config);
        //是否圆角
        if(config.isLogoRoundCorner()){
            logoImg = ImgUtil.roundImageCorner(logoImg,roundRadius);
        }
        //是否边框
        if(config.isLogoBorder()){
            Color logoBorderColor = ColorUtil.argb2Color(config.getLogoBorderColor());
            int borderSize = calLogoBorderSize(logoImg,config);
            logoImg = ImgUtil.addRoundedBorder(logoImg,roundRadius,borderSize,logoBorderColor);
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
        if(!config.isLogoRoundCorner()){
            return QrCodeConfig.RECT_RADIUS;
        }
        //如果没有设置,则按默认
        if(QrCodeConfig.RECT_RADIUS == config.getLogoRadius()){
            return QrCodeConfig.ROUND_RADIUS;
        }
        return config.getLogoRadius();
    }
}
