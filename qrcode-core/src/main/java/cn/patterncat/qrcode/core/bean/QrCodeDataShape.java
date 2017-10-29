package cn.patterncat.qrcode.core.bean;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by patterncat on 2017-10-29.
 */
public enum QrCodeDataShape {

    RECT {
        @Override
        public void draw(Graphics2D g2, int x, int y, int w, int h) {
            g2.fillRect(x, y, w, h);
        }
    },CIRCLE {
        @Override
        public void draw(Graphics2D g2, int x, int y, int w, int h) {
            Ellipse2D.Float shape = new Ellipse2D.Float(x, y, w, h);
            g2.fill(shape);
        }
    },TRIANGLE {
        @Override
        public void draw(Graphics2D g2, int x, int y, int w, int h) {
            int[] xpoints = {x, x + (w >> 1), x + w};
            int[] ypoints= {y + w, y, y + w};
            Polygon p = new Polygon(xpoints,ypoints,3);
            g2.fillPolygon(p);
        }
    };

    public abstract void draw(Graphics2D g2, int x, int y, int w, int h);
}
