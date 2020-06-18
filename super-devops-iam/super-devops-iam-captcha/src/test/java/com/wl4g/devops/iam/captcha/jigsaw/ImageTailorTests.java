/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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

import static com.wl4g.devops.components.tools.common.io.FileIOUtils.writeFile;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.io.File;

import com.wl4g.devops.iam.captcha.jigsaw.ImageTailor.TailoredImage;

public class ImageTailorTests {

	public static void main(String[] args) throws Exception {
		ImageTailor tailor = new ImageTailor();
		// TailoredModel img = tailor.getImageUrl("http://vps.vjay.pw/1.jpg");
		TailoredImage img = tailor.getImageFile(USER_DIR + "/src/main/resources/static/jigsaw/jigsaw_default1.jpg");
		System.out.println(img);
		writeFile(new File("d:\\a.png"), img.getPrimaryImg(), false);
		writeFile(new File("d:\\b.png"), img.getBlockImg(), false);
	}

}