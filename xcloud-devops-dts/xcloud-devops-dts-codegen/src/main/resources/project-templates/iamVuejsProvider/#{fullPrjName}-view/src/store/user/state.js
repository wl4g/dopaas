/**
 * Created by Administrator on 2017/5/10.
 */
import {
  cache
} from 'utils/'

export default {
  // 登录成功后的用户信息
  userinfo: cache.get('userinfo') || {},

  // 记住密码相关信息，现在暂且只做记住一个账号密码
  // 后期：每次登录成功一次，就缓存到列表中，然后在登录表单，输入时，会出现下拉列表选择之前登录过得用户
  remumber: {
    remumber_flag: !!cache.get('remumber_flag'),
    remumber_login_info: cache.get('remumber_login_info') || {
      username: '',
      token: ''
    }
  }
}
