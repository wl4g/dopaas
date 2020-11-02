import router from '../../router'
import store from '../../store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import webView from '@/views/webview.vue'
import Layout from 'layout/routeview/Home.vue'
import Content from 'layout/routeview/Content.vue'
import cache from '../cache'
import i18nutil from '../../common/i18nutil'

NProgress.configure({ showSpinner: true }) // NProgress Configuration
const whiteList = ['/login'] // Anon path whitelist.

// 路由懒加载
const loadView = (view) => {
    // e.g: '@/views/home/overview/Overview.vue'
    try {
        if (view && view !== '') {
            return require(`@/views${view}.vue`)
        }
    } catch (err) {
        console.error('No found route vue page file:', view, err)
    }
};

// 按parentId转换成树结构列表
function transform2TreeRoutes(list) {
    list.forEach(item => {
        if (item.type == '3') { // 排除'按钮'菜单
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

// 查找顶级跟菜单
function findRootParent(list, one) {
    let parent = list.find(n => n.id === one.parentId);
    if (parent.parentId) {
        return findRootParent(list, parent);
    } else {
        return parent;
    }
}

// 默认指向第一个子菜单
function processFirstRedirect(list) {
    let findFirstChild = function (list, parentId) {
        let childs = list.filter(item => {
            return item.parentId == parentId && item.type != 3;
        });
        if (childs) {
            childs.sort(function (a, b) {
                return a.sort - b.sort
            });
            return childs[0];
        } else {
            return;
        }
    };
    list.forEach((item, index) => {
        let firstChild = findFirstChild(list, item.id);
        if (firstChild && firstChild.routePath) {
            if(firstChild.routeNamespace=='/'){
                item.redirect = '/' + firstChild.permission.replace(':','_');
            }else{
                item.redirect = firstChild.routePath;
            }
        }
    });
}

function transform2OneChildrenRoutes(list) {
    processFirstRedirect(list);

    let i = 0;
    let flatOneChildRoutes = list.filter(item => {
        if (item.parentId == 0) {//顶级
            item.path = item.routePath;
            if (!item.path || item.routeNamespace=='/') {
                item.path = '/' + item.permission.replace(':','_');
            }
            item.component = Layout;
            return true;
        } else {
            i++;
            let parent = findRootParent(list, item);
            if (!parent.children) {
                parent.children = [];
            }
            if (item.type == '1') {
                item.path = item.routePath;
                if (!item.path || item.routeNamespace=='/') {
                    item.path = '/' + item.permission.replace(':','_');
                }
                if (item.pageLocation) {
                    item.component = loadView(item.pageLocation);
                }
                parent.children.push(item);
            } else if (item.type == '2') {
                item.path = item.routePath ? item.routePath : 'webview' + item.nameEn;
                item.component = webView;
                item.meta = {
                    linkhref: item.pageLocation
                };
                parent.children.push(item);
            } else { // include item.type == '3'
                if (item.pageLocation && item.routePath) {
                    let view = loadView(item.pageLocation);
                    if (view) {
                        item.path = item.routePath;
                        if (!item.path || item.routeNamespace=='/') {
                            item.path = '/' + item.permission.replace(':','_');
                        }
                        item.component = view;
                        parent.children.push(item);
                    }
                }
                console.warn("not support this type" + item.type);
            }
            // 处理标签栏标题显示
            if (!item.meta) {
                item.meta = {}
            }
            item.meta.nameZh = item.nameZh;
            item.meta.nameEn = item.nameEn;
            item.name = item.nameEn;
            return false;
        }
    });
    return flatOneChildRoutes;
}

router.beforeEach(async (to, from, next) => {
    // Start loading progdeepMenuRoutess
    NProgress.start();

    document.title = i18nutil.getPageTitle(to.meta);

    // determine whether the user has logged in
    let hasLogin = cache.get("login_username");
    if (!hasLogin) {
        if (whiteList.indexOf(to.path) !== -1) {
            // in the free login whitelist, go directly
            next()
        } else {
            // other pages that do not have permission to access are redirected to the login page.
            next(`/login?redirect=${to.path}`)
            NProgress.done()
        }
        return;
    }

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
                // Generate accessible routes map basedon roles
                const generateRoutes = await store.dispatch('generateRoutes');
                const cloneGenerateRoutes = JSON.parse(JSON.stringify(generateRoutes));

                let deepChildRoutes = transform2TreeRoutes(generateRoutes.slice());
                let flatOneChildRoutes = transform2OneChildrenRoutes(cloneGenerateRoutes.slice());

                // 设置动态路由对象
                (function setAsyncRouter(list) {
                    list.forEach((item, index) => {
                        // 处理静态路由
                        if (item.type == '1') {
                            // 头部一级菜单
                            if (item.level == '1') {
                                item.path = item.routePath;
                                if (!item.path || item.routeNamespace=='/') {
                                    item.path = '/' + item.permission.replace(':','_');
                                }

                                // 默认指向第一个子菜单
                                if (item.children && item.children.length) {
                                    item.redirect = item.children[0].routePath
                                }

                                item.component = Layout;
                            } else {
                                // 二级菜单下有子菜单
                                if (item.children && item.children.length) {
                                    item.path = item.permission.replace(':','_');
                                    item.component = Content;
                                } else {
                                    // 二级菜单没有子菜单
                                    item.path = item.routePath;
                                    let routePath = loadView(item.pageLocation);
                                    item.component = routePath;
                                }
                            }
                        } else if (item.type == '2') {            // 处理动态路由
                            item.path = item.routePath ? item.routePath : 'webview' + item.nameEn;
                            item.component = webView;
                            item.meta = {
                                linkhref: item.pageLocation
                            }
                        } else if (item.type == '3') {
                            // 页面按钮权限，必须有pageLocation和routePath
                            if (item.pageLocation && item.routePath) {
                                item.path = item.routePath;
                                if (!item.path || item.routeNamespace=='/') {
                                    item.path = '/' + item.permission.replace(':','_');
                                }
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
                        item.meta.nameZh = item.nameZh;
                        item.meta.nameEn = item.nameEn;
                        item.name = item.nameEn;
                    })
                })(deepChildRoutes);

                // 查找顶级菜单
                let rootDeepChildRoutes = deepChildRoutes.filter(n => n.parentId == 0);
                // 排序顶级菜单
                rootDeepChildRoutes = rootDeepChildRoutes.sort((a, b) => a.sort - b.sort);

                //fullOriginRoutes
                cache.set('deepChildRoutes', deepChildRoutes);
                store.commit('update_routList', { routList: rootDeepChildRoutes });

                // 动态添加路径
                router.addRoutes(flatOneChildRoutes);
                cache.set('flatOneChildRoutes', flatOneChildRoutes);

                // hack method to ensure that addRoutes is complete
                // set the replace: true, so the navigation will not leave a history record
                next()
                // next({ ...to, replace: true })
            } catch (error) {
                // remove token and go to login page to re-login
                //await store.dispatch('user/deepMenuRoutesetToken')
                console.error(error);
                Message.error(error || 'Has Error')
                next(`/login?redirect=${to.path}`)
                NProgress.done()
            }
        }
    }
})

router.afterEach(() => {
    // finish progdeepMenuRoutess bar
    NProgress.done()
})
