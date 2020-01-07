package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author vjay
 * @date 2020-01-06 17:16:00
 */
@RestController
@RequestMapping("/fs")
public class FsController {

	@Autowired
	protected CiCdProperties config;

	@PostMapping(value = "/upload")
	public RespBase<?> upload(@RequestParam(value = "file") MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		String fileName = file.getOriginalFilename();// 文件名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));// 后缀名
		fileName = UUID.randomUUID() + suffixName;// 新文件名
		String path = config.getTestReport().getUploadPath() + "/" + fileName;
		saveFile(file, path);
        resp.setData(config.getTestReport().getDownloadUrl()+"/"+fileName);
		return resp;
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

    @RequestMapping(value = "/download/{fileName:.+}")
    public ResponseEntity<FileSystemResource> exportXls(@PathVariable String fileName) {
        File file = new File(config.getTestReport().getUploadPath()+"/"+fileName);
        return export(file);
    }


    public ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        if(!file.exists()){
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        String suffixName = file.getName().substring(file.getName().lastIndexOf("."));// 后缀名
        headers.add("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + suffixName);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }

}
