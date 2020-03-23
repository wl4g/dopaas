package com.wl4g.devops.coss.natives;

import com.wl4g.devops.tool.common.io.FileIOUtils;
import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;

/**
 * @author vjay
 * @date 2020-03-19 10:00:00
 */
public class MetadataIndexManager {

    public static final String indexFileName = "/.bucket.metadata";
    private static final String charset = "UTF-8";

    public void create(String bucketPath){
        File file = new File(bucketPath + indexFileName);
        create(file);
    }

    private void create(File file) {
        if (file.exists()) {
            return;
        }
        MetadataIndex metadataIndex = new MetadataIndex();
        metadataIndex.setNumberOfDocuments(0);
        metadataIndex.setStorageUsage(0);
        long now = System.currentTimeMillis();
        metadataIndex.setCreateDate(now);
        metadataIndex.setModifyDate(now);
        write(file, metadataIndex);
    }

    public void addFile(String bucketPath, int addFileNum, long addFileSize) {
        File file = new File(bucketPath + indexFileName);
        check(file);
        ///////

        //给该文件加锁
        RandomAccessFile randomAccessFile = null;
        FileChannel fileChannel= null;
        FileLock fileLock=null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel=randomAccessFile.getChannel();
            fileLock = fileChannel.lock();

            MetadataIndex metadataIndex = read(file);
            metadataIndex.setNumberOfDocuments(metadataIndex.getNumberOfDocuments() + addFileNum);
            metadataIndex.setStorageUsage(metadataIndex.getStorageUsage() + addFileSize);
            metadataIndex.setModifyDate(System.currentTimeMillis());
            write(file, metadataIndex);


        } catch (IOException e) {
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

    public void addRequest(String bucketPath, long requestTimes) {
        File file = new File(bucketPath + indexFileName);
        check(file);
        //给该文件加锁
        RandomAccessFile randomAccessFile = null;
        FileChannel fileChannel= null;
        FileLock fileLock=null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel=randomAccessFile.getChannel();
            fileLock = fileChannel.lock();
            MetadataIndex metadataIndex = read(file);
            metadataIndex.setNumberOfRequests(metadataIndex.getNumberOfRequests() + requestTimes);
            metadataIndex.setModifyDate(System.currentTimeMillis());
            write(file, metadataIndex);
        } catch (IOException e) {
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

    private void check(File file){
        if(!file.exists()){
            create(file);
        }
        Assert2.isTrue(file.exists(),"index file not exists and create fail");
    }

    private void write(File file, MetadataIndex metadataIndex) {
        String s = JacksonUtils.toJSONString(metadataIndex);
        FileIOUtils.writeFile(file, s, Charset.forName(charset), false);
    }

    public MetadataIndex read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        String s = FileIOUtils.readFileToString(file, charset);
        return JacksonUtils.parseJSON(s, MetadataIndex.class);
    }

    public static class MetadataIndex implements Serializable {
        private static final long serialVersionUID = 381411777614066880L;

        private long storageUsage = 0;
        private long numberOfRequests = 0;
        private long numberOfDocuments = 0;
        private long createDate = 0;
        private long modifyDate = 0;

        public long getStorageUsage() {
            return storageUsage;
        }

        public void setStorageUsage(long storageUsage) {
            this.storageUsage = storageUsage;
        }

        public long getNumberOfRequests() {
            return numberOfRequests;
        }

        public void setNumberOfRequests(long numberOfRequests) {
            this.numberOfRequests = numberOfRequests;
        }

        public long getNumberOfDocuments() {
            return numberOfDocuments;
        }

        public void setNumberOfDocuments(long numberOfDocuments) {
            this.numberOfDocuments = numberOfDocuments;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }
    }

}
