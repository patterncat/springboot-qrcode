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
}
