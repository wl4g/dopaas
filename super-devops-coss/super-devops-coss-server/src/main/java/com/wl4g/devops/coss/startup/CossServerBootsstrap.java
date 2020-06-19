package com.wl4g.devops.coss.startup;

import static com.wl4g.devops.tool.common.lang.Assert2.notEmptyOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.devops.coss.config.ChannelServerProperties;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * {@link CossServerBootsstrap}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月17日
 * @since
 */
public class CossServerBootsstrap implements ApplicationRunner {

	final protected SmartLogger log = getLogger(getClass());

	/** {@link ChannelCossServer} list. */
	final protected List<ChannelCossServer> servers;

	/** {@link ChannelServerProperties} */
	final protected ChannelServerProperties config;

	public CossServerBootsstrap(ChannelServerProperties config, List<ChannelCossServer> servers) {
		notNullOf(config, "config");
		notEmptyOf(servers, "servers");
		this.servers = servers;
		this.config = config;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// Startup all servers.
		doStartupServers();
	}

	/**
	 * Do startup servers.
	 */
	private void doStartupServers() {
		for (ChannelCossServer server : servers) {
			server.start();
		}

	}

}
