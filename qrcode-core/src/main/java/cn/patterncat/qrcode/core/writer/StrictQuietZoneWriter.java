package cn.patterncat.qrcode.core.writer;

import cn.patterncat.qrcode.core.bean.BitMatrixWrapper;
import cn.patterncat.qrcode.core.bean.DetectInfo;
import cn.patterncat.qrcode.core.util.BitMatrixUtil;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.Optional;

/**
 * Created by patterncat on 2017-10-27.
 */
public class StrictQuietZoneWriter extends DefaultQrCodeWriter{

    @Override
    public BitMatrixWrapper renderResult(QRCode code, int width, int height, int quietZone) {
        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();

        if(width < inputWidth || height < inputHeight){
            throw new IllegalArgumentException("target img size cannot contain target msg");
        }

        int outputWidth = width;
        int outputHeight = height;

        int qrWidth = inputWidth;
        int qrHeight = inputHeight;

        //这里按指定的quietZone倒推计算multiple,由于是整形计算,因此会有精度偏差
        int multiple = Math.min((outputWidth - quietZone * 2) / qrWidth, (outputHeight - quietZone * 2) / qrHeight);
        // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
        // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
        // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
        // handle all the padding from 100x100 (the actual QR) up to 200x160.
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

        BitMatrixWrapper wrapper = scaleUpQrCodeToOutputBitMatrix(input,inputWidth,inputHeight,
                outputWidth,outputHeight,multiple,
                topPadding,leftPadding);
        return wrapper;
    }
}
