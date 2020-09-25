import { store } from "../utils";
import iputil from "./iputil";

export default {
    ci: {
        cluster: 'ci-server',// 这个必须和数据表app_cluster的name字段对应
        twoDomain: 'ci',// 默认二级域名
        defaultContextPath: '/ci-server',// 默认项目根路径
        defaultPort: '14046',// 默认端口
    },
    scm: {
        cluster: 'scm-server',
        twoDomain: 'scm',
        defaultContextPath: '/scm-server',
        defaultPort: '14043',
    },
    umc: {
        cluster: 'umc-manager',
        twoDomain: 'umc',
        defaultContextPath: '/umc-manager',
        defaultPort: '14048',
    },
    erm: {
        cluster: 'erm-manager',
        twoDomain: 'erm',
        defaultContextPath: '/erm-manager',
        defaultPort: '14051',
    },
    iam: {
        cluster: 'iam-server',
        twoDomain: 'iam',
        defaultContextPath: '/iam-server',
        defaultPort: '14040',
    },
    doc: {
        cluster: 'doc-manager',
        twoDomain: 'doc',
        defaultContextPath: '/doc-manager',
        defaultPort: '14060',
    },
    coss: {
        cluster: 'coss-manager',
        twoDomain: 'coss',
        defaultContextPath: '/coss-manager',
        defaultPort: '14062',
    },
    vcs: {
        cluster: 'vcs-manager',
        twoDomain: 'vcs',
        defaultContextPath: '/vcs-manager',
        defaultPort: '14063',
    },
    gw: {
        cluster: 'gateway-manager',
        twoDomain: 'gw',// 默认二级域名
        defaultContextPath: '/gateway-manager',// 默认项目根路径
        defaultPort: '14084',// 默认端口
    },
    dts: {
        cluster: 'dts-manager',
        twoDomain: 'dts',
        defaultContextPath: '/dts-manager',
        defaultPort: '14080',
    },
    <#list moduleMap?keys as moduleName>
    ${moduleName}: {
        cluster: '${projectName?lower_case}-server',
        twoDomain: '${projectName?lower_case}',
        defaultContextPath: '/${projectName?lower_case}-server',
        defaultPort: '8080',
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
