import * as types from './mutations_types'
import {cache} from "../../utils";

export default {
    [types.SET_CUR_ROUTE](state, paths) {
        state.headerCurRouter = paths.rootPath
        state.leftCurRouter = paths.fullPath
    },

    [types.UPDATE_ROUTLIST](state, routList) {
        cache.set('rootDeepChildRoutes', routList.routList);
        state.routList = routList.routList
    }
}
