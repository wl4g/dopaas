package com.wl4g.devops.shell.processor.event;

/**
 * Command event listener
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public interface CommandEventListener extends EventListener {

	void onCommand(String command);

}