import * as types from './mutations_types'
import {store} from "../../utils";

export default {
  [types.SET_CUR_ROUTE] (state, paths) {
    state.headerCurRouter = paths.rootPath
    state.leftCurRouter = paths.fullPath
  },

  [types.UPDATE_ROUTLIST](state,routList){
    store.set("routList",routList.routList);
    state.routList = routList.routList
  }
}
