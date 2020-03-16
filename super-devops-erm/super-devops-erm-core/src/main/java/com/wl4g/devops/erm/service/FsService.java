package com.wl4g.devops.erm.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FsService {

    String uploadFile(MultipartFile img);

    ResponseEntity<FileSystemResource> downloadFile(String path) throws IOException;

    String uploadImg(MultipartFile img);

    byte[] downloadImg(String path) throws IOException;
}
