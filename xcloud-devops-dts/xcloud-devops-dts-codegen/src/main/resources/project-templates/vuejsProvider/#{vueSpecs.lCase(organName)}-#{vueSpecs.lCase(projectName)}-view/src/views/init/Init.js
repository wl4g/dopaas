import { store } from "../../utils";
import iputil from "../../common/iputil";
import global from "../../common/global_variable";
import ajax from '@/utils/ajax/ajax'

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

        if (this.checkHostname()) {
            var routList = this.$store.state.router.routList;
            if (this.$route.redirectedFrom && this.$route.redirectedFrom != '/') {
                // jump 跳转刷新前页面
                this.$router.push({ path: this.$route.redirectedFrom });
            } else {
                //jump to first not hidden page
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
        }
    },
    methods: {
        checkHostname() {
            let hostname = location.hostname;
            let isIp = iputil.isIp(hostname);
            if (hostname == 'localhost' || isIp) {
                this.$alert('请使用域名作为请求地址，禁止使用localhost或ip,详情请看:\nhttps://github.com/wl4g/xcloud-devops/blob/master/README_CN.md\n或者\nhttps://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md', '警告', {
                    confirmButtonText: '确定'
                });
                return false;
            } else {
                return true;
            }
        },
        // [顺序优先特殊接口，直接走ajax]登录完成时，需优先加载初始化信息
        initSystemConfiguration() {
            // 1. Load syscluster modules.
            ajax({
                type: 'get',
                path: '/clusterConfig/loadInit',
                sysModule: global.iam,
                fn: data => {
                    store.set("iam_system_modules", data.data);
                    console.debug("Loaded sysmodules config.");
                },
                errFn: () => {
                    console.error("Cannot to load sysmodules");
                },
            });
            // 2. Load sysdict.
            ajax({
                type: 'get',
                path: '/dict/loadInit',
                sysModule: global.iam,
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


