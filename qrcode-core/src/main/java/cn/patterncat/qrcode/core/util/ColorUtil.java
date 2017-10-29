package cn.patterncat.qrcode.core.util;

import java.awt.*;

/**
 * Created by patterncat on 2017-10-26.
 */
public class ColorUtil {

    /**
     * 必须是Ox开头
     * @param argb
     * @return
     */
    public static int argbString2Int(String argb){
        return Long.decode(argb).intValue();
    }

    /**
     * 转换为color对象
     * @param argb
     * @return
     */
    public static Color argb2Color(String argb){
        int color = argbString2Int(argb);
        int a = ((0x7f000000 & color) >> 24) | 0x00000080;
        int r = (0x00ff0000 & color) >> 16;
        int g = (0x0000ff00 & color) >> 8;
        int b = (0x000000ff & color);
        return new Color(r, g, b, a);
    }

    /**
     * 判断是否有alpha
     * @param argb
     * @return
     */
    public static boolean hasTransparency(int argb) {
        return (argb & 0xFF000000) != 0xFF000000;
    }

    /**
     * 将浮点的不透明度转换为ragb的16进制值
     * 比如1.0 -- FF
     * 0.95 -- F2
     * @param opacity
     * @return
     */
    public static String opacity2AlphaHex(float opacity){
        if(opacity > 1.0f || opacity < 0f){
            throw new IllegalArgumentException("opacity should in [0,1]");
        }
        int alpha = Math.round(opacity * 255);
        String hex = Integer.toHexString(alpha).toUpperCase();
        if (hex.length() == 1){
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 将现有的color常量加上opacity,构造成argb color
     * @param color
     * @param opacity
     * @return
     */
    public static Color argb(Color color,float opacity){
        if(opacity > 1.0f || opacity < 0f){
            throw new IllegalArgumentException("opacity should in [0,1]");
        }
        int alpha = Math.round(opacity * 255);
        return new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha);
    }

    /**
     * 转换为字符型
     * 比如Color.ORANGE,0.8f --> 0xCCFFC800
     * @param color
     * @param opacity
     * @return
     */
    public static String argbHex(Color color,float opacity){
        Color argb = argb(color, opacity);
        return "0x"+Integer.toHexString(argb.getRGB()).toUpperCase();
    }
}
