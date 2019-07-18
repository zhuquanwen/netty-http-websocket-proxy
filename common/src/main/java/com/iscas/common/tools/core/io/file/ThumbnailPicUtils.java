package com.iscas.common.tools.core.io.file;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 生成缩略图工具类
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/14 18:05
 * @since jdk1.8
 */
public class ThumbnailPicUtils {
    /**
     * 私有方法，防止别人实例化使用*/
    private ThumbnailPicUtils(){}
    /**
     * 将图片生成一个缩略图
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param originalFile 原图路径
     * @param thumbnailFile 缩略图路径
     * @param thumbWidth, 缩略图宽
     * @param thumbHeight 缩略图高
     * @throws
     * @return void
     */
    public static void transform(String originalFile, String thumbnailFile, int thumbWidth, int thumbHeight) throws Exception
    {

        Image image = javax.imageio.ImageIO.read(new File(originalFile));

        double thumbRatio = (double)thumbWidth / (double)thumbHeight;
        int imageWidth    = image.getWidth(null);
        int imageHeight   = image.getHeight(null);
        double imageRatio = (double)imageWidth / (double)imageHeight;
        if (thumbRatio < imageRatio)
        {
            thumbHeight = (int)(thumbWidth / imageRatio);
        }
        else
        {
            thumbWidth = (int)(thumbHeight * imageRatio);
        }

        if(imageWidth < thumbWidth && imageHeight < thumbHeight)
        {
            thumbWidth = imageWidth;
            thumbHeight = imageHeight;
        }
        else if(imageWidth < thumbWidth) {
            thumbWidth = imageWidth;
        } else if(imageHeight < thumbHeight) {
            thumbHeight = imageHeight;
        }

        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);

        javax.imageio.ImageIO.write(thumbImage, "JPG", new File(thumbnailFile));
    }
}
