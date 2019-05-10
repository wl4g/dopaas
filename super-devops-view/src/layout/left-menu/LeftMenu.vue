<template>

  <div class="left" :style="{'height':win_size.height,'width':$store.state.leftmenu.width}" id='admin-left'>
    <div class="acm">应用配置管理 ACM</div>
    <div id='left-menu'>
        <el-row class="tac">
          <el-col :span="24">
            <el-menu
              :default-active="$route.path"
              class="el-menu-vertical-demo"
              text-color="#fff"
              active-text-color="#ffd04b"
              >
              <el-menu-item @click.native="routerGo(item.path)" :index="$store.state.router.headerCurRouter+'/'+item.path" v-for="item in menu_list" :key="item.path">
                <i :class="'fa fa-'+item.icon"></i>
                <span v-if="$store.state.leftmenu.menu_flag" slot="title">{{item.name}}</span>
              </el-menu-item>
            </el-menu>
          </el-col>
        </el-row>
      <div class="toggle-menu"
           @click='toggleMenu'
           :style='{left:$store.state.leftmenu.width}'>
        <i
          :class='[{"el-icon-arrow-left":$store.state.leftmenu.menu_flag},{"el-icon-arrow-right":!$store.state.leftmenu.menu_flag}]'></i>
      </div>
    </div>
  </div>
</template>

<script>
  import LeftMenu from './LeftMenu.js'

  export default LeftMenu
</script>

<style scoped lang='less'>
  @import url(./LeftMenu.less);
  .acm{
    background: #324057;
    color: #fff;
    text-align: center;line-height: 46px;
  }
  // .el-menu li{
  //   color: #959fae;
  // }
</style>
