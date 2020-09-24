import { store } from "../../utils";
import promise from "../login/promise";
import router from '../../router/index.js'
import webView from '@/views/webview.vue'
import Layout from 'layout/routeview/Home.vue'
import Content from 'layout/routeview/Content.vue'
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

// 按parentId转换成树结构列表
function transformTreeData(list) {
    list.forEach(item => {
        // 过滤按钮类型
        if (item.type == '3') {
            return
        }
        if (item.parentId && item.parentId != 0) {
            let child = item;
            let parent = list.find(n => {
                return n.id === child.parentId;
            });
            while (parent) {
                if (!parent.children) {
                    parent.children = []
                }
                if (!parent.children.includes(child)) {
                    parent.children.push(child);
                    parent.children.sort(function (a, b) {
                        return a.sort - b.sort
                    });
                }
                if (parent.parentId != undefined || parent.parentId != null) {
                    child = parent;
                    var parentId = parent.parentId;
                    parent = null;
                    parent = list.find(n => {
                        return n.id === parentId;
                    });
                } else {
                    parent = null
                }
            }
        }
    });
    return list
}

export default {
    name: 'Init',
    data() {
        return {
            fullscreenLoading: false,
        }
    },
    mounted() {
        this.getCache();

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
                this.$alert('请使用域名作为请求地址，禁止使用localhost或ip,详情请看:\nhttps://github.com/wl4g/super-devops/blob/master/README_CN.md\n或者\nhttps://gitee.com/wl4g/super-devops/blob/master/README_CN.md', '错误', {
                    confirmButtonText: '确定'
                });
                return false;
            } else {
                return true;
            }
        },
        getCache() {
            let that = this;
            // Gets application cluster(modules)
            that.$$api_iam_clusterConfigInfo({
                fn: data => {
                    store.set("iam_system_modules", data.data);
                    console.debug("Sysmodules(cluster) config stored.");
                },
                errFn: () => {
                    // console.debug("Failed to get sysmodules from cache");
                    // try again
                    that.$$api_iam_clusterConfigInfo({
                        fn: data => {
                            // console.debug("Load sysmodules info: " + JSON.stringify(data.data))
                            store.set("iam_system_modules", data.data.list);
                            //console.info(store.get("iam_system_modules"));
                            console.debug("get cluster config success");
                        },
                    });
                },
            });
            //dict
            that.$$api_iam_dictCache({
                fn: data => {
                    //console.info(data.data)
                    store.set("dicts_cache", data.data);
                    //console.info(store.get("dicts_cache"));
                    console.info("get dict success");
                },
                errFn: () => {
                    //console.info("get dict cache fail");
                    //try again
                    that.$$api_iam_dictCache({
                        fn: data => {
                            //console.info(data.data)
                            store.set("dicts_cache", data.data);
                            //console.info(store.get("dicts_cache"));
                            console.info("get dict success");
                        },
                    });
                },
            })
        },
    },
}


