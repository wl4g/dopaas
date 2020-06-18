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
package com.wl4g.devops.components.shell.console.args;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.wl4g.devops.components.shell.annotation.ShellOption;

public class MixedArgument implements Serializable {

	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "m", lopt = "map", help = "Map<String, Integer> type argument")
	private Map<String, Integer> map;

	@ShellOption(opt = "p", lopt = "prop", help = "Properties type argument")
	private Properties properties;

	@ShellOption(opt = "l", lopt = "list", help = "List<String> type argument")
	private List<String> list;

	@ShellOption(opt = "s", lopt = "set", help = "Set<String> type argument")
	private Set<String> set;

	@ShellOption(opt = "e", lopt = "enable1", help = "boolean type argument", defaultValue = "true")
	private boolean enable1;

	@ShellOption(opt = "E", lopt = "enable2", help = "Boolean type argument", defaultValue = "false")
	private Boolean enable2 = true;

	public Map<String, Integer> getMap() {
		return map;
	}

	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Set<String> getSet() {
		return set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}

	public boolean isEnable1() {
		return enable1;
	}

	public void setEnable1(boolean enable1) {
		this.enable1 = enable1;
	}

	public Boolean getEnable2() {
		return enable2;
	}

	public void setEnable2(Boolean enable2) {
		this.enable2 = enable2;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}