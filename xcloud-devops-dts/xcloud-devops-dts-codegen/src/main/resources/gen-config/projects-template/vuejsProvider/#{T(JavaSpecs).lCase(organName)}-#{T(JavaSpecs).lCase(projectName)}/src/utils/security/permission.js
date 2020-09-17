import router from '../../router'
import store from '../../store'
import { Message } from 'element-ui'
import { getUserName } from '@/utils/security/iamutil' // get token from cookie
import webView from '@/views/webview.vue'
import Layout from 'layout/routeview/Home.vue'
import Content from 'layout/routeview/Content.vue'
import promise from "../../views/login/promise";
import {
  store as utilstore,
} from '../../utils/'

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

const whiteList = ['/login'] // no redirect whitelist

router.beforeEach(async (to, from, next) => {
  // determine whether the user has logged in
  const hasLogin = getUserName()

  if (hasLogin) {
    if (to.path === '/login') {
      // if is logged in, redirect to the home page
      next()
    } else {
      // determine whether the user has obtained his permission roles through getInfo
      const hasRoles = store.getters.roles && store.getters.roles.length > 0
      if (hasRoles) {
        next()
      } else {
        try {
          // generate accessible routes map based on roles
          const accessRoutes = await store.dispatch('generateRoutes')

          //promise.buildRoleRoute();
          let res = transformTreeData(accessRoutes.slice());

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
              }else if(item.type == '3'){
                //do notthing. @See: MARK1
              }else{
                throw Error("Failed to create dynamic menu, because menu type is required or unsupported!menuType="+ item.type);

              }

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

          // 特殊处理newpipeline页面
          highLevel.forEach(n => {
            if (n.permission === 'ci') {
              n.children.push({
                path: '/ci/pipelineedit',
                component: require("@/views/ci/pipelineedit/PipelineEdit.vue"),
                name: 'pipelineedit',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/ci/pipehisdetail',
                component: require("@/views/ci/pipehisdetail/PipeHisDetail.vue"),
                name: 'pipehisdetail',
                icon: '',
                hidden: true
              })
            }
            if (n.permission === 'doc') {
              n.children.push({
                path: '/doc/diff',
                component: require("@/views/doc/diff/Diff.vue"),
                name: 'docDiff',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/doc/mdEdit',
                component: require("@/views/doc/mdedit/MdEdit.vue"),
                name: 'mdEdit',
                icon: '',
                hidden: true
              })
            }
            if (n.permission === 'erm') {
              n.children.push({
                path: '/erm/clusteredit',
                component: require("@/views/erm/clusteredit/ClusterEdit.vue"),
                name: 'clusteredit',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/erm/dnsprivateresolution',
                component: require("@/views/erm/dnsprivateresolution/DnsPrivateResolution.vue"),
                name: 'dnsprivateresolution',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/erm/dnsprivateblacklist',
                component: require("@/views/erm/dnsprivateblacklist/DnsPrivateBlacklist.vue"),
                name: 'dnsprivateblacklist',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/gw/gatewayDetail',
                component: require("@/views/gw/gatewaydetail/GatewayDetail.vue"),
                name: 'gatewayDetail',
                icon: '',
                hidden: true
              });
            }
            if (n.permission === 'coss') {
              n.children.push({
                path: '/coss/bucketdetail',
                component: require("@/views/coss/bucketdetail/BucketDetail.vue"),
                name: 'bucketdetail',
                icon: '',
                hidden: true
              });
              n.children.push({
                path: '/coss/fs',
                component: require("@/views/coss/fs/Fs.vue"),
                name: 'fs',
                icon: '',
                hidden: true
              })
            }
            if (n.permission === 'umc') {
              n.children.push({
                path: '/umc/custom/engineedit',
                component: require("@/views/umc/engineedit/EngineEdit.vue"),
                name: 'engineedit',
                icon: '',
                hidden: true
              });
              n.children.push({
                path: '/umc/custom/mysqldatasource',
                component: require("@/views/umc/mysqldatasource/MysqlDataSource.vue"),
                name: 'mysqldatasource',
                icon: '',
                hidden: true
              });
            }
            if (n.permission === 'vcs') {
              n.children.push({
                path: '/vcs/project',
                component: require("@/views/vcs/project/Project.vue"),
                name: 'project',
                icon: '',
                hidden: true
              });
              n.children.push({
                path: '/vcs/projectdetail',
                component: require("@/views/vcs/projectdetail/ProjectDetail.vue"),
                name: 'projectdetail',
                icon: '',
                hidden: true
              });
            }
            if (n.permission === 'dts') {
              n.children.push({
                path: '/dts/tableedit',
                component: require("@/views/dts/tableedit/TableEdit.vue"),
                name: 'tableedit',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/dts/table',
                component: require("@/views/dts/table/Table.vue"),
                name: 'table',
                icon: '',
                hidden: true
              })
              n.children.push({
                path: '/dts/projectedit',
                component: require("@/views/dts/projectedit/ProjectEdit.vue"),
                name: 'projectedit',
                icon: '',
                hidden: true
              })
            }
          });

          utilstore.set('allRouter',res);
          store.commit('update_routList', { routList: highLevel })
          // dynamically add accessible routes
          router.addRoutes(highLevel)

          // hack method to ensure that addRoutes is complete
          // set the replace: true, so the navigation will not leave a history record
          next()
          // next({ ...to, replace: true })
        } catch (error) {
          // remove token and go to login page to re-login
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
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
    }
  }
})

router.afterEach(() => {

})
