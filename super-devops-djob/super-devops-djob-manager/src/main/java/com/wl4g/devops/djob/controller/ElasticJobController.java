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
package com.wl4g.devops.djob.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.devops.djob.core.job.ElasticJob;
import com.wl4g.devops.djob.core.scheduler.ElasticJobManager;

/**
 * Elastic job manager controller
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月12日
 * @since
 */
@RestController
public class ElasticJobController extends GenericDjobController {

	@Autowired
	private ElasticJobManager jobService;

	/**
	 * 添加动态任务（适用于脚本逻辑已存在的情况，只是动态添加了触发的时间）
	 * 
	 * @param job
	 *            任务信息
	 * @return
	 */
	@PostMapping("/job")
	public Object addJob(@RequestBody ElasticJob job) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", true);

		if (!StringUtils.hasText(job.getJobName())) {
			result.put("status", false);
			result.put("message", "name not null");
			return result;
		}

		if (!StringUtils.hasText(job.getCron())) {
			result.put("status", false);
			result.put("message", "cron not null");
			return result;
		}

		if (!StringUtils.hasText(job.getJobType())) {
			result.put("status", false);
			result.put("message", "getJobType not null");
			return result;
		}

		if ("SCRIPT".equals(job.getJobType())) {
			if (!StringUtils.hasText(job.getScriptCommandLine())) {
				result.put("status", false);
				result.put("message", "scriptCommandLine not null");
				return result;
			}
		} else {
			if (!StringUtils.hasText(job.getJobClass())) {
				result.put("status", false);
				result.put("message", "jobClass not null");
				return result;
			}
		}

		try {
			jobService.addJob(job);
		} catch (Exception e) {
			result.put("status", false);
			result.put("message", e.getMessage());
		}
		return result;
	}

	/**
	 * 删除动态注册的任务（只删除注册中心中的任务信息）
	 * 
	 * @param jobName
	 *            任务名称
	 * @throws Exception
	 */
	@GetMapping("/job/remove")
	public Object removeJob(String jobName) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", true);
		try {
			jobService.removeJob(jobName);
		} catch (Exception e) {
			result.put("status", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
}