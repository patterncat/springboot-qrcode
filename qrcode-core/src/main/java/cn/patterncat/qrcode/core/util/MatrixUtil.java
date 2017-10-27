package cn.patterncat.qrcode.core.util;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;

/**
 * 改进MatrixToImageWriter.java的一些方法
 * Created by patterncat on 2017-10-27.
 */
public class MatrixUtil {

    /**
     * 主要将原来的getBufferedImageColorModel改为从参数传入进去
     * @param matrix
     * @param onColor
     * @param offColor
     * @param colorModel
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix, int onColor, int offColor,int colorModel) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, colorModel);
        int[] pixels = new int[width * height];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[index++] = matrix.get(x, y) ? onColor : offColor;
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
}
