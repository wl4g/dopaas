package cn.edu.njupt.dto;

import java.util.List;

/**
 * 识别请求对象
 * {
 * 	"img": //图片list
 * ["/home/stitp/javaData/cut/259152861412872192/259152861412872192-0.jpg", "/home/stitp/javaData/cut/259152861412872192/259152861412872192-1.jpg", "/home/stitp/javaData/cut/259152861412872192/259152861412872192-2.jpg", "/home/stitp/javaData/cut/259152861412872192/259152861412872192-3.jpg", "/home/stitp/javaData/cut/259152861412872192/259152861412872192-4.jpg", "/home/stitp/javaData/cut/259152861412872192/259152861412872192-5.jpg"],
 * “score”: [20,10,10,30,15,15,100]，//小题总分
 *  "count":"6",//图片数
 *  "key":"45WFEQ6",//用户唯一标识
 *  “serial”:”AW412F4E234”,//本次请求的序列号
 *  "category": "1"//网络类别,目前设置为1，表示识别数字0-19
 * }
 */
public class PostObj {
    private List<String> img;
    private List<Integer> score;
    private Integer count;
    private String key;
    private String serial;
    private String category;

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }

    public List<Integer> getScore() {
        return score;
    }

    public void setScore(List<Integer> score) {
        this.score = score;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"img\":")
                .append(img);
        sb.append(",\"score\":")
                .append(score);
        sb.append(",\"count\":")
                .append(count);
        sb.append(",\"key\":\"")
                .append(key).append('\"');
        sb.append(",\"serial\":\"")
                .append(serial).append('\"');
        sb.append(",\"category\":\"")
                .append(category).append('\"');
        sb.append('}');
        return sb.toString();
    }


}
