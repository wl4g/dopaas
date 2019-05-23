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
package com.wl4g.devops.shell.processor;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.shell.bean.LineResultState.*;
import static com.wl4g.devops.shell.processor.AbstractProcessor.*;

import com.wl4g.devops.shell.bean.LineResultState;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;

/**
 * Shell console utility tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public abstract class ShellConsoles implements Closeable {

	final private static Logger log = LoggerFactory.getLogger(ShellConsoles.class);

	/**
	 * Mark whether the console output stream of the current thread is complete.
	 */
	final private static ThreadLocal<Boolean> completedCache = new InheritableThreadLocal<>();

	/**
	 * Manually output simple string message to the client console.
	 * 
	 * @param message
	 */
	public final static void write(String message) {
		ChannelMessageHandler client = getClient();
		if (client != null && client.isActive()) {
			try {
				client.writeAndFlush(new ResultMessage(getState(), message));
			} catch (IOException e) {
				String errmsg = getRootCauseMessage(e);
				errmsg = isBlank(errmsg) ? getMessage(e) : errmsg;
				log.error("=> {}", errmsg);
			}
		}
	}

	/**
	 * Has the circulation channel been opened?
	 * 
	 * @return
	 */
	public final static boolean isBegin() {
		Boolean completed = completedCache.get();
		return completed != null && !completed;
	}

	/**
	 * Manually open data flow message transaction output.
	 */
	public final static void begin() {
		completedCache.set(false);

		// Output start mark
		write(EMPTY);
	}

	/**
	 * Manually end data flow message transaction output.
	 */
	public final static void end() {
		completedCache.set(true);

		// Output end mark
		write(EMPTY);
	}

	/**
	 * Has the circulation channel been closed?
	 * 
	 * @return
	 */
	public final static boolean isClose() {
		Boolean completed = completedCache.get();
		return completed != null && completed;
	}

	/**
	 * Get the current channel status
	 */
	public final static LineResultState getState() {
		LineResultState state = NONCE;
		Boolean completed = completedCache.get();
		if (completed != null) {
			state = completed ? FINISH : RESP_WAIT;
		}
		return state;
	}

	@Override
	public void close() throws IOException {
		end();
	}

}
