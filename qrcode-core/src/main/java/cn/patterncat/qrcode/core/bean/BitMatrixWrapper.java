package cn.patterncat.qrcode.core.bean;

import com.google.zxing.common.BitMatrix;
import lombok.Builder;
import lombok.Data;

/**
 * Created by patterncat on 2017-10-28.
 */
@Data
@Builder
public class BitMatrixWrapper {

    private BitMatrix bitMatrix;

    private BitMatrix detectOutMatrix;

    private BitMatrix detectInMatrix;

//    private int quietZone;
//
//    private int multiple;
//
//    private QRCode qrCode;
}
