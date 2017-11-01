package cn.patterncat.qrcode.core.color;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by patterncat on 2017-11-01.
 */
public class GradientColor extends BufferedImage{

    public GradientColor(int width, int height, int imageType,
                         GradientType type, Color startColor, Color endColor) {
        super(width, height, imageType);
        GradientPaint grad = type.build(0,0,width,height, startColor, endColor);

        Graphics2D g2 = (Graphics2D) getGraphics();
        g2.setPaint(grad);
        g2.fillRect(0,0,width,height);
    }
}
