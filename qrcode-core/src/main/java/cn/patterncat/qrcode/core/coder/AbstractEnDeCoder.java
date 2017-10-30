package cn.patterncat.qrcode.core.coder;

import cn.patterncat.qrcode.core.bean.BitMatrixInfo;
import cn.patterncat.qrcode.core.bean.ImageType;
import cn.patterncat.qrcode.core.bean.QrCodeConfig;
import cn.patterncat.qrcode.core.util.ColorUtil;
import cn.patterncat.qrcode.core.util.ImgUtil;
import cn.patterncat.qrcode.core.util.QrCodeGenerator;
import cn.patterncat.qrcode.core.writer.DefaultQrCodeWriterQrCode;
import cn.patterncat.qrcode.core.writer.QrCodeMatrixWriter;
import cn.patterncat.qrcode.core.writer.StrictQuietZoneWriterQrCode;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
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
        if(config.isPaddingStrict()){
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
        int qrCodeWidth = bitMatrixInfo.getOutputWidth();
        int qrCodeHeight = bitMatrixInfo.getOutputHeight();

        //绘制qrcode的前景色及背景色
        BufferedImage qrCodeImg = drawQrCode(bitMatrixInfo,config);

        //判断图片大小与设置的是否一致,不一致则缩放
        int neededWidth = config.getSize();
        int neededHeight = config.getSize();
        if (qrCodeWidth != neededWidth || qrCodeHeight != neededHeight) {
            BufferedImage tmp = new BufferedImage(neededWidth, neededHeight,qrCodeImg.getType());
            tmp.getGraphics().drawImage(
                    qrCodeImg.getScaledInstance(neededWidth, neededHeight,
                            Image.SCALE_SMOOTH), 0, 0, null);
            qrCodeImg = tmp;
        }

        if(config.hasBgImage()){
            //设置整个图片的背景
            qrCodeImg = coverQrCodeToBgImage(qrCodeImg,config);
        }

        if(config.hasLogo()){
            //添加logo
            drawLogoOnQrCode(qrCodeImg,config);
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
        //如果是image是jpg,则使用argb会变成黑色
        int colorModel = config.getImageType() == ImageType.jpg ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        return QrCodeGenerator.toColorBufferedImage(bitMatrixInfo,config,colorModel);
    }

    protected void drawLogoOnQrCode(BufferedImage qrCode,QrCodeConfig config) throws IOException {
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
        ImgUtil.coverImage(logoImg,qrCode,
                config.getLogoSizeRatio(),config.getLogoSizeRatio(),
                AlphaComposite.SrcAtop); //将logo覆盖过去,使用SrcAtop解决圆角时没有被背景色填充问题
    }

    protected BufferedImage coverQrCodeToBgImage(BufferedImage qrCode,QrCodeConfig config) throws IOException {
        BufferedImage bgImage = ImgUtil.fromPathOrUrl(config.getBgImage());
        int width = qrCode.getWidth();
        int height = qrCode.getHeight();
        BufferedImage dstImg = bgImage;
        if (bgImage.getWidth() != width || height != qrCode.getHeight()) {
            dstImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            dstImg.getGraphics().drawImage(bgImage.getScaledInstance(width, height, Image.SCALE_SMOOTH)
                    , 0, 0, null);
        }
        //将qrcode覆盖到背景图上
        if(config.isUseBgImgColor()){
            return QrCodeGenerator.addBgImgAndUseBgImgAsQrCodeOnColor(dstImg,qrCode,config);
        }
        ImgUtil.coverImage(qrCode,dstImg,1,1,
                AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,config.getBgImgOpacity()));
        return dstImg;
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
//            return QrCodeConfig.ROUND_RADIUS;
            return logoImg.getWidth() >> 2; //四分之一
        }
        return config.getLogoRadius();
    }
}
