/**
 * Created by maolunhuang 
 */

import { Home , Content } from 'layout/'
import Main  from './main'

export default {
  path: '/main',
  name: '/',
  icon: 'inbox',
  component: Home,Content,
  redirect: '/main/configuration',
  children:Main,
}