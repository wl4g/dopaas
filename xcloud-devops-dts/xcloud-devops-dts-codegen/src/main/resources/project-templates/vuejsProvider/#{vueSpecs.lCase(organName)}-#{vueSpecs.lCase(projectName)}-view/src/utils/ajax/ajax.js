import Vue from 'vue'
import axios from 'axios'
import VueAxios from 'vue-axios'
import qs from 'qs'
import global from "@/common/global_variable";
import { store } from "../index";

Vue.use(VueAxios, axios)

// 导入封装的回调函数
import {
    cbs,
    gbs
} from 'config/'


// 动态设置本地和线上接口域名
Vue.axios.defaults.baseURL = gbs.host
Vue.axios.defaults.timeout = 30000
Vue.axios.defaults.withCredentials = true

/**
 * 封装axios的通用请求
 * 
 * @param  {string}   type              get或post
 * @param  {string}   pathParams        url参数
 * @param  {string}   url               请求的接口URL
 * @param  {object}   data              传的参数，没有则传空对象
 * @param  {Function} fn                成功函数
 * @param  {Function} fn                失败函数
 * @param  {boolean}  tokenFlag         是否需携带token参数@deprecated
 * @param  {object}   headers
 * @param  {object}   opts
 * @param  {object}   sysModule
 */
export default function ({
    type,
    pathParams,
    path,
    data,
    fn,
    errFn,
    headers,
    opts,
    sysModule,
} = {}) {
    // 根据sys获取baseUrl
    var baseUrl = global.getBaseUrl(sysModule);
    var p = baseUrl + path;
    if (typeof path === 'function') {
        p = path(pathParams || {})
    }

    //统一添加currentOrganizationCode
    const currentOrganization = store.get("currentOrganization");
    if (currentOrganization) {
        if (p.indexOf('?') > 0) {
            p = p + '&organization_code=' + Common.Util.Codec.encodeBase58(currentOrganization.organizationCode);
        } else {
            p = p + '?organization_code=' + Common.Util.Codec.encodeBase58(currentOrganization.organizationCode);
        }
    }

    var options = {
        method: type === 'json' ? 'post' : type,
        url: p,
        headers: headers && typeof headers === 'object' ? headers : {},
        withCredentials: true, // 实现cors必须设置
    }

    try {
        options[type === 'get' ? 'params' : 'data'] = (type === 'json' ? data : qs.stringify(data))
    } catch (error) {
        console.error(error)
    }
    // 分发显示加载样式任务
    // this.$store.dispatch('show_loading')
    // Axios内置属性设置
    if (opts && typeof opts === 'object') {
        for (var f in opts) {
            options[f] = opts[f]
        }
    }

    // Add build-in security headers(replayToken/xsrfToken).
    if (options.method.toUpperCase() == 'POST' || options.method.toUpperCase() == 'DELETE') {
        var iamCore = new IAMCore();
        var replayToken = iamCore.generateReplayToken();
        options.headers[replayToken.headerName] = replayToken.value;
        var xsrfToken = iamCore.getXsrfToken();
        options.headers[xsrfToken.headerName] = xsrfToken.value;
    }

    // 发送请求
    Vue.axios(options).then((res) => {
        //console.debug("Response data.code: "+ res.data[gbs.api_status_key_field]);
        // this.$store.dispatch('hide_loading')

        // Backend response code: 200
        if (res.data[gbs.api_status_key_field] == gbs.api_status_value_field) {
            fn(res.data)
        } else {
            //返回code不是200的时候处理
            if (gbs.api_custom[res.data[gbs.api_status_key_field]]) {
                gbs.api_custom[res.data[gbs.api_status_key_field]](this, res.data, options.method, p, fn, errFn, data)
            } else if (errFn) {
                errFn.call(this, res.data);
                if (res.data && res.data.message) {
                    this.$message.error(res.data.message);
                }
            } else if (res.data && res.data.message) {
                this.$message.error(res.data.message);
            } else {
                this.$message.error("unknow errFn");
            }
        }
    }).catch((ex) => {
        // this.$store.dispatch('hide_loading');
        if (errFn) {
            errFn.call(this, ex);
        } else {
            console.error(ex);
        }
    })
};
