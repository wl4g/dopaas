/**
 * Created by <Wanglsir@gmail.com> on 2019/4/30.
 */
/**
 * 系统设置
 * @type {Object}
 */
export default [
    {
        name: '获取系统设置信息',
        method: 'getSetting',
        path: '/System/getSetting',
        type: 'get'
    },
    {
        name: '修改系统设置信息',
        method: 'updateSetting',
        path: '/System/updateSetting',
        type: 'post'
    }
]
