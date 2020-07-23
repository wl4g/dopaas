package com.wl4g.devops.gateway.server.console;

import com.wl4g.devops.components.shell.annotation.ShellComponent;
import com.wl4g.devops.components.shell.annotation.ShellMethod;
import com.wl4g.devops.components.shell.handler.SimpleShellContext;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.gateway.server.config.GatewayRefreshProperties;
import com.wl4g.devops.gateway.server.console.args.UpdateRefreshTimeArgument;
import com.wl4g.devops.gateway.server.route.IRouteCacheRefresh;
import com.wl4g.devops.gateway.server.task.ApplicationTaskRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

/**
 * @author vjay
 * @date 2020-07-23 10:33:00
 */
@ShellComponent
public class GatewayConsole {

    final private SmartLogger log = getLogger(getClass());

    @Autowired
    private IRouteCacheRefresh iRouteCacheRefresh;

    @Autowired
    private GatewayRefreshProperties gatewayRefreshProperties;

    @Autowired
    private ApplicationTaskRunner applicationTaskRunner;

    @ShellMethod(keys = "refresh", group = "Gateway server shell commands", help = "refresh gateway config all")
    public String refresh(SimpleShellContext context) {
        try {
            iRouteCacheRefresh.flushRoutesPermanentToMemery();
            context.printf("Refresh success");
        } catch (Exception e) {
            log.error("", e);
            context.printf("Refresh fail");
        }
        return "Refresh success";
    }

    @ShellMethod(keys = "updateRefreshTime", group = "Gateway server shell commands", help = "Update refresh time ms")
    public String updateRefreshTime(UpdateRefreshTimeArgument arg, SimpleShellContext context) {
        try {
            gatewayRefreshProperties.setRefreshTimeMs(arg.getRefreshTimeMs());
            context.printf("Update success");
        } catch (Exception e) {
            log.error("", e);
            context.printf("Update success");
        }
        return "Update success";
    }

}
