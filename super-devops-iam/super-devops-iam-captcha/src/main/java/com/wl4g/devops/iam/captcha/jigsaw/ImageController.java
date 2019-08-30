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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Task History controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@Controller
@RequestMapping("/public")
public class ImageController extends BaseController {

	@Autowired
	private ImageManager imageManager;

	private static String baseImgeUrl = "http://localhost:14040/iam-server/public/";


	@RequestMapping(value = "/image1",produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getImage1(String uuid) throws Exception {
		Assert.hasText(uuid,"uuid is null");
		Image.ImageInfo imageRandom = imageManager.getImageRandom(uuid);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(imageRandom.getBackImage(), "png",out);
		return out.toByteArray();
	}

	@RequestMapping(value = "/image2",produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseBody
	public byte[] getImage2(String uuid) throws Exception {
		Assert.hasText(uuid,"uuid is null");
		Image.ImageInfo imageRandom = imageManager.getImageRandom(uuid);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(imageRandom.getMoveImage(), "png",out);
		return out.toByteArray();
	}

	@RequestMapping(value = "/applycaptcha")
	@ResponseBody
	public ApplycaptchaInfo applycaptcha() throws Exception {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		Assert.hasText(uuid,"uuid is null");
		Image.ImageInfo imageRandom = imageManager.getImageRandom(uuid);
		ApplycaptchaInfo applycaptchaInfo = new ApplycaptchaInfo();
		applycaptchaInfo.setUuid(uuid);
		applycaptchaInfo.setY(imageRandom.getY());
		applycaptchaInfo.setImage1(baseImgeUrl+"image1?uuid="+uuid);
		applycaptchaInfo.setImage2(baseImgeUrl+"image2?uuid="+uuid);
		return applycaptchaInfo;
	}



	@RequestMapping(value = "/verify")
	@ResponseBody
	public boolean verify(@RequestBody VerifyInfo verifyInfo) throws Exception {
		Assert.notNull(verifyInfo,"verifyInfo is null");
		return imageManager.verify(verifyInfo);
	}

	public class ApplycaptchaInfo{
		private String uuid;
		private int y;
		private String image1;
		private String image2;

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public String getImage1() {
			return image1;
		}

		public void setImage1(String image1) {
			this.image1 = image1;
		}

		public String getImage2() {
			return image2;
		}

		public void setImage2(String image2) {
			this.image2 = image2;
		}
	}


}