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
package com.wl4g.devops.iam.common.security.replay;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.components.tools.common.codec.Base58.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrueOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A default replay attacks protection token.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月7日
 * @since
 */
public final class DefaultReplayToken implements ReplayToken {
	private static final long serialVersionUID = 9081452892797891148L;

	/**
	 * Unrepeatable request timestamp
	 * 
	 * @return
	 */
	@JsonProperty("t")
	private Long timestamp;

	/**
	 * Unrepeatable request nonce
	 * 
	 * @return
	 */
	@JsonProperty("n")
	private String nonce;

	/**
	 * Unrepeatable request signature
	 * 
	 * @return
	 */
	@JsonProperty("s")
	private String signature;

	public DefaultReplayToken() {
	}

	DefaultReplayToken(Long timestamp, String nonce, String signature) {
		setTimestamp(timestamp);
		setNonce(nonce);
		setSignature(signature);
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public DefaultReplayToken setTimestamp(Long timestamp) {
		notNullOf(timestamp, "timestamp");
		this.timestamp = timestamp;
		return this;
	}

	public String getNonce() {
		return nonce;
	}

	public DefaultReplayToken setNonce(String nonce) {
		hasTextOf(nonce, "nonce");
		this.nonce = nonce;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public DefaultReplayToken setSignature(String signature) {
		hasTextOf(signature, "signature");
		this.signature = signature;
		return this;
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + " - " + toJSONString(this) + "]";
	}

	/**
	 * Build replay token by request signature.
	 * 
	 * @param replayToken
	 * @return
	 */
	public static DefaultReplayToken build(String replayToken) {
		hasTextOf(replayToken, "replayToken");
		isTrueOf(!equalsAnyIgnoreCase(replayToken, "null", "undefined", ""), "replayToken");
		String decodeSignature = new String(decodeBase58(replayToken), UTF_8);
		return parseJSON(decodeSignature, DefaultReplayToken.class);
	}

}