// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import 'register/'
import store from 'store/'
import dictutil from './common/dictutil'
import globalVariable from './common/global_variable.js'
Vue.prototype.dictutil = dictutil;
Vue.prototype.GLOBAL = globalVariable;

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI, { size: 'mini' });
import dictstore from "@/store/global/dictstore";
import '@/utils/security/permission'
import promise from "@/views/login/promise";
import i18n from '@/i18n/i18n'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
Vue.use(mavonEditor)
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)

import  OrganizationSelector from  './components/organization-selector';
Vue.component('organization-selector',OrganizationSelector);

import SvgIcon from './layout/left-menu/svgIcon'// svg component
Vue.component('svg-icon', SvgIcon)

Vue.config.productionTip = false;
Vue.config.devtools = true;

new Promise(resolve => {
    var iamModuleConfig = { "pluginName": "IamPlugin", "version": "v2.0.0", "modules": [{ "modName": "IamAllModule", "stable": "IAM.all.min.js", "grey": "IAM.all.js", "css_stable": "IAM.all.min.css", "css_grey": "IAM.all.css", "ratio": 100 }], "dependencies": [{ "features": ["IamAll"], "depends": ["IamAllModule"], "sync": true }] };
    new LoaderJS(iamModuleConfig).use("IamAll", function () {
        console.log("******* IAM JSSDK loaded completed! *******");
        resolve()
    })
}).then(() => {
    /* eslint-disable no-new */
    new Vue({
        el: '#app',
        router,
        store,
        i18n,
        template: '<App/>',
        components: { App },
        beforeCreate() {
            console.debug('根组件：beforeCreate')
        },
        created() {
            console.debug('根组件：created')
        },
        beforeMount() {
            console.debug('根组件：beforeMount')
        },
        mounted() {
            console.debug('根组件：mounted')
            //promise.buildRoleRoute(this);
        }
    });
})





