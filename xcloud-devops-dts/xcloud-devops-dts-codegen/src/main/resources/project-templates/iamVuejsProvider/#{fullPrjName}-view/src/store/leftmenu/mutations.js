import * as types from './mutations_types'

export default {
  [types.SET_MENU_OPEN] (state) {
    state.width = '210px'; //refer:/store/leftmenu/state.js
    state.menu_flag = true;
  },
  [types.SET_MENU_CLOSE] (state) {
    state.width = '0px' // 收起按钮与左边菜单栏间隔,需配合.toggle-menu{transform: translate(2px, -50%);}
    state.menu_flag = false
  }
}
