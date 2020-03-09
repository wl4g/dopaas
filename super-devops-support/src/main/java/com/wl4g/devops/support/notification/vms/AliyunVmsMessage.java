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
package com.wl4g.devops.support.notification.vms;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import javax.validation.constraints.NotNull;

/**
 * AliyunVmsMessage
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 * @see https://help.aliyun.com/document_detail/114036.html?spm=a2c4g.11186623.6.583.3b6b24b9KMGLCS
 */
public class AliyunVmsMessage extends VmsMessage {
	private static final long serialVersionUID = 1303039928183495028L;

	/**
	 * @see https://help.aliyun.com/document_detail/114036.html?spm=a2c4g.11186623.6.583.28265ad58befcz
	 */
	@NotNull
	private Action action;

	/**
	 * 语音文件的播放次数，取值范围为1~3。
	 */
	private Integer playTimes = 2;

	/**
	 * 语音文件播放的音量。取值范围为0~100，默认为100。
	 */
	private Integer volume = 100;

	/**
	 * 语速控制，取值范围：-500~500。
	 */
	private Integer speed = 100;

	public AliyunVmsMessage(String calledNumber, String templateKey) {
		this(Action.SingleCallByTts, calledNumber, templateKey);
	}

	public AliyunVmsMessage(Action action, String calledNumber, String templateKey) {
		this(action, calledNumber, templateKey, null);
	}

	public AliyunVmsMessage(Action action, String calledNumber, String templateKey, String callbackId) {
		super(calledNumber, templateKey, callbackId);
		notNullOf(action, "aliyunVmsAction");
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public Integer getPlayTimes() {
		return playTimes;
	}

	public AliyunVmsMessage setPlayTimes(Integer playTimes) {
		if (!isNull(playTimes)) {
			this.playTimes = playTimes;
		}
		return this;
	}

	public Integer getVolume() {
		return volume;
	}

	public AliyunVmsMessage setVolume(Integer volume) {
		if (!isNull(volume)) {
			this.volume = volume;
		}
		return this;
	}

	public Integer getSpeed() {
		return speed;
	}

	public AliyunVmsMessage setSpeed(Integer speed) {
		if (!isNull(speed)) {
			this.speed = speed;
		}
		return this;
	}

	public static enum Action {

		/**
		 * https://help.aliyun.com/document_detail/114036.html?spm=a2c4g.11186623.6.579.7bc95f33wpPjWM
		 */
		SingleCallByVoice,

		/**
		 * https://help.aliyun.com/document_detail/114035.html?spm=a2c4g.11186623.6.581.56295ad5EBbcwv#
		 */
		SingleCallByTts;

	}

}
