package com.wl4g.devops.gateway.server.console;

import com.wl4g.devops.components.shell.annotation.ShellComponent;
import com.wl4g.devops.components.shell.annotation.ShellMethod;
import com.wl4g.devops.components.shell.handler.SimpleShellContext;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;
import com.wl4g.devops.gateway.server.config.GatewayRefreshProperties;
import com.wl4g.devops.gateway.server.console.args.UpdatingRefreshDelayArgument;
import com.wl4g.devops.gateway.server.coordinate.RefreshableConfigurationCoordinator;
import com.wl4g.devops.gateway.server.route.AbstractRouteRepository;
import com.wl4g.devops.gateway.server.route.IRouteCacheRefresh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Flux;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

/**
 * {@link GatewayConsole}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-23
 * @since
 */
@ShellComponent
public class GatewayConsole {

	final protected SmartLogger log = getLogger(getClass());

	@Autowired
	protected IRouteCacheRefresh refresher;

	@Autowired
	protected GatewayRefreshProperties config;

	@Autowired
	private AbstractRouteRepository abstractRouteRepository;

	@Autowired
	protected RefreshableConfigurationCoordinator coordinator;

	/**
	 * Manual refresh gateway configuration all from cache(redis).
	 * 
	 * @param context
	 */
	@ShellMethod(keys = "refresh", group = DEFAULT_GATEWAY_SHELL_GROUP, help = "Refresh gateway configuration all")

	public void refresh(SimpleShellContext context) {
		try {
			context.printf("Refreshing configuration ...");
			refresher.flushRoutesPermanentToMemery();
		} catch (Exception e) {
			log.error("", e);
		}

		Flux<RouteDefinition> memeryRoutes = abstractRouteRepository.getRouteDefinitionsByMemery();

		// Print result message.
		StringBuilder res = new StringBuilder(100);
		res.append("Refresh succeeded. The current configuration information is: ");
		res.append(LINE_SEPARATOR);
		res.append("\t");
		res.append("----route info----\n"); // TODO

		memeryRoutes.subscribe(route -> res.append(JacksonUtils.toJSONString(route))
				.append("\n")
		);

		context.printf(res.toString());
		context.completed();
	}

	/**
	 * Updating refresh delay time.
	 * 
	 * @param arg
	 * @param context
	 * @return
	 */
	@ShellMethod(keys = "updateRefreshDelay", group = DEFAULT_GATEWAY_SHELL_GROUP, help = "Update configuration refresh schedule delay(Ms)")
	public void updateRefreshDelay(UpdatingRefreshDelayArgument arg, SimpleShellContext context) {
		try {
			config.setRefreshDelayMs(arg.getRefreshDelayMs());

			// Restart refresher
			coordinator.restartRefresher();
		} catch (Exception e) {
			log.error("", e);
		}

		context.printf("Successful updated, The now refreshDelayMs is: ".concat(valueOf(config.getRefreshDelayMs())));
		context.completed();
	}

	/** Gateway shell console group name. */
	final public static String DEFAULT_GATEWAY_SHELL_GROUP = "Gateway server shell commands";

}
