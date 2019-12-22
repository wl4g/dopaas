package cn.edu.njupt.bean;

import java.io.Serializable;

/**
 * 图片上传的一些信息
 */
public class UploadImageInfo implements Serializable {
    private String imgName;//图片名字
    private String url;//图片网址
    private String absolutePath;//图片绝对路径

    public UploadImageInfo(){}

    public UploadImageInfo(String imgName, String url, String absolutePath) {
        this.imgName = imgName;
        this.url = url;
        this.absolutePath = absolutePath;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"imgName\":\"")
                .append(imgName).append('\"');
        sb.append(",\"url\":\"")
                .append(url).append('\"');
        sb.append(",\"absolutePath\":\"")
                .append(absolutePath).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
