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

import java.io.Serializable;

/**
 * Analyze jigsaw image model.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月30日
 * @since
 */
public class AnylyzeJigsawImgModel implements Serializable {
	private static final long serialVersionUID = 4975604364412626949L;

	private String applyUuid;
	private Integer x;
	private Trail[] trails; // Enhanced check

	public String getApplyUuid() {
		return applyUuid;
	}

	public void setApplyUuid(String applyUuid) {
		this.applyUuid = applyUuid;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Trail[] getTrails() {
		return trails;
	}

	public void setTrails(Trail[] trails) {
		this.trails = trails;
	}

	/**
	 * Mouse trail information.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月30日
	 * @since
	 */
	public static class Trail implements Serializable {
		private static final long serialVersionUID = 4975604364422626949L;

		private Integer trailX;
		private Integer trailY;

		public Integer getTrailX() {
			return trailX;
		}

		public void setTrailX(Integer trailX) {
			this.trailX = trailX;
		}

		public Integer getTrailY() {
			return trailY;
		}

		public void setTrailY(Integer trailY) {
			this.trailY = trailY;
		}

	}

}