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
package com.wl4g.devops.ci.core.param;

/**
 * New create pipeline handle parameter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public class NewParameter extends GenericParameter {
	private static final long serialVersionUID = -79398460376632146L;

	/**
	 * [Extensible]</br>
	 * Pipeline Pipeline task processing depends on external task tracking ID
	 * (e.g. task ID of external project or business docs management system).
	 */
	private String trackId;

	/**
	 * External task tracking type.
	 *
	 * @see {@link #taskTraceId}
	 */
	private String trackType;

	private String annex;

	public NewParameter() {
		super();
	}

	public NewParameter(Integer pipeId, String remark, String trackId, String trackType, String annex) {
		super(pipeId, remark);
		setTrackId(trackId);
		setTrackType(trackType);
		setAnnex(annex);
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		// when pcm not config, this params can be null
		this.trackId = trackId;
	}

	public String getTrackType() {
		return trackType;
	}

	public void setTrackType(String trackType) {
		// when pcm not config, this params can be null
		this.trackType = trackType;
	}

	public String getAnnex() {
		return annex;
	}

	public void setAnnex(String annex) {
		this.annex = annex;
	}
}