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
package com.wl4g.devops.components.shell.handler;

import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.Collection;

import org.slf4j.Logger;

import com.wl4g.devops.components.shell.exception.ChannelShellException;
import com.wl4g.devops.components.shell.exception.NoSupportedInterruptShellException;
import com.wl4g.devops.components.shell.exception.ProgressShellException;
import com.wl4g.devops.components.shell.signal.ProgressSignal;
import com.wl4g.devops.components.tools.common.annotation.Reserved;
import com.wl4g.devops.components.tools.common.math.Maths;

/**
 * {@link ProgressShellContext}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月3日 v1.0.0
 * @see
 */
public class ProgressShellContext extends AbstractShellContext {
	final public static int DEFAULT_WHOLE = 100;

	/**
	 * Last progress signal.
	 */
	protected ProgressSignal lastProgressed = new ProgressSignal(getClass().getSimpleName(), DEFAULT_WHOLE, 0);

	private ProgressShellContext() {
	}

	ProgressShellContext(AbstractShellContext context) {
		super(context);
	}

	/**
	 * Output current progress percentage to client console.
	 *
	 * @param title
	 * @param currentProgressPercent
	 * @return
	 */
	public ProgressShellContext printf(String title, float currentProgressPercent) throws ChannelShellException {
		isTrue(currentProgressPercent >= 0 && currentProgressPercent <= 1, "Progress percentage must be between 0 and 1");
		// checkCurrentProgressRight(currentProgressPercent);

		int curProgressWithDefault = Maths.multiply(DEFAULT_WHOLE, currentProgressPercent).intValue();
		return (ProgressShellContext) printf0(lastProgressed = new ProgressSignal(title, DEFAULT_WHOLE, curProgressWithDefault));
	}

	/**
	 * Output the number of current progress to the client console.
	 *
	 * @param title
	 * @param whole
	 * @param currentProgress
	 * @return
	 */
	public ProgressShellContext printf(String title, int whole, int currentProgress) throws ChannelShellException {
		// checkCurrentProgressRight(currentProgress);
		return (ProgressShellContext) printf0(lastProgressed = new ProgressSignal(title, whole, currentProgress));
	}

	/**
	 * Complete command execution manually, for example, when receiving an
	 * interrupt event, call it to output the message for the last time. </br>
	 * </br>
	 * <b><font color=red>Note: Don't forget to execute it, or the client
	 * console will pause until it timeout.</font><b>
	 * 
	 * @param message
	 * @see {@link #completed()}
	 */
	public void completed(String message) throws ChannelShellException {
		printf(message, getProgressed());
		super.completed();
	}

	/**
	 * Get current progressed percentage.
	 * 
	 * @return
	 */
	public float getProgressed() {
		return Maths.divide(lastProgressed.getProgress(), lastProgressed.getWhole()).floatValue();
	}

	/**
	 * Check the forward percentage of current progress.
	 * 
	 * @param currentProgress
	 * @throws ProgressShellException
	 */
	@Reserved
	private void checkCurrentProgressRight(int currentProgress) throws ProgressShellException {
		if (lastProgressed.getProgress() > currentProgress) {
			throw new ProgressShellException(format("Progress cannot be reduced, progressed made: %s, current progress: %s",
					lastProgressed.getProgress(), currentProgress));
		}
	}

	/**
	 * Check the forward percentage of current progress.
	 * 
	 * @param currentProgress
	 * @throws ProgressShellException
	 */
	@Reserved
	private void checkCurrentProgressRight(float currentProgress) throws ProgressShellException {
		float last = getProgressed();
		if (last > currentProgress) {
			throw new ProgressShellException(
					format("Progress cannot be reduced, progressed made: %s%%, current progress: %s%%", last, currentProgress));
		}
	}

	/**
	 * {@link Util}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年2月2日 v1.0.0
	 * @see
	 */
	public static abstract class Util {
		final private static Logger log = getLogger(Util.class);

		/** Shell context cache. */
		final private static ThreadLocal<ProgressShellContext> contextCache = new InheritableThreadLocal<>();

		private Util() {
		}

		/**
		 * Bind shell context.
		 *
		 * @param context
		 * @return
		 */
		public static ProgressShellContext bind(ProgressShellContext context) {
			if (context != null) {
				contextCache.set(context);
			}
			return context;
		}

		/**
		 * Got current bind {@link ProgressShellContext}. </br>
		 * 
		 * @see {@link EmbeddedServerShellHandler#run()#MARK1}
		 * @return
		 */
		public static ProgressShellContext getContext() {
			ProgressShellContext context = contextCache.get();
			if (isNull(context)) {
				log.debug("The progress shell context was not retrieved. first use bind()");
				return notOpProgressShellContext;
			}
			return context;
		}

		/**
		 * Empty implements {@link ProgressShellContext}
		 */
		final private static ProgressShellContext notOpProgressShellContext = new NoOpProgressShellContext();

	}

	/**
	 * {@link NoOpProgressShellContext}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年2月5日 v1.0.0
	 * @see
	 */
	public static class NoOpProgressShellContext extends ProgressShellContext {

		private NoOpProgressShellContext() {
		}

		@Override
		public NoOpProgressShellContext printf(String title, float currentProgressPercent) throws ChannelShellException { // Ignore
			return this;
		}

		@Override
		public NoOpProgressShellContext printf(String title, int whole, int currentProgress) throws ChannelShellException { // Ignore
			return this;
		}

		@Override
		public void completed(String message) throws ChannelShellException { // Ignore
		}

		@Override
		public float getProgressed() { // Ignore
			return 0f;
		}

		// --- Parent. ---

		@Override
		public boolean isInterrupted() throws NoSupportedInterruptShellException { // Ignore
			return false;
		}

		@Override
		public void completed() throws ChannelShellException { // Ignore
		}

		@Override
		public Collection<ShellEventListener> getUnmodifiableEventListeners() {
			return null; // Ignore
		}

		@Override
		public boolean addEventListener(String name, ShellEventListener eventListener) { // Ignore
			return false;
		}

		@Override
		public boolean removeEventListener(String name) {
			return false; // Ignore
		}

		@Override
		protected NoOpProgressShellContext printf0(Object output) throws ChannelShellException {
			return this; // Ignore
		}

	}

}