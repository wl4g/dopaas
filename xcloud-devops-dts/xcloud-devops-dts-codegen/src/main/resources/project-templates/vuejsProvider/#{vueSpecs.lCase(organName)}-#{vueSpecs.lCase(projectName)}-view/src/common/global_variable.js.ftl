import { store } from "../utils";

export default {
<#if vueSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
    iam: {
        cluster: 'iam-server',
        defaultTwoDomain: 'iam-services',
        defaultContextPath: '/iam-server',
        defaultServerPort: '14040',
    },
<#-- 当生成的是IAM本地模式时(即，IAM server与业务应用属于同一工程)，前段IAM模块会硬编码变量如：global.iam，
但此时配置却是实际的项目配置。 -->
<#elseif vueSpecs.isConf(extraOptions, "gen.iam.security-mode", "local")>
    iam: {
        cluster: '${entryAppName}',
        defaultTwoDomain: '${entryAppSubDomain}',
        defaultContextPath: '/${entryAppName}',
        defaultServerPort: '${entryAppPort}',
    },
</#if>
<#list moduleMap?keys as moduleName>
    ${moduleName}: {
        cluster: '${entryAppName}',
    },
</#list>
    getBaseUrl: function (sysModule) {
        // Extract baseUri from store.
        let baseUri = '';
        let sysModuleCache = store.get("iam_system_modules");
        if (sysModuleCache && sysModuleCache[sysModule.cluster] && sysModuleCache[sysModule.cluster]['extranetBaseUri']) {
            baseUri = sysModuleCache[sysModule.cluster]['extranetBaseUri'];
        } else {
            // If it is an IAM app, fallback get.
            if (sysModule && sysModule.cluster == this.iam.cluster) {
                baseUri = new IAMCore(this.iam).getIamBaseUri();
            } else {
                console.error("Cannot get baseUri from store, No such sysModule: " + sysModule);
            }
        }
        console.debug("Got sysModule baseUri: ", baseUri);
        return baseUri;
    },

}
