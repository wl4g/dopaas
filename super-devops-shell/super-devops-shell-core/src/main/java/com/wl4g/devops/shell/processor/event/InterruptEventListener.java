package com.wl4g.devops.shell.processor.event;

/**
 * Interrupt event listener
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public interface InterruptEventListener extends EventListener {

	void onInterrupt();

}