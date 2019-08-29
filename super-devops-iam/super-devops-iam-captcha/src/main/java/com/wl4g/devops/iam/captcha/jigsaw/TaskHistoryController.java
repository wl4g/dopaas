/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.captcha.jigsaw;

import com.wl4g.devops.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

/**
 * Task History controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@Controller
@RequestMapping("/public")
public class TaskHistoryController extends BaseController {

	@Autowired
	private ImageManager imageManager;


	@RequestMapping(value = "/image1",produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getImage1(String uuid) throws Exception {
		Image.ImageInfo imageRandom = imageManager.getImageRandom(uuid);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(imageRandom.getBackImage(), "png",out);
		return out.toByteArray();
	}

	@RequestMapping(value = "/image2",produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getImage2(String uuid) throws Exception {
		Image.ImageInfo imageRandom = imageManager.getImageRandom(uuid);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(imageRandom.getMoveImage(), "png",out);
		return out.toByteArray();
	}

	@RequestMapping(value = "/imageY")
	@ResponseBody
	public int getImageInfo(String uuid) throws Exception {
		Image.ImageInfo imageRandom = imageManager.getImageRandom(uuid);
		return imageRandom.getY();

	}

}