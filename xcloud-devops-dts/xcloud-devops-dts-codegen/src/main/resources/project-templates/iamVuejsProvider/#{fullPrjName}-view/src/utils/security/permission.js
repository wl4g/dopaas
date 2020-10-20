import router from '../../router'
import store from '../../store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getUserName } from '@/utils/security/iamutil' // get token from cookie
import webView from '@/views/webview.vue'
import Layout from 'layout/routeview/Home.vue'
import Content from 'layout/routeview/Content.vue'
import promise from "../../views/login/promise";
import {
    store as utilstore,
} from '../../utils/'

function getPageTitle(target) {
    var lang = sessionStorage.getItem("authzPrincipalLangAttributeName");
    if (lang == null || lang == 'zh_CN') {
        return target.displayName + ' - XCloud DevOps'
    } else {
        return target.name + ' - XCloud DevOps'
    }
}

NProgress.configure({ showSpinner: false }) // NProgress Configuration

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
        if (item.type == '3') {//[MARK1]
            /*if (!item.pageLocation || !item.routePath) {
                return
            }
            item.hidden = true;
            let parent = list.find(n => {
                return n.id === item.parentId;
            });

            if (parent) {
                if (!parent.children) {
                    parent.children = []
                }
                parent.children.push(item);
            }*/
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

function getTopParent(list, one){
    let parent = list.find(n => {
        return n.id === one.parentId;
    });
    if(parent.parentId){
        return getTopParent(list,parent);
    }else{
        return parent;
    }
}

function dealWithRedirect(list) {
    list.forEach((item, index) => {
        let firstChild = getFirstChild(list,item.id);
        if(firstChild && firstChild.routePath){
            item.redirect = firstChild.routePath;
        }
    });
}

function getFirstChild(list,parentId) {
    let childs = list.filter(item => {
        return item.parentId == parentId && item.type != 3;
    });
    if(childs){
        childs.sort(function (a, b) {
            return a.sort - b.sort
        });
        return childs[0];
    }else{
        return;
    }
}



function transformDataToRoute(list) {
    dealWithRedirect(list);

    let i = 0;
    let result = list.filter(item => {
        if (item.parentId == 0) {//顶级
            item.path = item.routePath;
            if (!item.path) {
                item.path = '/' + item.permission
            }
            //TODO 默认指向第一个子菜单
            /*if (item.children && item.children.length) {
                item.redirect = item.children[0].routePath
            }*/
            item.component = Layout;
            return true;
        }else{
            i++;
            let parent = getTopParent(list,item);
            if(!parent.children){
                parent.children = [];
            }

            if (item.type == '1') {
                item.path = item.routePath;
                if (!item.path) {
                    item.path = '/' + item.permission
                }
                if(item.pageLocation){
                    item.component = loadView(item.pageLocation);
                }
                parent.children.push(item);

            }else if (item.type == '2') {
                item.path = item.routePath ? item.routePath : 'webview' + item.name;
                item.component = webView;
                item.meta = {
                    linkhref: item.pageLocation
                };
                parent.children.push(item);

            }else { //incule item.type == '3'
                if(item.pageLocation && item.routePath){
                    let view  = loadView(item.pageLocation);
                    if(view){
                        item.path = item.routePath;
                        if (!item.path) {
                            item.path = '/' + item.permission
                        }
                        item.component = view;
                        parent.children.push(item);
                    }

                }
                console.warn("not support this type" + item.type);
            }
            return false;
        }
    });
    return result;
}

const whiteList = ['/login'] // no redirect whitelist

router.beforeEach(async (to, from, next) => {
    // start progress bar
    NProgress.start()

    document.title = getPageTitle(to.meta);

    // determine whether the user has logged in
    const hasLogin = getUserName()

    if (hasLogin) {
        if (to.path === '/login') {
            // if is logged in, redirect to the home page
            next()
            NProgress.done()
        } else {
            // determine whether the user has obtained his permission roles through getInfo
            const hasRoles = store.getters.roles && store.getters.roles.length > 0
            if (hasRoles) {
                next()
            } else {
                try {
                    // generate accessible routes map based on roles
                    const accessRoutes = await store.dispatch('generateRoutes');

                    //const accessRoutes2 = await store.dispatch('generateRoutes');
                    const accessRoutes2 = JSON.parse(JSON.stringify(accessRoutes));;

                    //promise.buildRoleRoute();
                    let res = transformTreeData(accessRoutes.slice());

                    let res2 = transformDataToRoute(accessRoutes2.slice());

                    // 设置动态路由对象
                    (function setAsyncRouter(list) {
                        list.forEach((item, index) => {
                            // 处理静态路由
                            if (item.type == '1') {
                                // 头部一级菜单
                                if (item.level == '1') {
                                    item.path = item.routePath;
                                    if (!item.path) {
                                        item.path = '/' + item.permission
                                    }

                                    // 默认指向第一个子菜单
                                    if (item.children && item.children.length) {
                                        item.redirect = item.children[0].routePath
                                    }

                                    item.component = Layout;
                                } else {
                                    // 二级菜单下有子菜单
                                    if (item.children && item.children.length) {
                                        item.path = item.permission;
                                        item.component = Content;
                                    } else {
                                        // 二级菜单没有子菜单
                                        item.path = item.routePath;
                                        let routePath = loadView(item.pageLocation);
                                        item.component = routePath;
                                    }
                                }
                            } else if (item.type == '2') {            // 处理动态路由
                                item.path = item.routePath ? item.routePath : 'webview' + item.name;
                                item.component = webView;
                                item.meta = {
                                    linkhref: item.pageLocation
                                }
                            } else if (item.type == '3') {
                                // 页面按钮权限，必须有pageLocation和routePath
                                if (item.pageLocation && item.routePath) {
                                    item.path = item.routePath;
                                    let routePath = loadView(item.pageLocation);
                                    item.component = routePath;
                                }
                                //do notthing. @See: MARK1
                            } else {
                                throw Error("Failed to create dynamic menu, because menu type is required or unsupported!menuType=" + item.type);

                            }

                            // 处理标签栏标题显示
                            if (!item.meta) {
                                item.meta = {}
                            }
                            item.meta.displayName = item.displayName;
                            item.meta.name = item.name;
                        })
                    })(res);

                    // 筛选出一级菜单
                    let highLevel = res.filter(n => {
                        return n.parentId == 0;
                    });

                    // 一级菜单按sort排序
                    highLevel = highLevel.sort(function (a, b) {
                        return a.sort - b.sort
                    });

                    //特殊处理newpipeline页面


                    utilstore.set('allRouter', res);
                    store.commit('update_routList', { routList: highLevel })
                    // dynamically add accessible routes
                    router.addRoutes(res2)

                    // hack method to ensure that addRoutes is complete
                    // set the replace: true, so the navigation will not leave a history record
                    next()
                    // next({ ...to, replace: true })
                } catch (error) {
                    // remove token and go to login page to re-login
                    //await store.dispatch('user/resetToken')
                    console.error(error);
                    Message.error(error || 'Has Error')
                    next(`/login?redirect=${to.path}`)
                    NProgress.done()
                }
            }
        }
    } else {
        if (whiteList.indexOf(to.path) !== -1) {
            // in the free login whitelist, go directly
            next()
        } else {
            // other pages that do not have permission to access are redirected to the login page.
            next(`/login?redirect=${to.path}`)
            NProgress.done()
        }
    }
})

router.afterEach(() => {
    // finish progress bar
    NProgress.done()
})
