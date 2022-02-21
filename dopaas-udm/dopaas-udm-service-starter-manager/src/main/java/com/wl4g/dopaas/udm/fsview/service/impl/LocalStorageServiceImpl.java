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
package com.wl4g.dopaas.udm.fsview.service.impl;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.lang.DateUtils2;
import com.wl4g.dopaas.udm.fsview.bean.FileInfo;
import com.wl4g.dopaas.udm.fsview.config.FsViewerProperties;
import com.wl4g.dopaas.udm.fsview.service.FsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wl4g.dopaas.udm.fsview.util.PathUtils.splicePath;

//@Service
@Configuration
@ConditionalOnProperty(name="doc.storage-type",havingValue = "local")
public class LocalStorageServiceImpl implements FsService {

    @Autowired
    private FsViewerProperties fsViewerProperties;

    @Override
    public List<FileInfo> getTreeFiles(String subPath) {
        File basePath = new File(fsViewerProperties.getBasePath() + subPath);
        List<FileInfo> fileInfos = new ArrayList<>();
        getChildren(basePath, fileInfos, subPath);
        return fileInfos;
    }

    private void getChildren(File path, List<FileInfo> fileInfos,String subPath){
        File[] files = path.listFiles();
        if(files == null || files.length<=0){
            return;
        }
        for(File file : files){
            FileInfo fileInfo = new FileInfo();
            fileInfo.setPath(getRelativePath(file.getAbsolutePath(),subPath));
            fileInfo.setFileName(file.getName());
            fileInfo.setDir(file.isDirectory());
            fileInfos.add(fileInfo);
            getChildren(file, fileInfo.getChildren(),subPath);
        }
    }

    @Override
    public FileInfo getFileInfo(String path, String subPath) throws IOException {
        File file = new File(fsViewerProperties.getBasePath() + splicePath(subPath , path));
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(getRelativePath(file.getAbsolutePath(),subPath));
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
        File file = new File(fsViewerProperties.getBasePath() + path);
        if(file.isFile()){
            FileIOUtils.forceDelete(file);
        }else{
            FileIOUtils.deleteDirectory(file);
        }
    }

    @Override
    public void addDir(String path) throws IOException {
        File file = new File(fsViewerProperties.getBasePath() + path);
        boolean newFile = file.mkdirs();
        if (!newFile || !file.exists()) {
            throw new IOException("create new dir fail");
        }
    }

    @Override
    public void addFile(String path) throws IOException {
        File file = new File(fsViewerProperties.getBasePath() + path);
        boolean newFile = file.createNewFile();
        if (!newFile || !file.exists()) {
            throw new IOException("create new file fail");
        }
    }

    @Override
    public void renameFile(String path, String toPath) throws IOException {
        File file = new File(fsViewerProperties.getBasePath() + path);
        File toFile = new File(fsViewerProperties.getBasePath() + toPath);
        boolean b = file.renameTo(toFile);
        if(!b || !toFile.exists()){
            throw new IOException("rename file fail");
        }
    }


    @Override
    public void saveFile(String path, String content) {
        File file = new File(fsViewerProperties.getBasePath() + path);
        if (!file.exists()) {
            return;
        }
        FileIOUtils.writeFile(file, content, false);
    }

    @Override
    public String uploadFile(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();// 文件名
        path = fsViewerProperties.getBasePath() + path + "/" + fileName;
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
        File file = new File(fsViewerProperties.getBasePath() + "/" + path);
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


    private String getRelativePath(String absolutePath, String subPath) {
        String baseFilePath = splicePath(fsViewerProperties.getBasePath(),subPath);
        if (absolutePath.startsWith(baseFilePath)) {
            return absolutePath.substring(baseFilePath.length());
        } else {
            return absolutePath;
        }
    }

}