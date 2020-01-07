package com.wl4g.devops.support.cli.process;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.wl4g.devops.support.cli.command.DestroableCommand;

import ch.ethz.ssh2.Session;
import static ch.ethz.ssh2.ChannelCondition.*;
import static ch.ethz.ssh2.channel.Channel.*;

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
		session.waitForCondition((CLOSED | EOF | TIMEOUT), timeout);
	}

	@Override
	public Integer exitValue() {
		return session.getExitStatus();
	}

}
