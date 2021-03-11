/**
 * Created by Administrator on 2017/5/11.
 */
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import { Login } from 'views/'
import { Init } from 'views/'
import Layout from 'layout/routeview/Home.vue'
import middleware from '@/views/middleware.vue'

const createRouter = () => new Router({
  routes: [
    {
      path: '*',
      hidden: true,
      redirect(to) {
        return '/init'
      }
    },
    {
      path: '/',
      name: 'Login',
      hidden: true,
      redirect(to) {
        return '/init'
      }
    }, {
      path: '/login',
      name: '登录',
      hidden: true,
      component: Login
    }, {
      path: '/init',
      name: '初始化',
      hidden: true,
      component: Init
    },
    {
      path: '/common',
      name: '框架页',
      component: Layout,
      children: [
        {
          path: 'middleware',
          name: '打开外部链接',
          component: middleware
        }
      ]
    },
    //Function,
    // Home,
    // Umc,
    // Srm,
    // Ci,
    // Scm,
    // Iam,
    // Share,
  ]
})

const router =  createRouter();

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
