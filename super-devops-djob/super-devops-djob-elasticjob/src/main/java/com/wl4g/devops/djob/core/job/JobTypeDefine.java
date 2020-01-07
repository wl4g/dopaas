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
package com.wl4g.devops.djob.core.job;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.script.ScriptJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public enum JobTypeDefine {

	SIMPLE_JOB("SimpleJob", SimpleJob.class),

	DATAFLOW_JOB("SimpleJob", DataflowJob.class),

	SCRIPT_JOB("SimpleJob", ScriptJob.class);

	final private static List<JobTypeDefine> jobTypes = new ArrayList<JobTypeDefine>(4) {
		private static final long serialVersionUID = -833328836999812718L;
		{
			addAll(asList(values()));
		}
	};

	final private String name;

	final private Class<?> clazz;

	private JobTypeDefine(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	final public static List<JobTypeDefine> getJobTypes() {
		return jobTypes;
	}

	final public static JobTypeDefine of(String name) {
		JobTypeDefine type = safeOf(name);
		if (Objects.isNull(type)) {
			throw new IllegalArgumentException(String.format("Unsupported jobType for '%s'", name));
		}
		return type;
	}

	final public static JobTypeDefine safeOf(String name) {
		Optional<JobTypeDefine> opt = getJobTypes().stream().filter(j -> j.getName().equals(name)).findFirst();
		return opt.isPresent() ? opt.get() : null;
	}

}