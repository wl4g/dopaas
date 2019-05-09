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
package com.zrk.oauthclient.definition;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * 返回数据转化器
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:43:44
 */
public class WeiXinAttributesDefinition extends OAuthAttributesDefinition {

	public static final String ID = "id"; // 数据库业务id
	public static final String OPEN_ID = "openid"; // openid。
	public static final String NICK_NAME = "nickname"; // 用户在昵称。
	public static final String SEX = "sex"; // 性别。 1
	public static final String PROVINCE = "province"; // 省
	public static final String CITY = "city"; // 市
	public static final String HEADIMGURL = "headimgurl"; // 大小为40×40像素的QQ头像URL。

	public WeiXinAttributesDefinition() {
		addAttribute(ID, Converters.longConverter);
		addAttribute(OPEN_ID, Converters.stringConverter);
		addAttribute(NICK_NAME, Converters.stringConverter);
		addAttribute(SEX, Converters.integerConverter);
		addAttribute(PROVINCE, Converters.stringConverter);
		addAttribute(CITY, Converters.stringConverter);
		addAttribute(HEADIMGURL, Converters.stringConverter);
	}

	// {
	// "openid": "oD5YLsz-b3HG9w5-QJsZBGElgSXU",
	// "nickname": "鍛ㄥ悍",
	// "sex": 1,
	// "language": "zh_CN",
	// "city": "Ankang",
	// "province": "Shaanxi",
	// "country": "CN",
	// "headimgurl":
	// "http://wx.qlogo.cn/mmopen/ajNVdqHZLLC3oofytPALMmC3VgXoltuicFnCabiczkv0H3iaG9tNaHOzaNzaW4v9GZ6TiapswUc7uu66xhbas2r1Sw/0",
	// "privilege": [],
	// "unionid": "o3bgEwsGhh-1FHS8CkpL6KPrp8ZE"
	// }

}