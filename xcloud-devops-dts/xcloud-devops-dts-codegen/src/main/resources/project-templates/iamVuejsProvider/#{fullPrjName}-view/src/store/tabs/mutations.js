import {
    cache
} from 'utils/'

import * as types from './mutations_types'

export default {
    [types.UPDATE_TABS](state, {
        route
    }) {
        state.tabs_cur = route.path
        if (route.path != '/login') {
            if (state.list.filter(r => r.path == route.path).length == 0) {
                state.list.push({
                    name: route.name,
                    path: route.path,
                    params: route.params,
                    query: route.query,
                    hash: route.hash
                })
                cache.set('openedRoutes', state.list)
            }
        }
    },

    [types.REMOVE_TABS](state, {
        path
    }) {
        var index = state.list.findIndex(r => r.path == route.path);
        if (index == -1) {
            console.error('No found route path from store.', path);
        } else {
            state.list.splice(index, 1);
            cache.set('openedRoutes', state.list)
        }
    }
}
