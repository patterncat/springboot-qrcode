package cn.patterncat.qrcode.core.writer;

import cn.patterncat.qrcode.core.bean.BitMatrixInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.Map;

/**
 * 改造core-3.3.0-sources.jar!/com/google/zxing/Writer.java
 * Created by patterncat on 2017-10-28.
 */
public interface QrCodeMatrixWriter {

    /**
     * Encode a barcode using the default settings.
     *
     * @param contents The contents to encode in the barcode
     * @param format The barcode format to generate
     * @param width The preferred width in pixels
     * @param height The preferred height in pixels
     * @return {@link BitMatrix} representing encoded barcode image
     * @throws WriterException if contents cannot be encoded legally in a format
     */
    BitMatrixInfo encode(String contents, BarcodeFormat format, int width, int height)
            throws WriterException;

    /**
     * @param contents The contents to encode in the barcode
     * @param format The barcode format to generate
     * @param width The preferred width in pixels
     * @param height The preferred height in pixels
     * @param hints Additional parameters to supply to the encoder
     * @return {@link BitMatrix} representing encoded barcode image
     * @throws WriterException if contents cannot be encoded legally in a format
     */
    BitMatrixInfo encode(String contents,
                         BarcodeFormat format,
                         int width,
                         int height,
                         Map<EncodeHintType,?> hints)
            throws WriterException;

    BitMatrixInfo renderResult(QRCode code, int width, int height, int quietZone);
}
