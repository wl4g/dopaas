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
package com.wl4g.devops.support.cli.repository;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.util.Assert;

import com.wl4g.devops.common.exception.support.NoSuchProcessException;
import com.wl4g.devops.support.cli.process.DestroableProcess;

/**
 * Default command-line process registration repository.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public class DefaultProcessRepository implements ProcessRepository {

	/**
	 * Command-line process registration repository.
	 */
	final protected ConcurrentMap<String, DestroableProcess> registry = new ConcurrentHashMap<>();

	@Override
	public void register(String processId, DestroableProcess dpw) {
		Assert.state(isNull(registry.putIfAbsent(processId, dpw)), "Already command line process");
	}

	@Override
	public void setDestroable(String processId, boolean destroable) throws NoSuchProcessException {
		get(processId).setDestroable(destroable);
	}

	@Override
	public DestroableProcess get(String processId) throws NoSuchProcessException {
		DestroableProcess process = registry.get(processId);
		if (isNull(process)) {
			throw new NoSuchProcessException(String.format("No such command-line process of '%s'", processId));
		}
		return process;
	}

	@Override
	public boolean hasProcess(String processId) {
		return registry.containsKey(processId);
	}

	@Override
	public Collection<DestroableProcess> getProcessRegistry() {
		return registry.values();
	}

	@Override
	public DestroableProcess cleanup(String processId) {
		return registry.remove(processId);
	}

}