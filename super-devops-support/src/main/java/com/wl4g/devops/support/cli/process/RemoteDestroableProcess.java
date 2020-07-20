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
package com.wl4g.devops.support.cli.process;

import ch.ethz.ssh2.Session;
import com.wl4g.devops.support.cli.command.DestroableCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import static ch.ethz.ssh2.ChannelCondition.CLOSED;
import static ch.ethz.ssh2.channel.Channel.STATE_OPEN;
import static org.springframework.util.Assert.notNull;

/**
 * Remote destroable process implements.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-06
 * @since
 */
public final class RemoteDestroableProcess extends DestroableProcess {

	/**
	 * Execution remote process of session {@link Session}
	 */
	final private Session session;

	public RemoteDestroableProcess(String processId, DestroableCommand command, Session session) {
		super(processId, command);
		notNull(session, "Command remote process session can't null.");
		this.session = session;
	}

	@Override
	public OutputStream getStdin() {
		return session.getStdin();
	}

	@Override
	public InputStream getStdout() {
		return session.getStdout();
	}

	@Override
	public InputStream getStderr() {
		return session.getStderr();
	}

	@Override
	public boolean isAlive() {
		return session.getState() == STATE_OPEN;
	}

	@Override
	public void destoryForcibly() {
		session.close();
	}

	@Override
	public void waitFor(long timeout, TimeUnit unit) throws IOException, InterruptedException {
		// Wait for completed by condition.
		session.waitForCondition((CLOSED), timeout);
	}

	@Override
	public Integer exitValue() {
		return session.getExitStatus();
	}

}