package cn.patterncat.qrcode;

import org.junit.Test;

/**
 * Created by patterncat on 2017-10-26.
 */
public class NumberTest {

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
}
