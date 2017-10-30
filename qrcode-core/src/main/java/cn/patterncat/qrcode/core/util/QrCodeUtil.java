package cn.patterncat.qrcode.core.util;

import cn.patterncat.qrcode.core.bean.*;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.ByteMatrix;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * 改进MatrixToImageWriter.java的一些方法
 * 因为已经有core-3.3.0-sources.jar!/com/google/zxing/qrcode/encoder/MatrixUtil.java
 * 避免重名困扰,将自己的MatrixUtil改为BitMatrixUtil
 * Created by patterncat on 2017-10-27.
 */
public class QrCodeUtil {

    public static final int OUT_DETECT_RECT_SIZE = 7;

    public static BufferedImage addBgImgAndUseBgImgAsQrCodeOnColor(BufferedImage bgImg,
                                                                   BufferedImage qrCode,QrCodeConfig config){
        int width = qrCode.getWidth();
        int height = qrCode.getHeight();

        Graphics2D g2 = bgImg.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,config.getBgImgOpacity()));

        final int onColor = config.getOnColorIntValue();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int pointColor = qrCode.getRGB(x,y);
                //这里将前景色覆盖为使用背景色
                if(pointColor == onColor){
                    qrCode.setRGB(x,y,bgImg.getRGB(x,y));
                }
            }
        }
        g2.drawImage(qrCode,0,0, width, height, null);
        g2.dispose();
        bgImg.flush();
        return bgImg;
    }


    /**
     * 跟QrCodeConfig耦合的一个版本
     * 支持二维码编码信息部分的形状定制
     * @param bitMatrixInfo matrix的信息
     * @param config 配置对象
     * @param colorModel 颜色模型
     * @return 返回生成的BufferedImage
     */
    public static BufferedImage toColorBufferedImage(BitMatrixInfo bitMatrixInfo, QrCodeConfig config,
                                                     int colorModel) {
        int offColor = config.getOffColorIntValue();
        int onColor = config.getOnColorIntValue();
        int detectInColor = config.getDetectInColorIntValue();
        int detectOutColor = config.getDetectOutColorIntValue();

        int width = bitMatrixInfo.getOutputWidth();
        int height = bitMatrixInfo.getOutputHeight();

        BufferedImage image = new BufferedImage(width, height, colorModel);
        Graphics2D g2 = image.createGraphics();

        g2.setColor(new Color(offColor));
        g2.fillRect(0, 0, width, height);
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ByteMatrix byteMatrix = bitMatrixInfo.getQrCode().getMatrix();
        int multiple = bitMatrixInfo.getMultiple();
        int byteWidth = byteMatrix.getWidth();
        int byteHeight = byteMatrix.getHeight();
        int leftPadding = bitMatrixInfo.getLeftPadding();
        int topPadding = bitMatrixInfo.getTopPadding();

        //改为由原始信息,根据padding和multiple去画扩大后的二维码
        for(int x = 0; x < byteWidth; x++){
            for(int y = 0; y < byteHeight; y++){
                int outputX = leftPadding + x * multiple;
                int outputY = topPadding + y * multiple;
                if(byteMatrix.get(x,y) == 0){
                    //背景区域
                    g2.setColor(new Color(offColor));
                    g2.fillRect(outputX,outputY,multiple,multiple);
                    continue;
                }
                //余下的都是有信息的区域
                Optional<DetectInfo> optional = isDectectPosition(byteMatrix,x,y);
                if(optional.isPresent()){
                    //detect区域
                    if(InOutType.OUTER == optional.get().getInOutType()){
                        g2.setColor(new Color(detectOutColor));
                    }else{
                        g2.setColor(new Color(detectInColor));
                    }
                    g2.fillRect(outputX,outputY,multiple,multiple);
                    continue;
                }else{
                    //数据区域
                    g2.setColor(new Color(onColor));
                    config.getDataShape().draw(g2,outputX,outputY,multiple,multiple);
                }
            }
        }

        g2.dispose();
        image.flush();
        return image;
    }

    /**
     * 由于getBufferedImageColorModel方法是保包内访问权限,这里在默认onColor和offColor的时候,设置为了TYPE_BYTE_BINARY
     * 这里提供个参数来设置
     * @param onColor 前景色
     * @param offColor 背景色
     * @param useBinaryIfMatch 匹配到binary的话,是否优先使用
     * @return 返回最佳的颜色类型
     */
    public static int getBufferedImageColorModel(int onColor,int offColor,boolean useBinaryIfMatch){
        if (onColor == MatrixToImageConfig.BLACK && offColor == MatrixToImageConfig.WHITE) {
            if(!useBinaryIfMatch){
                //如果是默认颜色,但是useBinaryIfMatch == false,则强制为rgb
                return BufferedImage.TYPE_INT_RGB;
            }
            // Use faster BINARY if colors match default
            return BufferedImage.TYPE_BYTE_BINARY;
        }
        if (ColorUtil.hasTransparency(onColor) || ColorUtil.hasTransparency(offColor)) {
            // Use ARGB representation if colors specify non-opaque alpha
            return BufferedImage.TYPE_INT_ARGB;
        }
        // Default otherwise to RGB representation with ignored alpha channel
        return BufferedImage.TYPE_INT_RGB;
    }

    /**
     * 判断是不是Detection Position
     * 位置在左上角,右上角,左下角
     * 分内外两个矩形,外层矩阵边长为7,内层矩阵边长为3
     * 这里返回是否属于detect矩形里头,并分内外层,之后至于是否实心,看byteMatrix的值
     *   1 1 1 1 1 1 1
     *   1 0 0 0 0 0 1
     *   1 0 1 1 1 0 1
     *   1 0 1 1 1 0 1
     *   1 0 1 1 1 0 1
     *   1 0 0 0 0 0 1
     *   1 1 1 1 1 1 1
     * 如果不获取方位,则这个方法可以简写一下,合并if条件
     * @param byteMatrix byte矩阵
     * @param x 坐标x
     * @param y 坐标y
     * @return DetectInfo,optional类型
     */
    public static Optional<DetectInfo> isDectectPosition(ByteMatrix byteMatrix, int x, int y){
        int width = byteMatrix.getWidth();
        int height = byteMatrix.getHeight();

        if(x < OUT_DETECT_RECT_SIZE && y < OUT_DETECT_RECT_SIZE){
            //左上角
            DetectInfo.DetectInfoBuilder builder = DetectInfo.builder()
                    .positionType(DetectPositionType.LEFT_TOP);
            //判断是内层还是外层
            if(x == 0  //左边
                    || y == 0  //上边
                    || x == OUT_DETECT_RECT_SIZE - 1 //右边
                    || y == OUT_DETECT_RECT_SIZE -1){ //下边
                builder.inOutType(InOutType.OUTER);
            }else{
                builder.inOutType(InOutType.INNER);
            }
            return Optional.of(builder.build());
        }

        if(x < OUT_DETECT_RECT_SIZE && (y >= height - OUT_DETECT_RECT_SIZE)){
            //左下角,注意这里y要 >= height - OUT_DETECT_RECT_SIZE
            DetectInfo.DetectInfoBuilder builder = DetectInfo.builder()
                    .positionType(DetectPositionType.LEFT_BOTTOM);
            if(x == 0  //左边
                    || y == height - OUT_DETECT_RECT_SIZE//上边
                    || x == OUT_DETECT_RECT_SIZE - 1//右边
                    || y == height - 1){ //下边
                builder.inOutType(InOutType.OUTER);
            }else{
                builder.inOutType(InOutType.INNER);
            }
            return Optional.of(builder.build());
        }

        if((x >= width - OUT_DETECT_RECT_SIZE) && y < OUT_DETECT_RECT_SIZE){
            //右上角,注意这里x要 >= width - OUT_DETECT_RECT_SIZE
            DetectInfo.DetectInfoBuilder builder = DetectInfo.builder()
                    .positionType(DetectPositionType.RIGHT_TOP);
            if(x == width - OUT_DETECT_RECT_SIZE //左边
                    || y == 0  //上边
                    || x == width - 1  //右边
                    || y == OUT_DETECT_RECT_SIZE - 1){ //下边
                builder.inOutType(InOutType.OUTER);
            }else {
                builder.inOutType(InOutType.INNER);
            }
            return Optional.of(builder.build());
        }

        return Optional.empty();
    }
}
