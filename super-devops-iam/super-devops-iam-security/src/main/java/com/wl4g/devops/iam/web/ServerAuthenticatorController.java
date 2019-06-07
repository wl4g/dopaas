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
package com.wl4g.devops.iam.web;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.ExtraController;
import com.wl4g.devops.support.cache.JedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;

@ExtraController
public class ServerAuthenticatorController extends AbstractAuthenticatorController {

	@Autowired
	private JedisService jedisService;

	/**
	 *
	 * @param from  who are you
	 * @param to    which service you want to visit
	 * @return
	 */
	@RequestMapping(URI_S_GET_TOKEN)
	@ResponseBody
	public String getToken(String from,String to) {
		if (log.isInfoEnabled()) {
			log.info("getToken processing... sessionId[{}]");
		}
		Assert.hasText(from,"appGroup is null");
		Assert.hasText(to,"appGroup is null");
		String token = null;
		/*
		 * create token
		 */
		try {
			String key = SERVER_TOKEN_KEY+from+to;
			token =  UUID.randomUUID().toString().replaceAll("-", "");
			jedisService.set(key,token,0);//save for ever until change
		} catch (Exception e) {
			log.error("getToken exception. This can generally safely be ignored.", e);
		}

		if (log.isInfoEnabled()) {
			log.info("getToken finished. [{}]", token);
		}
		return token;
	}


	/**
	 *
	 * @param from  who are you
	 * @param to    which service you want to visit
	 * @return
	 */
	@RequestMapping(URI_S_AUTH_TOKEN)
	@ResponseBody
	public RespBase RespBase(String from,String to,String token) {
		if (log.isInfoEnabled()) {
			log.info("authToken processing... sessionId[{}]");
		}
		RespBase respBase = new RespBase();
		/*
		 * create token
		 */
		try {
			String key = SERVER_TOKEN_KEY+from+to;
			String realToken = jedisService.get(key);//save for ever until change
			if(StringUtils.equals(realToken,token)){
				respBase.setCode(RetCode.OK);
			}else{
				respBase.setCode(RetCode.UNAUTHC);
			}
		} catch (Exception e) {
			log.error("authToken exception. This can generally safely be ignored.", e);
		}

		return respBase;
	}

}