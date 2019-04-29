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
package com.zrk.oauthclient.profile;

import java.util.Map;

/**
 * 客户端接口类
 * 
 * @author Administrator
 *
 */
public interface ClientProfile {

	public String getId();

	public void setId(final Object id);

	public String getOpenid();

	public String getNickname();

	public Integer getSex();

	public String getIcon();

	public void addAttribute(final String key, Object value);

	public Map<String, Object> getAttributes();

	public Object getAttribute(final String name);

}