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
package com.wl4g.devops.erm.service;

import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@SpringBootFeignClient("fsService")
@RequestMapping("/fs")
public interface FsService {

    @RequestMapping(value = "/uploadFile", method = POST)
    String uploadFile(@RequestBody MultipartFile img);

    @RequestMapping(value = "/downloadFile", method = POST)
    ResponseEntity<FileSystemResource> downloadFile(@RequestParam(name="path",required=false) String path) throws IOException;

    @RequestMapping(value = "/uploadImg", method = POST)
    String uploadImg(@RequestBody MultipartFile img);

    @RequestMapping(value = "/downloadImg", method = POST)
    byte[] downloadImg(@RequestParam(name="path",required=false) String path) throws IOException;
}