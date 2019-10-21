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
package com.wl4g.devops.support.cli;

import static org.springframework.util.Assert.isTrue;

import java.io.IOException;
import java.io.Serializable;

import com.wl4g.devops.common.exception.ci.TimeoutDestroyProcessException;

/**
 * Default command-line process management implements.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public class DefaultProcessManagerImpl extends GenericProcessManager {

	final public static long DEFAULT_DESTROY_ROUND_MS = 200L;

	/**
	 * Destroy command-line process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param processId
	 *            Create command-lien process ID.
	 * @param timeoutMs
	 *            Destroy process timeout Ms.
	 * @throws TimeoutDestroyProcessException
	 */
	@Deprecated
	@Override
	public void destroy(Serializable processId, long timeoutMs) throws TimeoutDestroyProcessException {
		isTrue(timeoutMs >= DEFAULT_DESTROY_ROUND_MS,
				String.format("Destroy timeoutMs must be less than or equal to %s", DEFAULT_DESTROY_ROUND_MS));

		Process process = repository.getProcessInfo(processId).getProcess();
		try {
			process.getOutputStream().close();
		} catch (IOException e) {
		}
		try {
			process.getInputStream().close();
		} catch (IOException e) {
		}
		try {
			process.getErrorStream().close();
		} catch (IOException e) {
		}

		// Destroy force.
		for (long i = 0, c = (timeoutMs / DEFAULT_DESTROY_ROUND_MS); process.isAlive() || i < c; i++) {
			process.destroyForcibly();
			try {
				Thread.sleep(DEFAULT_DESTROY_ROUND_MS);
			} catch (InterruptedException e) {
				log.error("Failed to destory comand-line process.", e);
				break;
			}
		}

		// Check destroyed?
		if (process.isAlive()) {
			throw new TimeoutDestroyProcessException(
					String.format("Still not destroyed '%s', destruction handler timeout", processId));
		}

		// Cleanup
		repository.cleanup(processId);
	}

}
