package cn.patterncat.qrcode.core.bean;

import cn.patterncat.qrcode.core.util.ColorUtil;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by patterncat on 2017-10-26.
 */
@Data
@Builder
public class QrCodeConfig {

    public static final int DEFAULT_IMG_SIZE = 200;
    public static final int DEFAULT_IMG_PADDING = 1;

    public static final String BLACK = "0xFF000000";
    public static final String WHITE = "0xFFFFFFFF";

    //矩形圆角radius为0
    public static final int RECT_RADIUS = 0;

    /**
     * qrcode携带的信息
     */
    private String msg;

    /**
     * 默认是UTF-8
     */
    @Builder.Default
    private String charset = StandardCharsets.UTF_8.name();

    /**
     * 整个图片的长/宽 px,默认是200x200
     * 注意这里不是二维码本身的大小
     * 二维码大小见生成的QRCode
     */
    @Builder.Default
    private int size = DEFAULT_IMG_SIZE;

    /**
     * 整个图片相对里头二维码的padding,即内边距,默认是1
     * 如果是以二维码为视角,相当于二维码相对整个图片的margin,即外边距
     * 注意这个值不是最后生成的图片中白边的大小,这个是原始QRCode的padding
     * 最后图片的padding是要QRCode根据指定的图片大小进行伸缩重新计算得来
     */
    @Builder.Default
    private int padding = DEFAULT_IMG_PADDING;

    /**
     * qrcode的背景色,16进制argb格式,比如0xFFFFFFFF,默认为白色
     */
    @Builder.Default
    private String bgColor = WHITE;

    /**
     * qrcode二维码的颜色,16进制argb格式,比如0xFF000000,默认为黑色
     */
    @Builder.Default
    private String onColor = BLACK;

    /**
     * 生成图片类型jpg或png
     */
    private ImageType imageType;

    /**
     * 二维码纠错级别,默认high
     */
    @Builder.Default
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.H;

    /**
     * 支持文件路径或url地址
     */
    private String logo;

    /**
     * 整个图片size/logo的size的比例
     * 默认是5,即logo占图片的1/5
     */
    @Builder.Default
    private int logoSizeRatio = 5;

    /**
     * logo及border的弧度,默认为矩形,即radius=0
     */
    @Builder.Default
    private int logoRadius = RECT_RADIUS;

    /**
     * logo是否有边框,默认有
     * logo外边距默认是根据图片大小来字段计算
     */
    @Builder.Default
    private boolean logoBorder = true;

    /**
     * logo边框的颜色,默认是白色
     */
    @Builder.Default
    private String logoBorderColor = WHITE;

    public void validParams(){
        //// TODO: 2017/10/26 校验参数
    }

    /**
     * 构造EncodeHints
     * todo 这里最好加一下参数校验
     * @return
     */
    public Map<EncodeHintType,Object> buildEncodeHints(){
        Map<EncodeHintType, Object> hints = new HashMap<>(3);
        hints.put(EncodeHintType.ERROR_CORRECTION,this.getErrorCorrectionLevel());
        hints.put(EncodeHintType.CHARACTER_SET,this.getCharset());
        //这里是从二维码角度来讲,因此是设置margin
        hints.put(EncodeHintType.MARGIN,this.getPadding());
        return hints;
    }

    public int getBgColorIntValue(){
        return ColorUtil.argbString2Int(this.bgColor);
    }

    public int getOnColorIntValue(){
        return ColorUtil.argbString2Int(this.onColor);
    }

    public boolean isRoundLogoCorner(){
        return this.logoRadius > RECT_RADIUS;
    }

}
