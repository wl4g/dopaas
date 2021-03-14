/**
 * Created by Administrator on 2017/5/10.
 */
import {
  cache
} from 'utils/'

import * as types from './mutations_types'

export default {
  [types.UPDATE_USERINFO] (state, userDb) {
    state.userinfo = userDb.userinfo || {}
    cache.set('userinfo', state.userinfo)
  },

  [types.REMOVE_USERINFO] (state) {
    cache.remove('userinfo')
    state.userinfo = {}
  },

  [types.UPDATE_REMUMBER] (state, userDb) {
    state.remumber.remumber_flag = userDb.remumber_flag
    state.remumber.remumber_login_info = userDb.remumber_login_info

    cache.set('remumber_flag', state.remumber.remumber_flag)
    cache.set('remumber_login_info', state.remumber.remumber_login_info)
  },

  [types.REMOVE_REMUMBER] (state) {
    cache.remove('remumber_flag')
    cache.remove('remumber_login_info')

    state.remumber.remumber_flag = false
    state.remumber.remumber_login_info = {
      username: '',
      token: ''
    }
  }
}
