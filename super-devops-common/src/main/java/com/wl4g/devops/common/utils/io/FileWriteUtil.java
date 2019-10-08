package com.wl4g.devops.common.utils.io;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author vjay
 * @date 2019-09-24 10:17:00
 */
public class FileWriteUtil {

    public static void writerFile(String result, File file) {
        writerFile(result, file, true);
    }

    public static void writerFile(String result, File file, boolean isAppend) {
        if (null == result || file == null) {
            return;
        }
        FileWriter writer = null;
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists() || !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            writer = new FileWriter(file, isAppend);
            writer.write(result);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String path = "/Users/vjay/Downloads/filetest/1.txt";
        writerFile("test\n", new File(path));
    }

}
