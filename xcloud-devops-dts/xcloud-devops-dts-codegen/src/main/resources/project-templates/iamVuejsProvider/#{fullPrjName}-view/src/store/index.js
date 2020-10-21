import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'

Vue.use(Vuex)

import user from './user/'
import global from './global/'
import leftmenu from './leftmenu/'
import router from './router/'
import tabs from './tabs/'

export default new Vuex.Store({
  modules: {
    user,
    global,
    router,
    leftmenu,
    tabs
  },
  getters
})
