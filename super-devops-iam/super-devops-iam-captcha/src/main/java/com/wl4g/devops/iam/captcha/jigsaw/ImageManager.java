package com.wl4g.devops.iam.captcha.jigsaw;

import com.wl4g.devops.iam.captcha.jigsaw.Image.ImageInfo;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author vjay
 * @date 2019-08-29 09:37:00
 */
@Service
public class ImageManager {

    //cache size
    private static int cacheSize = 100;

    //store random size
    private static int storeSize = 4;

    //file base path
    private static String fileBasePath = "/Users/vjay/Downloads/images/Pic";

    //file base url
    private static String urlBase = "http://vps.vjay.pw/image/Pic";

    //suffic
    private static String suffix = ".jpg";

    //is image get from net
    private static boolean isGetFromNet = false;

    //cache
    public static List<ImageInfo> list = new Vector<>();

    public static Map<String,ImageInfo>  map = new HashMap<>();

    public ImageInfo getImageRandom() throws Exception {
        return getImageRandom(null);
    }

    public synchronized ImageInfo getImageRandom(String uuid) throws Exception {
        if(map.get(uuid)!=null){
            return map.get(uuid);
        }else if (RandomUtils.nextBoolean() && list.size() > 0) {//get from cache
            int index = RandomUtils.nextInt(0, list.size());
            ImageInfo imageInfo = list.get(index);
            map.put(uuid,imageInfo);
            return imageInfo;
        } else {
            ImageUtil imageUtil = new ImageUtil();
            ImageInfo imageInfo = null;
            if (isGetFromNet) {
                imageInfo = imageUtil.cutImageHttp(urlBase + RandomUtils.nextInt(0, storeSize) + suffix);
            } else {
                imageInfo = imageUtil.cutImageFile(fileBasePath + RandomUtils.nextInt(0, storeSize) + suffix);
            }
            if (imageInfo == null) {
                throw new Exception("get image fail");
            }
            list.add(imageInfo);
            while (list.size() > cacheSize) {
                list.remove(0);
            }
            map.put(uuid,imageInfo);
            return imageInfo;
        }
    }

    public void cleanCache(){

    }



}
