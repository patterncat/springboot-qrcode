package cn.patterncat.qrcode.core.util;

import cn.patterncat.qrcode.core.bean.BitMatrixInfo;
import cn.patterncat.qrcode.core.bean.DetectInfo;
import cn.patterncat.qrcode.core.bean.DetectPositionType;
import cn.patterncat.qrcode.core.bean.InOutType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.ByteMatrix;

import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * 改进MatrixToImageWriter.java的一些方法
 * 因为已经有core-3.3.0-sources.jar!/com/google/zxing/qrcode/encoder/MatrixUtil.java
 * 避免重名困扰,将自己的MatrixUtil改为BitMatrixUtil
 * Created by patterncat on 2017-10-27.
 */
public class BitMatrixUtil {

    public static final int OUT_DETECT_RECT_SIZE = 7;

    /**
     * 主要将原来的getBufferedImageColorModel改为从参数传入进去
     * 这里实现了二维码的着色
     * @param bitMatrixInfo
     * @param onColor
     * @param offColor
     * @param colorModel
     * @return
     */
    public static BufferedImage toColorBufferedImage(BitMatrixInfo bitMatrixInfo, int onColor, int offColor,
                                                int detectOutColor, int detectInColor,
                                                int colorModel) {
        BitMatrix matrix = bitMatrixInfo.getBitMatrix();
        BitMatrix detectOut = bitMatrixInfo.getDetectOutMatrix();
        BitMatrix detectIn = bitMatrixInfo.getDetectInMatrix();

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, colorModel);
        int[] pixels = new int[width * height];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int targetOnColor = onColor;
                //进行detect位置的颜色设置
                if(detectIn.get(x,y)){
                    targetOnColor = detectInColor;
                }else if(detectOut.get(x,y)){
                    targetOnColor = detectOutColor;
                }
                //二维码颜色设置
                pixels[index++] = matrix.get(x, y) ? targetOnColor : offColor;
            }
        }
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    /**
     * 由于getBufferedImageColorModel方法是保包内访问权限,这里在默认onColor和offColor的时候,设置为了TYPE_BYTE_BINARY
     * 这里提供个参数来设置
     * @return
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
     * @param byteMatrix
     * @param x
     * @param y
     * @return
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
