import * as types from './mutations_types'
import { getRoutes } from '@/apis/iam'

export default {
  set_cur_route: ({
    commit
  }, paths) => {
    commit(types.SET_CUR_ROUTE, paths)
  },
  generateRoutes({ commit }, roles) {
    return new Promise((resolve, reject) => {
      getRoutes().then(resp => {
        console.debug("System modules menu loaded.");
        resolve(resp.data.data)
      }).catch(error => {
        reject(error)
      })
    })
  }
}
