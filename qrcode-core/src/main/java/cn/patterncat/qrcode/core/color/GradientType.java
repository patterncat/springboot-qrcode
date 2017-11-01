package cn.patterncat.qrcode.core.color;

import java.awt.*;

/**
 * Created by patterncat on 2017-11-01.
 */
public enum GradientType {
    TOP_DOWN {
        @Override
        public GradientPaint build(int startX, int startY, int endX, int endY, Color colorFrom, Color colorTo) {
            return new GradientPaint(startX, startY, colorFrom, startX, endY, colorTo); //从上到下渐变;
        }
    }, LEFT_RIGHT {
        @Override
        public GradientPaint build(int startX, int startY, int endX, int endY, Color colorFrom, Color colorTo) {
            return new GradientPaint(startX, startY, colorFrom, endX, startY, colorTo); //从从左到右渐变;
        }
    }, LEFT_TOP_RIGHT_DOWN {
        @Override
        public GradientPaint build(int startX, int startY, int endX, int endY, Color colorFrom, Color colorTo) {
            return new GradientPaint(startX, startY, colorFrom, endX, endY, colorTo); //从左上到右下渐变;
        }
    };

    public abstract GradientPaint build(int startX, int startY, int endX, int endY, Color colorFrom, Color colorTo);
}
