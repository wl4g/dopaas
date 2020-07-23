package com.wl4g.devops.gateway.server.console;

import com.wl4g.devops.components.shell.annotation.ShellComponent;
import com.wl4g.devops.components.shell.annotation.ShellMethod;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.gateway.server.route.IRouteCacheRefresh;
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

    @ShellMethod(keys = {"refresh"}, group = "Gateway server shell commands", help = "refresh gateway config all")
    public String refresh() {
        try {
            iRouteCacheRefresh.flushRoutesPermanentToMemery();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
