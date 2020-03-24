package com.wl4g.devops.coss.natives;

import com.wl4g.devops.coss.model.metadata.BucketStatusMetaData;
import com.wl4g.devops.coss.model.metadata.ObjectsStatusMetaData;
import com.wl4g.devops.tool.common.io.FileIOUtils;
import com.wl4g.devops.tool.common.io.FileLockUtils;
import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;

import static com.wl4g.devops.coss.model.metadata.ObjectsStatusMetaData.ObjectStatusMetaData;

/**
 * @author vjay
 * @date 2020-03-19 10:00:00
 */
public class MetadataIndexManager {

    public static final String BUCKET_METADATA = "/.bucket.metadata";
    public static final String OBJECT_METADATA_DIR = "/.metadata/";
    public static final String OBJECT_METADATA_PRE = "block.";//  e.g: /.metadata/block.0

    private static final String charset = "UTF-8";

    public void createBucketMeta(String bucketPath) {
        File file = new File(bucketPath + BUCKET_METADATA);
        createBucketMeta(file);
    }

    public void addObject(String bucketPath, File file, ObjectStatusMetaData objectStatusMetaData) throws Exception {
        File lastObjectMetaFile = getLastObjectMetaFile(file.getParentFile());

        FileLockUtils.doTryLock(file, fileLock -> {
            String s = FileIOUtils.readFileToString(lastObjectMetaFile, charset);
            ObjectsStatusMetaData objectsStatusMetaData = JacksonUtils.parseJSON(s, ObjectsStatusMetaData.class);
            Map<String, ObjectStatusMetaData> objects = objectsStatusMetaData.getObjects();
            objects.put(file.getName(), objectStatusMetaData);
            String s1 = JacksonUtils.toJSONString(objectsStatusMetaData);
            FileIOUtils.writeFile(lastObjectMetaFile,s1);
            return null;
        });

        modifyBucketMetaData(bucketPath,1, Files.size(file.toPath()),0);
    }

    public void delObject(String bucketPath, File file) {
        //TODO
    }

    public void modifyObject(String bucketPath, File file, ObjectStatusMetaData objectStatusMetaData) {
        //TODO
    }

    public ObjectStatusMetaData getObject(String bucketPath, File file){
        //TODO
        return null;
    }

    private File getObjectMetaFileByKey(File parentFile){
        if(!parentFile.exists()){
            return null;
        }
        File metaDataDir = new File(parentFile.getAbsolutePath() + OBJECT_METADATA_DIR);
        File[] files = metaDataDir.listFiles();
        if(!metaDataDir.exists() || files ==null || files.length<=0){
            return null;
        }
        //TODO doing.......

        return null;
    }

    private File getLastObjectMetaFile(File parentFile) {
        File metaDataDir = new File(parentFile.getAbsolutePath() + OBJECT_METADATA_DIR);
        if(!metaDataDir.exists()){
            metaDataDir.mkdirs();
            Assert2.isTrue(metaDataDir.exists(),"make dir fail");
            return createObjectMetaFile(parentFile);
        }
        File[] files = metaDataDir.listFiles();
        if(files.length<=0){
            return createObjectMetaFile(parentFile);
        }
        File lastFile = files[0];
        int largestSuffix = 0;
        for(File file : files){
            int suffix = getSuffix(file.getName());
            if(suffix > largestSuffix){
                largestSuffix = suffix;
                lastFile = file;
            }
        }
        return lastFile;
    }


    private File createObjectMetaFile(File parentFile){
        ObjectsStatusMetaData objectsStatusMetaData = new ObjectsStatusMetaData();
        File file = new File(parentFile.getAbsolutePath() + OBJECT_METADATA_DIR + OBJECT_METADATA_PRE + 0);
        FileIOUtils.writeFile(file,JacksonUtils.toJSONString(objectsStatusMetaData),false);
        return file;
    }


    private static int getSuffix(String fileName){
        int i = fileName.lastIndexOf(".");
        String substring = fileName.substring(i + 1, fileName.length());
        try {
            return Integer.valueOf(substring);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return 0;
    }





    /**
     * Add file
     *
     * @param bucketPath
     * @param addFileNum
     * @param addFileSize
     */
    public void modifyBucketMetaData(String bucketPath, int addFileNum, long addFileSize, long requestTimes) {
        File file = new File(bucketPath + BUCKET_METADATA);
        checkBucketMetaData(file);
        try {
            FileLockUtils.doTryLock(file, fileLock -> {
                BucketStatusMetaData metadataIndex = readBucketMetaData(file);
                metadataIndex.setNumberOfDocuments(metadataIndex.getNumberOfDocuments() + addFileNum);
                metadataIndex.setStorageUsage(metadataIndex.getStorageUsage() + addFileSize);
                metadataIndex.setNumberOfRequests(metadataIndex.getNumberOfRequests() + requestTimes);
                metadataIndex.setModifyDate(System.currentTimeMillis());
                writeBucketMetaData(file, metadataIndex);
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkBucketMetaData(File file) {
        if (!file.exists()) {
            createBucketMeta(file);
        }
        Assert2.isTrue(file.exists(), "index file not exists and createBucketMeta fail");
    }

    private void createBucketMeta(File file) {
        if (file.exists()) {//needn't create metadata
            return;
        }
        BucketStatusMetaData metadataIndex = new BucketStatusMetaData();
        metadataIndex.setNumberOfDocuments(0);
        metadataIndex.setStorageUsage(0);
        long now = System.currentTimeMillis();
        metadataIndex.setCreateDate(now);
        metadataIndex.setModifyDate(now);
        writeBucketMetaData(file, metadataIndex);
    }

    private void writeBucketMetaData(File file, BucketStatusMetaData metadataIndex) {
        String s = JacksonUtils.toJSONString(metadataIndex);
        FileIOUtils.writeFile(file, s, Charset.forName(charset), false);
    }

    public BucketStatusMetaData readBucketMetaData(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        String s = FileIOUtils.readFileToString(file, charset);
        return JacksonUtils.parseJSON(s, BucketStatusMetaData.class);
    }


}
