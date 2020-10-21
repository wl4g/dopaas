import { cache } from "../utils";

export default {
<#if vueSpecs.isConf(extOpts, "iam.mode", "cluster")>
    iam: {
        cluster: 'iam-server',
        defaultTwoDomain: 'iam-services',
        defaultContextPath: '/iam-server',
        defaultServerPort: '14040',
    },
<#-- 当生成的是IAM本地模式时(即，IAM server与业务应用属于同一工程)，前段IAM模块会硬编码变量如：global.iam，
但此时配置却是实际的项目配置。 -->
<#elseif vueSpecs.isConf(extOpts, "iam.mode", "local")>
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
        // Extract baseUri from cache.
        let baseUri = '';
        let sysModuleCache = cache.get("iam_system_modules");
        if (sysModuleCache && sysModuleCache[sysModule.cluster] && sysModuleCache[sysModule.cluster]['extranetBaseUri']) {
            baseUri = sysModuleCache[sysModule.cluster]['extranetBaseUri'];
        } else {
            // If it is an IAM app, fallback get.
            if (sysModule && sysModule.cluster == this.iam.cluster) {
                baseUri = new IAMCore({ deploy: this.iam }).getIamBaseUri();
            } else {
                console.error("Cannot get baseUri from cache, No such sysModule: " + sysModule);
            }
        }
        console.debug("Got sysModule baseUri: ", baseUri);
        return baseUri;
    },

}
