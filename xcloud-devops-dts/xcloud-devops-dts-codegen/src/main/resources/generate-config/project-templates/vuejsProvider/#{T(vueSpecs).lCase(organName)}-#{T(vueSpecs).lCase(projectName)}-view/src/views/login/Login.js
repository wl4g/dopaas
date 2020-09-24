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
                deploy: {
                    // e.g. http://127.0.0.1:14040/iam-server
                    // e.g. http://localhost:14040/iam-server
                    // e.g. http://iam.wl4g.debug/iam-server
                    //baseUri: "http://localhost:14040/iam-server",
                    defaultTwoDomain: "iam", // IAM后端服务部署二级域名，当iamBaseUri为空时，会自动与location.hostnamee拼接一个IAM后端地址.
                    defaultContextPath: "/iam-server"
                },
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
