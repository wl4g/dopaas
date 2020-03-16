package com.wl4g.devops.erm.controller;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.erm.service.FsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/fs")
public class FsController {

	@Autowired
	private FsService fsService;

	@PostMapping(value = "/uploadImg")
	public RespBase<?> uploadImg(@RequestParam(value = "img") MultipartFile img) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(fsService.uploadImg(img));
		return resp;
	}

	@RequestMapping(value = "/downloadImg/{date}/{fileName:.+}") // produces =
																	// MediaType.TEXT_HTML
	public byte[] downloadImg(@PathVariable String date, @PathVariable String fileName) throws IOException {
		return fsService.downloadImg("/" + date + "/" + fileName);
	}

	@PostMapping(value = "/uploadFile")
	public RespBase<?> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		String fileUrl = fsService.uploadFile(file);
		resp.setData(fileUrl);
		return resp;
	}

	@RequestMapping(value = "/downloadFile/{date}/{fileName:.+}")
	public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String date, @PathVariable String fileName)
			throws IOException {
		return fsService.downloadFile("/" + date + "/" + fileName);
	}

}
