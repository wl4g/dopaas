package com.wl4g.devops.common.utils.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author vjay
 * @date 2019-10-12 11:31:00
 */
public class FileReadUtil {


    public static List<String> readFile(String path, int beginLine,int limit) {
        FileInputStream inputStream = null;
        Scanner sc = null;
        List<String> lines = new ArrayList<>();
        try {
            inputStream = new FileInputStream(path);
            sc = new Scanner(inputStream);

            int index = 0;
            int count = 0;

            while (count<limit&&sc.hasNextLine()) {

                if (index >= beginLine) {
                    count++;
                    String line = sc.nextLine();
                    lines.add(line);
                } else {
                    sc.nextLine();
                }
                index++;
            }
        } catch (IOException e) {
            System.out.println("FileReader IOException!");
            e.printStackTrace();
        }finally {
            try {
                if(null!=inputStream){
                    inputStream.close();
                }
                if(null!=sc){
                    sc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static void main(String[] args){
        String path = "/Users/vjay/Downloads/testline.txt";
        List<String> strings = readFile(path, 7, 5);
        System.out.println(strings);
    }
}
