import {
    cache
} from '../../utils/'
import da from "element-ui/src/locale/lang/da";
import promise from './promise'
import global from "../../common/global_variable";

export default {
    name: 'login',
    data() {
        return {
        }
    },
    methods: {
        initIAMLoginUI() {
            var that = this;
            new IAMUi().initUI(document.getElementById("iam_container"), {
                deploy: global.iam,
                account: {
                    onSuccess: function (principal, data) {
                        console.debug("Logged successful for:", principal);
                        cache.set('login_username', principal);
                        that.$router.push('/');
                        return false; // 阻止SDK自动跳转
                    },
                    onError: function (errmsg) {
                        console.error("Failed to login, cause by:", errmsg);
                    }
                }
            });
        },
    },
    activated() {
        this.initIAMLoginUI()
    },
    mounted() {
    },
    beforeRouteLeave(to, from, next) {
        next();
    },
}
