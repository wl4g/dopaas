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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.shell.bean.LineResultState.*;
import static com.wl4g.devops.shell.processor.AbstractProcessor.*;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;

/**
 * Shell console utility tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月22日
 * @since
 */
public abstract class ShellConsoles {

	final private static Logger log = LoggerFactory.getLogger(ShellConsoles.class);

	/**
	 * Manually output simple string message to the client console.
	 * 
	 * @param message
	 */
	public static void write(String message) {
		write(new ResultMessage(message));
	}

	/**
	 * Manually output stream message to the client console.
	 * 
	 * @param message
	 */
	public static void writeStream(boolean completed, String message) {
		write(new ResultMessage(completed ? FINISH : RESP_WAIT, message));
	}

	/**
	 * Write result message
	 * 
	 * @param message
	 */
	public static void write(ResultMessage message) {
		ChannelMessageHandler client = getClient();
		if (client != null) {
			try {
				client.writeAndFlush(message);
			} catch (IOException e) {
				String errmsg = getRootCauseMessage(e);
				errmsg = isBlank(errmsg) ? getMessage(e) : errmsg;
				log.error("=> {}", errmsg);
			}
		}
	}

}
