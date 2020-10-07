import {
    store
} from '../../utils/'
import da from "element-ui/src/locale/lang/da";
import promise from './promise'
import global from "../../common/global_variable";

export default {
    name: 'login',
    data() {
        return {
            winSize: {
                width: '',
                height: ''
            },
            formOffset: {
                position: 'absolute',
                left: '',
                top: ''
            },
            register: false,
            login_actions: {
                disabled: false
            },
            data: {
                username: '',
                password: ''
            },
        }
    },
    methods: {
        postLoginSuccess(principal) {
            store.set('userinfo.username', principal);
            this.$router.push('/');
        },
        initIamJssdk() {
            var that = this;
            new IAMUi().initUI(document.getElementById("iam_container"), {
            	// refer: https://github.com/wl4g/xcloud-iam/blob/master/xcloud-iam-security/src/main/resources/iam-jssdk-webapps/example.html
<#if vueSpecs.isConf(extraOptions, "gen.iam.security-mode", "local")>
            	deploy: {
                    defaultTwoDomain: "${entryAppSubDomain}",
                    defaultServerPort: ${entryAppPort},
                    defaultContextPath: "/${entryAppName}"
                },
<#elseif vueSpecs.isConf(extraOptions, "gen.iam.security-mode", "cluster")>
            	deploy: {
                    defaultTwoDomain: "iam-services",
                    defaultContextPath: "/iam-server"
                },
</#if>
                account: {
                    onSuccess: function (principal, data) {
                        console.log("Login successful of: " + principal);
                        that.postLoginSuccess(principal);
                        return false; // 返回false会阻止自动调整
                    },
                    onError: function (errmsg) {
                        console.error("登录失败. " + errmsg);
                    }
                }
            });
        },
        removeLink() {
        }
    },
    activated() {
        this.initIamJssdk()
    },
    mounted() {
    },
    beforeRouteLeave(to, from, next) {
        this.removeLink();
        next();
    },
}
