package com.wl4g.devops.iam.captcha.jigsaw;

import java.awt.image.BufferedImage;

/**
 * @author vjay
 * @date 2019-08-29 11:07:00
 */
public class Image {
    private int id;

    private BufferedImage moveImage;

    private BufferedImage backImage;

    private long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BufferedImage getMoveImage() {
        return moveImage;
    }

    public void setMoveImage(BufferedImage moveImage) {
        this.moveImage = moveImage;
    }

    public BufferedImage getBackImage() {
        return backImage;
    }

    public void setBackImage(BufferedImage backImage) {
        this.backImage = backImage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class ImageInfo extends Image {

        private int x;

        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

    }
}
