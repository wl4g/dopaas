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
package com.wl4g.devops.umc.config;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.UnsupportedDataTypeException;

import org.springframework.util.Assert;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Metric template support.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月5日
 * @since
 */
public class MetricTemplateSupport {

	/**
	 * Metric templates cache.
	 */
	final private static Map<String, MetricTemplate> __metricTplCache = new ConcurrentHashMap<>(16);

	/**
	 * Get metric templates.
	 * 
	 * @param metricName
	 * @return
	 */
	public synchronized static MetricTemplate getMetricTemplate(String metricName) {
		Assert.hasText(metricName, "Metric name must not be empty.");
		MetricTemplate tpl = __metricTplCache.get(metricName);
		try {
			if (tpl == null) {
				String path = "com/wl4g/devops/umc/config/" + metricName + "_metric_template.json";
				URL url = Resources.getResource(path);
				try {
					url.openConnection();
				} catch (Exception e) {
					throw new UnsupportedDataTypeException(String.format("No support metric template for: %s", metricName));
				}
				String metricTplString = Resources.toString(url, Charsets.UTF_8);
				__metricTplCache.put(metricName, (tpl = parseJSON(metricTplString, MetricTemplate.class)));
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return tpl;
	}

	/**
	 * Metric templates definitions.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月5日
	 * @since
	 */
	public static class MetricTemplate {

	}

}
