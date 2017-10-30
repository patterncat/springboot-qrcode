package cn.patterncat.qrcode.core.writer;

import cn.patterncat.qrcode.core.bean.BitMatrixInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.Map;

/**
 * 从com.google.zxing.qrcode.QRCodeWriter拷贝基本方法过来
 * 由于原始类是final的,不好扩展,这里扩展一份修改renderResult方法修饰符方便子类重写
 * zxing版本升级的时候,注意下这个类的改动
 * Created by patterncat on 2017-10-27.
 */
public class DefaultQrCodeWriterQrCode implements QrCodeMatrixWriter {

    public static final int QUIET_ZONE_SIZE = 4;

    @Override
    public BitMatrixInfo encode(String contents, BarcodeFormat format, int width, int height)
            throws WriterException {

        return encode(contents, format, width, height, null);
    }

    @Override
    public BitMatrixInfo encode(String contents,
                                BarcodeFormat format,
                                int width,
                                int height,
                                Map<EncodeHintType, ?> hints) throws WriterException {

        if (contents.isEmpty()) {
            throw new IllegalArgumentException("Found empty contents");
        }

        if (format != BarcodeFormat.QR_CODE) {
            throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
        }

        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +
                    height);
        }

        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        int quietZone = QUIET_ZONE_SIZE;
        if (hints != null) {
            if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
                errorCorrectionLevel = ErrorCorrectionLevel.valueOf(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
            }
            if (hints.containsKey(EncodeHintType.MARGIN)) {
                quietZone = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
            }
        }

        QRCode code = Encoder.encode(contents, errorCorrectionLevel, hints);
        return renderResult(code, width, height, quietZone);
    }

    // Note that the input matrix uses 0 == white, 1 == black, while the output matrix uses
    // 0 == black, 255 == white (i.e. an 8 bit greyscale bitmap).
    public BitMatrixInfo renderResult(QRCode code, int width, int height, int quietZone) {
        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int qrWidth = inputWidth + (quietZone * 2);
        int qrHeight = inputHeight + (quietZone * 2);
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
        // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
        // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
        // handle all the padding from 100x100 (the actual QR) up to 200x160.
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

        BitMatrixInfo bitMatrixInfo = buildOutputMatrixInfo(code,outputWidth,outputHeight,
                multiple,topPadding,leftPadding);
        return bitMatrixInfo;
    }

    protected BitMatrixInfo buildOutputMatrixInfo(QRCode qrCode,
                                                  int outputWidth, int outputHeight, int multiple,
                                                  int topPadding, int leftPadding){
        return BitMatrixInfo.builder()
                .multiple(multiple)
                .outputWidth(outputWidth)
                .outputHeight(outputHeight)
                .topPadding(topPadding)
                .leftPadding(leftPadding)
                .qrCode(qrCode)
                .build();
    }
}
