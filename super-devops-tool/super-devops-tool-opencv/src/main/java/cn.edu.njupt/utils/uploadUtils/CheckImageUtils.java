package cn.edu.njupt.utils.uploadUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 检测图像的工具类
 */
public class CheckImageUtils {
    //日志
    private static final Logger logger = (Logger) LoggerFactory.getLogger(CheckImageUtils.class);

    /**
     * 检测上传的图像是不是图像
     * @param file
     * @return
     */
    public static boolean checkImage(MultipartFile file){
        boolean b = true;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null || image.getWidth() <= 0 || image.getHeight() <= 0){
                b = false;
                logger.error("checkImage: 上传的不是图像");
            }
        } catch (IOException e) {
            b = false;
            logger.error("checkImage: 上传的不是图像");
        }
        return b;
    }
}
