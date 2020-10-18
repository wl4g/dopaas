import { store } from "../../utils";
import iputil from "../../common/iputil";

// 路由懒加载
const loadView = (view) => {
    // '@/views/home/overview/Overview.vue'
    try {
        if (view && view !== '') {
            return require(`@/views${view}.vue`)
        }
    } catch (err) {
        console.error('No found routing page vue file path of: ' + view)
    }
};

export default {
    name: 'Init',
    data() {
        return {
            fullscreenLoading: false,
        }
    },
    mounted() {
        this.initSystemConfiguration();
        if (!this.checkServerURI()) {
            console.warn("No supported access serverURI is IP or localhost. =>", location.hostname);
            return;
        }

        var routList = this.$store.state.router.routList;
        if (this.$route.redirectedFrom && this.$route.redirectedFrom != '/') {
            // 跳转刷新前页面
            this.$router.push({ path: this.$route.redirectedFrom });
        } else {
            // To first not hidden page
            for (let i = 0; i < routList.length; i++) {
                if (routList[i].hidden != true) {
                    let children = routList[i].children;
                    if (children) {
                        for (let k = 0; k < children.length; j++) {
                            if (children[k].hidden != true) {
                                this.$router.push(children[k].path);
                                return;
                            }
                        }
                    }
                }
            }
        }
    },
    methods: {
        // Only domain name access is allowed.
        checkServerURI() {
            let hostname = location.hostname;
            let isIp = iputil.isIp(hostname);
            if (hostname == 'localhost' || isIp) {
                this.$alert(`For the convenience of development and debugging, 
please use the domain name as the request address, do not use localhost or IP. 
For more information, more refer to: \nhttps://github.com/wl4g/xcloud-devops/blob/master/README_CN.md
\n或者\nhttps://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md`, '警告', {
                    confirmButtonText: '确定'
                });
                return false;
            } else {
                return true;
            }
        },
        // [顺序优先]当登录完成时，需优先加载初始化信息
        initSystemConfiguration() {
            // 1. Load syscluster modules.
            this.$$api_iam_clusterConfigLoadInit({
                fn: data => {
                    store.set("iam_system_modules", data.data);
                    console.debug("Loaded sysmodules config.");
                },
                errFn: () => {
                    console.error("Cannot to load sysmodules");
                },
            });
            // 2. Load sysdict.
            this.$$api_iam_dictLoadInit({
                fn: data => {
                    store.set("dicts_cache", data.data);
                    console.debug("Loaded sysmodules config.");
                },
                errFn: () => {
                    console.error("Cannot to load sysmodules");
                },
            });
        },
    },
}
