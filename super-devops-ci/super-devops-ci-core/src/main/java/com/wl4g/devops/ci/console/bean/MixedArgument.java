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
package com.wl4g.devops.ci.console.bean;

import com.wl4g.devops.shell.annotation.ShellOption;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

	@Override
	public String toString() {
		return "SetTypeArgument [map=" + map + ", properties=" + properties + ", list=" + list + ", set=" + set + "]";
	}

}