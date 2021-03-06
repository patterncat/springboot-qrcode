package cn.patterncat.qrcode.core.bean;

import cn.patterncat.qrcode.core.color.GradientType;
import cn.patterncat.qrcode.core.util.ColorUtil;
import cn.patterncat.qrcode.core.util.ValidationUtil;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by patterncat on 2017-10-26.
 */
@lombok.Data
@lombok.Builder
public class QrCodeConfig {

    public static final int DEFAULT_IMG_SIZE = 200;
    public static final int DEFAULT_IMG_PADDING = 1;

    public static final String BLACK = "0xFF000000";
    public static final String WHITE = "0xFFFFFFFF";

    //矩形圆角radius为0
    public static final int RECT_RADIUS = 0;
    public static final int ROUND_RADIUS = 180;

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
     * 如果这里是true的话,则严格保证最后生成的padding大小跟设置的一样
     */
    @Builder.Default
    private boolean paddingStrict = false;

    /**
     * qrcode的背景色,16进制argb格式,比如0xFFFFFFFF,默认为白色
     */
    @Builder.Default
    private String offColor = WHITE;

    /**
     * qrcode二维码的颜色,16进制argb格式,比如0xFF000000,默认为黑色
     */
    @Builder.Default
    private String onColor = BLACK;

    /**
     * oncolor渐变色,两者一样则不渐变
     */
    @Builder.Default
    private String gradientOnColor = BLACK;

    /**
     * 默认的渐变类似从左上角到右下角
     */
    @Builder.Default
    private GradientType gradientType = GradientType.LEFT_TOP_RIGHT_DOWN;

    /**
     * 背景图片
     */
    private String bgImage;

    /**
     * 不透明程度
     */
    @Builder.Default
    private float bgImgOpacity = 0.8f;

    /**
     * 三个detect position的外层颜色
     */
    @Builder.Default
    private String detectOutColor = BLACK;

    /**
     * 三个detect position的内层颜色
     */
    @Builder.Default
    private String detectInColor = BLACK;

    /**
     * 如果这个为true,则使用bgImg的底色作为onColor
     */
    @Builder.Default
    private boolean useBgImgColor = false;

    /**
     * 生成图片类型jpg或png
     * 默认png,因为ImageIO对jpg在ragb下面有bug
     */
    @Builder.Default
    private ImageType imageType = ImageType.png;

    @Builder.Default
    private QrCodeDataShape dataShape = QrCodeDataShape.RECT;

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
     * 默认是5,即logo占图片的1/4
     */
    @Builder.Default
    private int logoSizeRatio = 4;

    @Builder.Default
    private boolean logoRoundCorner = false;

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
     * 默认为logo边框宽度的1/15
     */
    @Builder.Default
    private int logoBroderSizeRatio = 15;

    /**
     * logo边框的颜色,默认是白色
     */
    @Builder.Default
    private String logoBorderColor = WHITE;

    public boolean hasLogo(){
        return StringUtils.isNotBlank(logo);
    }

    public boolean hasBgImage(){
        return StringUtils.isNotBlank(bgImage);
    }

    public void validateParams(){
        ValidationUtil.checkNotBlank(msg,"msg should not be empty");
        ValidationUtil.checkExpressionTrue(size > 0,"size should > 0");
        ValidationUtil.checkExpressionTrue(padding >= 0 && padding < size,"padding should >= 0 and < size");
        ValidationUtil.checkExpressionTrue(logoSizeRatio > 0,"logoSizeRatio should > 0");
        ValidationUtil.checkExpressionTrue(logoBroderSizeRatio > 0,"logoBroderSizeRatio should > 0");
        ValidationUtil.checkExpressionTrue(logoRadius > -1,"logoRadius should > -1");
        ValidationUtil.checkExpressionTrue(imageType != null,"imageType should not be null");
        ValidationUtil.checkExpressionTrue(errorCorrectionLevel != null,"errorCorrectionLevel should not be null");
        ValidationUtil.checkExpressionTrue(dataShape != null,"dataShape should not be null");
        ValidationUtil.checkExpressionTrue(bgImgOpacity >= 0f && bgImgOpacity <= 1.0f,"bgImgOpacity should >=0 and <= 1");
        //validate color
        try{
            getOffColorIntValue();
            getOnColorIntValue();
            getGradientOnColorIntValue();
            getDetectInColor();
            getDetectOutColor();
            getLogoBorderColorIntValue();
        }catch (Exception e){
            throw new IllegalArgumentException("illegal color string",e);
        }
    }

    /**
     * 构造EncodeHints
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

    public int getOffColorIntValue(){
        return ColorUtil.argbString2Int(this.offColor);
    }

    public int getOnColorIntValue(){
        return ColorUtil.argbString2Int(this.onColor);
    }

    public int getGradientOnColorIntValue(){
        return ColorUtil.argbString2Int(this.gradientOnColor);
    }

    public int getDetectOutColorIntValue(){
        return ColorUtil.argbString2Int(this.detectOutColor);
    }

    public int getDetectInColorIntValue(){
        return ColorUtil.argbString2Int(this.detectInColor);
    }

    public int getLogoBorderColorIntValue(){
        return ColorUtil.argbString2Int(this.logoBorderColor);
    }

    public static class InternalBuilder extends QrCodeConfig.QrCodeConfigBuilder {
        InternalBuilder() {
            super();
        }
        @Override
        public QrCodeConfig build() {
            QrCodeConfig config = super.build();
            config.validateParams();
            return config;
        }
    }

    public static QrCodeConfig.QrCodeConfigBuilder builder() {
        return new InternalBuilder();
    }

}
