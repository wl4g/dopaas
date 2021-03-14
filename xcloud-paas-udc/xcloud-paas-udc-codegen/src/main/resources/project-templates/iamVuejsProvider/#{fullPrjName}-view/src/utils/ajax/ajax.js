import Vue from 'vue'
import axios from 'axios'
import VueAxios from 'vue-axios'
import qs from 'qs'
import global from "@/common/global_variable";
import { cache } from "../index";

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
 * Axios的通用请求
 *
 * @param  {string}    type           get或post
 * @param  {string}    dataType       数据类型(json|query或缺省)
 * @param  {string}    pathParams     URL参数
 * @param  {string}    path           请求URL的path
 * @param  {object}    data           body参数(可空)
 * @param  {Function}  fn             成功函数
 * @param  {Function}  errFn          失败函数
 * @param  {object}    headers        请求头
 * @param  {object}    opts           Axios设置选项
 * @param  {object}    sysModule      系统模块信息对象
 */
export default function ({
    type,
    dataType,
    pathParams,
    path,
    data,
    fn,
    errFn,
    headers,
    opts,
    sysModule,
} = {}) {
    // step1: 获取具体模块BaseURL
    var baseUrl = global.getBaseUrl(sysModule);
    var p = baseUrl + path;
    if (typeof path === 'function') {
        p = path(pathParams || {})
    }

    // step2: 设置axios内置属性
    var options = {
        method: type.toUpperCase(),
        url: p,
        headers: headers && typeof headers == 'object' ? headers : {},
        withCredentials: true, // 实现cors必须设置
    }
    if (opts && typeof opts == 'object') {
        for (var f in opts) {
            options[f] = opts[f]
        }
    }
    // Sets request data.
    let reqDataKey = 'GET,HEAD,TRACE'.includes(options.method) ? 'params' : 'data';
    /**
     * for example(SpringMVC):
     * Case1:
     * @RequestMapping("detail") // default by 'application/x-www-form-urlencoded'
     * public Object detail(Long id) {...}
     *
     * Case2:
     * @RequestMapping("save") // default by 'application/json'
     * public Object save(@RequestBody MyBean mybean) {...}
     *
     * Case3:
     * @RequestMapping("save") // default by 'application/x-www-form-urlencoded'
     * public Object save(MyBean mybean, String str) {...}
     *
     */
    if (!dataType || dataType == 'query') {
        if (reqDataKey == 'params') {
            if (typeof data == 'object') {
                options[reqDataKey] = data;
            } else {
                options.url = options.url.indexOf('?') > 0 ? (options.url + data) : (options.url + "?" + data);
            }
        } else if (typeof data == 'object') {
            // To flat URL parameters.
            options[reqDataKey] = qs.stringify(data);//from
        } else {
            options[reqDataKey] = data;//form
        }
        if (!options.headers['Content-Type']) {
            // Refer:org.springframework.web.HttpMediaTypeNotSupportedException: Content type 'application/x-www-form-urlencoded;charset=UTF-8' not supported
            options.headers['Content-Type'] = 'application/x-www-form-urlencoded';
        }
    } else {
        options[reqDataKey] = data;
        if (!options.headers['Content-Type']) {
            // options.headers['Content-Type'] = 'application/json;charset=UTF-8'; // Spring4.x-
            options.headers['Content-Type'] = 'application/json'; // Spring5.x+
        }
    }

    // step3: Add build-in security headers(replayToken/xsrfToken).
    if ('POST,PUT,DELETE'.includes(options.method)) {
        var iamCore = new IAMCore();
        var replayToken = iamCore.generateReplayToken();
        options.headers[replayToken.headerName] = replayToken.value;
        var xsrfToken = iamCore.getXsrfToken();
        options.headers[xsrfToken.headerName] = xsrfToken.value;
    }

    // step4: 统一添加organCode(用于控制数据权限)
    const currentOrganization = cache.get("currentOrganization");
    if (currentOrganization) {
        if (options.url.indexOf('?') > 0) {
            options.url += '&organization_code=' + Common.Util.Codec.encodeBase58(currentOrganization.organizationCode);
        } else {
            options.url += '?organization_code=' + Common.Util.Codec.encodeBase58(currentOrganization.organizationCode);
        }
    }

    // step5: Send request.
    Vue.axios(options).then((res) => {
        //console.debug("Response data.code: "+ res.data[gbs.api_status_key_field]);
        if (res.data[gbs.api_status_key_field] == gbs.api_status_value_field) {
            fn(res.data);
        } else { // Failure
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
        if (errFn) {
            errFn.call(this, ex);
        } else {
            console.error(ex);
        }
    })
};
