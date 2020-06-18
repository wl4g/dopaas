package com.wl4g.devops.coss.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.wl4g.devops.components.tools.common.io.FileIOUtils;

/**
 * @author vjay
 * @date 2020-03-24 14:59:00
 */
public class FileLockTests {

    public static void main(String[] args){
        MyThread mThread1=new MyThread();
        MyThread mThread2=new MyThread();
        mThread1.start();
        mThread2.start();
    }

    public static class MyThread extends Thread {

        public void run() {
            File file = new File("/Users/vjay/Downloads/testFileLock");
            //给该文件加锁
            RandomAccessFile randomAccessFile = null;
            FileChannel fileChannel= null;
            FileLock fileLock=null;
            try {
                randomAccessFile = new RandomAccessFile(file, "rw");
                fileChannel=randomAccessFile.getChannel();

                //fileChannel = new FileOutputStream(file).getChannel();

                fileLock = fileChannel.lock();
                //fileLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);

                for(int i = 0; i < 5; i++){
                    String str = Thread.currentThread().getName()+"---"+i + "\n";
                    System.out.println(str);
                    FileIOUtils.writeFile(file, str);
                    Thread.sleep(1000);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileLock != null) {
                        fileLock.release();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
