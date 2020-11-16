/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.umc.opentsdb.client.util;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import com.wl4g.devops.umc.opentsdb.client.bean.response.ErrorResponse;
import com.wl4g.devops.umc.opentsdb.client.common.Json;
import com.wl4g.devops.umc.opentsdb.client.exception.OpenTSDBHttpException;

/**
 * 响应解析工具类
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:30
 * @Version: 1.0
 */
public class ResponseUtil {

	/***
	 * 解析响应的内容
	 * 
	 * @param response
	 *            响应内容
	 * @return
	 * @throws IOException
	 */
	public static String getContent(HttpResponse response) throws IOException {
		if (checkGT400(response)) {
			throw new OpenTSDBHttpException(convert(response));
		} else {
			return getContentString(response);
		}
	}

	private static String getContentString(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, Charset.defaultCharset());
		}
		return null;
	}

	/***
	 * 判断响应码的是否为400以上，如果是，则表示出错了
	 * 
	 * @param response
	 *            查询对象
	 * @return
	 */
	private static boolean checkGT400(HttpResponse response) {
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode >= 400) {
			return true;
		}
		return false;
	}

	/***
	 * 将响应内容转换成errorResponse
	 * 
	 * @param response
	 *            查询对象
	 * @return
	 */
	private static ErrorResponse convert(HttpResponse response) throws IOException {
		return Json.readValue(getContentString(response), ErrorResponse.class);
	}

}