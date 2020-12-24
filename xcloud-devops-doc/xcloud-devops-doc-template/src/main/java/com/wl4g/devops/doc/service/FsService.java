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
package com.wl4g.devops.doc.service;

import com.wl4g.devops.doc.bean.FileInfo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FsService {

    List<FileInfo> getTreeFiles();

    List<FileInfo> getFilesByParent(String parentPath);

    FileInfo getFileInfo(String path) throws IOException;

    void delFile(String path) throws IOException;

    void addDir(String path) throws IOException;

    void addFile(String path) throws IOException;

    void renameFile(String path,String toPath) throws IOException;

    void saveFile(String path, String content);

    String uploadFile(MultipartFile img);

    ResponseEntity<FileSystemResource> downloadFile(String path) throws IOException;

}