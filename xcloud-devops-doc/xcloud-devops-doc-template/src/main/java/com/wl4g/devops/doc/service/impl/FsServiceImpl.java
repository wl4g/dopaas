/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.doc.service.impl;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.lang.DateUtils2;
import com.wl4g.devops.doc.bean.FileInfo;
import com.wl4g.devops.doc.config.FsProperties;
import com.wl4g.devops.doc.service.FsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FsServiceImpl implements FsService {

    @Autowired
    private FsProperties fsProperties;

    @Override
    public List<FileInfo> getTreeFiles(String subPath) {
        File basePath = new File(fsProperties.getBasePath() + subPath);
        List<FileInfo> fileInfos = new ArrayList<>();
        getChildren(basePath, fileInfos);
        return fileInfos;
    }

    private void getChildren(File path, List<FileInfo> fileInfos){
        File[] files = path.listFiles();
        if(files == null || files.length<=0){
            return;
        }
        for(File file : files){
            FileInfo fileInfo = new FileInfo();
            fileInfo.setPath(getRelativePath(file.getAbsolutePath()));
            fileInfo.setFileName(file.getName());
            fileInfo.setDir(file.isDirectory());
            fileInfos.add(fileInfo);
            getChildren(file, fileInfo.getChildren());
        }
    }

    @Override
    public List<FileInfo> getFilesByParent(String parentPath) {

        File file;
        if (StringUtils.isNotBlank(parentPath)) {
            file = new File(fsProperties.getBasePath() + parentPath);
        } else {
            file = new File(fsProperties.getBasePath());
        }

        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        List<FileInfo> fileInfos = new ArrayList<>();
        for (File f : files) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setPath(getRelativePath(f.getAbsolutePath()));
            fileInfo.setFileName(f.getName());
            fileInfo.setDir(f.isDirectory());
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }

    @Override
    public FileInfo getFileInfo(String path) throws IOException {
        File file = new File(fsProperties.getBasePath() + path);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(getRelativePath(file.getAbsolutePath()));
        fileInfo.setFileName(file.getName());
        fileInfo.setDir(file.isDirectory());
        fileInfo.setUpdateTime(DateUtils2.formatDateTime(new Date(file.lastModified())));
        if (file.isFile()) {
            fileInfo.setContent(FileIOUtils.readFileToString(file, "UTF-8"));
        }
        return fileInfo;
    }

    @Override
    public void delFile(String path) throws IOException {
        File file = new File(fsProperties.getBasePath() + path);
        if(file.isFile()){
            FileIOUtils.forceDelete(file);
        }else{
            FileIOUtils.deleteDirectory(file);
        }
    }

    @Override
    public void addDir(String path) throws IOException {
        File file = new File(fsProperties.getBasePath() + path);
        boolean newFile = file.mkdirs();
        if (!newFile || !file.exists()) {
            throw new IOException("create new dir fail");
        }
    }

    @Override
    public void addFile(String path) throws IOException {
        File file = new File(fsProperties.getBasePath() + path);
        boolean newFile = file.createNewFile();
        if (!newFile || !file.exists()) {
            throw new IOException("create new file fail");
        }
    }

    @Override
    public void renameFile(String path, String toPath) throws IOException {
        File file = new File(fsProperties.getBasePath() + path);
        File toFile = new File(fsProperties.getBasePath() + toPath);
        boolean b = file.renameTo(toFile);
        if(!b || !toFile.exists()){
            throw new IOException("rename file fail");
        }
    }


    @Override
    public void saveFile(String path, String content) {
        File file = new File(fsProperties.getBasePath() + path);
        if (!file.exists()) {
            return;
        }
        FileIOUtils.writeFile(file, content, false);
    }

    @Override
    public String uploadFile(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();// 文件名
        path = fsProperties.getBasePath() + path + "/" + fileName;
        saveFile(file, path);
        return fileName;
    }

    /**
     * @param path 相对路径
     * @return
     * @throws IOException
     */
    @Override
    public ResponseEntity<FileSystemResource> downloadFile(String path) throws IOException {
        File file = new File(fsProperties.getBasePath() + "/" + path);
        if(file.isDirectory()){//TODO 暂不支持文件夹下载
            throw new IOException("not support download dir yet");
        }
        return downloadFile(file);
    }

    private void saveFile(MultipartFile file, String localPath) {
        Assert.notNull(file, "文件为空");
        File dest = new File(localPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseEntity<FileSystemResource> downloadFile(File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }


    private String getRelativePath(String absolutePath) {
        String baseFilePath = fsProperties.getBasePath();
        if (absolutePath.startsWith(baseFilePath)) {
            return absolutePath.substring(baseFilePath.length());
        } else {
            return absolutePath;
        }
    }

}