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

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;

import com.wl4g.devops.iam.verification.model.VerifyCodeBasedModel;

/**
 * Analyze verify jigsaw image model.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月30日
 * @since
 */
public class VerifyJigsawImgModel extends VerifyCodeBasedModel {
	private static final long serialVersionUID = 4975604364412626949L;

	private Integer x;
	private Map<Integer, Integer> trails = new HashMap<>(); // AI enhanced check

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Map<Integer, Integer> getTrails() {
		return trails;
	}

	public void setTrails(Map<Integer, Integer> trails) {
		if (!isEmpty(trails)) {
			this.trails = trails;
		}
	}

}