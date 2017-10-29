package cn.patterncat.qrcode.core;

import cn.patterncat.qrcode.core.bean.PrettyArgbColors;
import cn.patterncat.qrcode.core.util.ColorUtil;
import org.junit.Test;

import java.awt.*;

/**
 * Created by patterncat on 2017-10-26.
 */
public class ColorTest {

    public static final int BLACK = 0xFF000000;
    public static final int WHITE = 0xFFFFFFFF;

    /**
     * 注意string转换过来的话,要先去掉0x
     * 另外Integer直接value是报错的,得先long转换,再int value
     */
    @Test
    public void testOxNumber(){
        System.out.println(Long.valueOf("FF000000",16).intValue());
        System.out.println(BLACK);
        System.out.println(Integer.toHexString(BLACK));

        System.out.println(Long.valueOf("FFFFFFFF",16).intValue());
        System.out.println(WHITE);
        System.out.println(Integer.toHexString(WHITE));
    }

    /**
     * 这种就是需要携带Ox前缀
     */
    @Test
    public void testLongDecode(){
        System.out.println(Long.decode("0xFF000000").intValue());
        System.out.println(BLACK);

        System.out.println(Long.decode("0xFFFFFFFF").intValue());
        System.out.println(WHITE);
    }

    @Test
    public void testOpacity2AlpahHex(){
        for (double i = 1; i >= 0; i -= 0.05) {
            float percent = (float) (Math.round(i * 100) / 100.0);
            String hex = ColorUtil.opacity2AlphaHex(percent);
            System.out.println(String.format("%.2f -- %s",percent,hex));
        }
    }

    @Test
    public void testArgbColor(){
        System.out.println(Color.ORANGE.getRGB());
        System.out.println("0x"+Integer.toHexString(Color.ORANGE.getRGB()));
        System.out.println(ColorUtil.argbHex(Color.ORANGE,0.8f));
        System.out.println(ColorUtil.argbHex(new Color(-2130706433),0f));
    }

    @Test
    public void testPrettyColor(){
        System.out.println(PrettyArgbColors.LIGHT_GREEN.getRed());
        System.out.println(PrettyArgbColors.LIGHT_GREEN.getGreen());
        System.out.println(PrettyArgbColors.LIGHT_GREEN.getBlue());
        System.out.println(PrettyArgbColors.LIGHT_GREEN.getAlpha());
    }
}
