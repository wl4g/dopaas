/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.iam.captcha.config;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.devops.iam.kaptcha")
public class KaptchaProperties {

	private Properties properties = new Properties();

	public KaptchaProperties() {
		// Default kaptcha settings
		this.getProperties().put("kaptcha.border", "no");
		this.getProperties().put("kaptcha.border.color", "red");
		this.getProperties().put("kaptcha.border.thickness", "5");
		this.getProperties().put("kaptcha.image.width", "150");
		this.getProperties().put("kaptcha.image.height", "50");
		// 0,0,205 black
		this.getProperties().put("kaptcha.noise.color", "0,0,205");
		// 255,250,205
		this.getProperties().put("kaptcha.background.clear.from", "178,223,238");
		this.getProperties().put("kaptcha.background.clear.to", "240,255,240");
		this.getProperties().put("kaptcha.textproducer.font.names", "微软雅黑");
		this.getProperties().put("kaptcha.textproducer.font.size", "30");
		// 255,110,180
		this.getProperties().put("kaptcha.textproducer.font.color", "72,118,255");
		this.getProperties().put("kaptcha.textproducer.char.space", "3");
		this.getProperties().put("kaptcha.textproducer.char.string", "ABCDEFGHJKMNQRSTUVWXYZ123456789");
		this.getProperties().put("kaptcha.textproducer.char.length", "5");
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}