import { store } from "../utils";
import iputil from "./iputil";

export default {
<#if javaSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
    iam: {
        cluster: 'iam-server',
        twoDomain: 'iam',
        defaultContextPath: '/iam-server',
        defaultPort: '14040',
    },
</#if>
<#list moduleMap?keys as moduleName>
    ${moduleName}: {
        cluster: '${projectName?lower_case}-server',
        twoDomain: '${projectName?lower_case}',
        defaultContextPath: '/${projectName?lower_case}-server',
        defaultPort: '28080',
    },
</#list>
    getBaseUrl: function (app, usedefault) {
        if (!app) {
            return;
        }
        let baseUri = '';
        let hostname = location.hostname;
        let protocol = location.protocol;
        let appModules = store.get("iam_system_modules");
        if (!usedefault && appModules && appModules != 'null' && appModules[app.cluster] && appModules[app.cluster]['extranetBaseUri']) {//found from store
            baseUri = appModules[app.cluster]['extranetBaseUri'];
            console.debug("Got appModule baseUri: "+ baseUri);
        }
        // Use default
        else {
            let isIp = iputil.isIp(hostname);
            // 为方便本地调试，当域名以debug/local/dev后缀结尾，跟localhost一样处理，同时修改java配置文件application-dev #32-#34, 同时修改数据库app_cluster_config
            if (hostname == 'localhost' || isIp || hostname.endsWith('.debug') || hostname.endsWith('.local') || hostname.endsWith('.dev')) {//if localhost
                baseUri = protocol + "//" + hostname + ":" + app.defaultPort + app.defaultContextPath;
                console.debug("Got appModule default baseUri(local): "+ baseUri);
            } else {
                var topDomainName = hostname.split('.').slice(-2).join('.');
                if (hostname.indexOf("com.cn") > 0) {
                    topDomainName = hostname.split('.').slice(-3).join('.');
                }
                baseUri = protocol + "//" + app.twoDomain + "." + topDomainName + app.defaultContextPath;
                console.debug("Got appModule default baseUri: "+ baseUri);
            }
        }
        return baseUri;
    },

}
