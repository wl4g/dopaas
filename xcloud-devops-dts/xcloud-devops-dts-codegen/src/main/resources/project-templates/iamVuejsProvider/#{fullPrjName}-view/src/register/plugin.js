/**
 * Created by administrator on 2017/5/14.
 */

import { ajax } from 'utils/'
import request from 'apis/'

var plugins = {}
for (var i = 0; i < request.length; i++) {
    if (typeof request[i] === 'object' && request[i].list && Array.isArray(request[i].list)) {
        for (var j = 0; j < request[i].list.length; j++) {
            // console.debug("Registering API plugin of:", request[i].list[j])
            plugins['api_' + request[i].module + '_' + request[i].list[j].method] = (function (n, m) {
                return function ({
                    type = request[n].list[m].type,
                    dataType = request[n].list[m].dataType,
                    pathParams,
                    path = request[n].list[m].path,
                    data,
                    fn,
                    errFn,
                    headers,
                    opts,
                    sysModule = request[n].list[m].sysModule,
                } = {}) {
                    ajax.call(this, {
                        type,
                        dataType,
                        pathParams,
                        path,
                        data,
                        fn,
                        errFn,
                        headers,
                        opts,
                        sysModule,
                    })
                }
            })(i, j)
        }
    }
}

console.debug("Registered API plugins:", plugins);
export default plugins
