package cn.patterncat.qrcode.core.bean;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import lombok.Builder;
import lombok.Data;

/**
 * Created by patterncat on 2017-10-28.
 */
@Data
@Builder
public class BitMatrixInfo {

//    private BitMatrix bitMatrix;

//    private BitMatrix detectOutMatrix;

//    private BitMatrix detectInMatrix;

    private int topPadding;

    private int leftPadding;

    private int multiple;

    private int outputWidth;

    private int outputHeight;

    private QRCode qrCode;
}
