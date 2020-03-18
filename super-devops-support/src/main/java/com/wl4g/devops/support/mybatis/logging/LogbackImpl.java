package com.wl4g.devops.support.mybatis.logging;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;

/**
 * LogbackImpl
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月21日
 * @since
 */
public class LogbackImpl implements Log {

	private Logger log;

	public LogbackImpl(String clazz) {
		log = getLogger(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		log.error(s, e);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void trace(String s) {
		log.trace(s);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}

}
