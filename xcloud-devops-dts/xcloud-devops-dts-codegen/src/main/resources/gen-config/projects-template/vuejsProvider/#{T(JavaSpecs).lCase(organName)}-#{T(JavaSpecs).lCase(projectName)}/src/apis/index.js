/**
 * 导出所有模块需要用到接口
 * 一级属性：模块名
 * 一级属性中的方法：当前模块需要用的接口
 */
import user from './user/'
import system from './system/'
import iam from './iam/'

export default [
    {
        module: 'user',
        name: '用户管理',
        list: user
    }, {
        module: 'system',
        name: '系统设置',
        list: system
    }, {
        module: 'iam',
        name: '权限管理',
        list: iam
    }
]
