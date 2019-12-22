package cn.edu.njupt.dto;

import java.io.Serializable;
import java.util.List;

/**
 * list4 = post('http://stitp:5555/todos',json.dumps(
 *             {   'img': img,
 *                 'key': '012345',
 *                 'score': score,
 *                 'count': len(img),
 *                 'serial':"0011223344"+str(i),
 *                 'category': '1',
 *                 'proportion':3
 *                 }))
 */
public class IdentifyRequestHead implements Serializable{
    private List<String> img;
    private List<Integer> score;
    private String key;
    private String serial;
    private String category;
    private Integer proportion;
    private Integer count;

    public IdentifyRequestHead() {
    }

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

    public Integer getProportion() {
        return proportion;
    }

    public void setProportion(Integer proportion) {
        this.proportion = proportion;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"img\":")
                .append(img);
        sb.append(",\"score\":")
                .append(score);
        sb.append(",\"key\":\"")
                .append(key).append('\"');
        sb.append(",\"serial\":\"")
                .append(serial).append('\"');
        sb.append(",\"category\":\"")
                .append(category).append('\"');
        sb.append(",\"proportion\":")
                .append(proportion);
        sb.append(",\"count\":")
                .append(count);
        sb.append('}');
        return sb.toString();
    }
}
