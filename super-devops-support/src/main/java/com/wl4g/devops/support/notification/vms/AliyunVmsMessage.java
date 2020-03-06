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

import static java.util.Objects.isNull;

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
	private Action action;

	/**
	 * 语音文件的播放次数，取值范围为1~3。
	 */
	private Integer playTimes = 3;

	/**
	 * 语音文件播放的音量。取值范围为0~100，默认为100。
	 */
	private Integer volume = 100;

	/**
	 * 语速控制，取值范围：-500~500。
	 */
	private Integer speed = 100;

	/**
	 * 预留给调用方使用的ID，最终会通过在回执消息中将此ID带回给调用方。 字符串类型，长度为1~15个字节。
	 */
	private String outId;

	public AliyunVmsMessage(Action action, String calledNumber, String templateKey) {
		this(action, calledNumber, calledNumber, templateKey, null, null, null, null);
	}

	public AliyunVmsMessage(Action action, String calledNumber, String templateKey, Integer playTimes, Integer volume,
			Integer speed) {
		this(action, calledNumber, calledNumber, templateKey, playTimes, volume, speed, null);
	}

	public AliyunVmsMessage(Action action, String calledShowNumber, String calledNumber, String templateKey, Integer playTimes,
			Integer volume, Integer speed, String outId) {
		super(calledShowNumber, calledNumber, templateKey);
		setAction(action);
		setPlayTimes(playTimes);
		setVolume(volume);
		setSpeed(speed);
		setOutId(outId);
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Integer getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(Integer playTimes) {
		if (!isNull(playTimes)) {
			this.playTimes = playTimes;
		}
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		if (!isNull(volume)) {
			this.volume = volume;
		}
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		if (!isNull(speed)) {
			this.speed = speed;
		}
	}

	public String getOutId() {
		return outId;
	}

	public void setOutId(String outId) {
		if (!isNull(outId)) {
			this.outId = outId;
		}
	}

	public static enum Action {
		SingleCallByVoice, SingleCallByTts;
	}

}
