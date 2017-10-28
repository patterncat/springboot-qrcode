package cn.patterncat.qrcode.core.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by patterncat on 2017-10-26.
 */
public class ImgUtil {

    /**
     * 将foreImg叠加到bgImg上
     * 直接修改bgImg
     * @param foreImg
     * @param bgImg
     * @param widthRatio
     * @param heightRatio
     * @return
     * @throws IOException
     */
    public static void coverImage(BufferedImage foreImg,BufferedImage bgImg,int widthRatio,int heightRatio) throws IOException {
        int bgImgWidth = bgImg.getWidth();
        int bgImgHeight = bgImg.getHeight();

        int w = bgImgWidth / widthRatio;
        int h = bgImgHeight / heightRatio;
        int x = (bgImgWidth - w) / 2;
        int y = (bgImgHeight - h) / 2;

        Graphics2D g2 = bgImg.createGraphics();
        //这里src是foreImg,dst是bgImg
        //这只叠加模式,Src就是覆盖部分只显示src,不显示dst
        g2.setComposite(AlphaComposite.Src);
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
        //将foreImg压缩到w,h大小,然后在bgImg的x,y位置覆盖
        g2.drawImage(foreImg, x, y, w, h, null);
        g2.dispose();
        foreImg.flush();
    }

    /**
     * 添加圆角边框
     * @param image
     * @param radius
     * @param borderSize
     * @param borderColor
     * @return
     */
    public static BufferedImage addRoundedBorder(BufferedImage image,int radius,int borderSize,Color borderColor){
        int w = image.getWidth() + borderSize*2;
        int h = image.getHeight() + borderSize*2;
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor == null ? Color.WHITE : borderColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, borderSize, borderSize, null);
        g2.dispose();
        return output;
    }

    /**
     * 将图形圆角化
     * @param image
     * @param radius 圆角的弧度
     * @return
     */
    public static BufferedImage roundImageCorner(BufferedImage image, int radius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }

    /**
     * 从path或url读取图片
     * 从网络读取的话,这里是没有显示设置超时时间的,需要注意
     * @param location
     * @return
     * @throws IOException
     */
    public static BufferedImage fromPathOrUrl(String location) throws IOException {
        if(location.startsWith("http")){
            return ImageIO.read(new URL(location));
        }
        return ImageIO.read(new File(location));
    }
}
