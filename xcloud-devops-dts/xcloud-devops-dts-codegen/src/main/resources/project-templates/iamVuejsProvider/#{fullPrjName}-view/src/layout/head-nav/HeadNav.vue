<template>
  <div>

    <header class="head-nav">
      <div class="btn-toggle" @click="toggleSidebarLightbox">
        <i class="el-icon-s-fold"></i>
      </div>

      <div class='logo-container'>
        <img data-v-12af00ba="" src="static/images/logo.png" alt="" class="logo">
        <!-- <a style="font-size:30px;line-height:55px;" width="100%" disabled >&nbsp;DevSecOps</a> -->
      </div>


      <!--<div class="menus">-->
          <!--<el-menu theme="dark" :default-active="$store.state.router.headerCurRouter" style="height:67px" class="el-menu-demo" mode="horizontal" unique-opened router>-->
          <!--&lt;!&ndash; v-if='!item.hidden && (($store.state.user.userinfo.access_status===1 && $store.state.user.userinfo.web_routers[item.path]) || $store.state.user.userinfo.access_status!==1)'&ndash;&gt;-->
          <!--<el-menu-item-->
            <!--v-for='(item,index) in routList'-->
            <!--:index="item.path"-->
            <!--:key='item.path'-->
            <!--v-if='!item.hidden && (($store.state.user.userinfo.access_status===1 && $store.state.user.userinfo.web_routers[item.path]) || $store.state.user.userinfo.access_status!==1)'>-->
            <!--&lt;!&ndash;<img src="/static/images/menu/CI_on.png" />&ndash;&gt;-->
            <!--&lt;!&ndash;<img :src="item.icon?item.icon:'/static/images/menu/UMC_on.png'" onerror="this.style.display='none'"/>&ndash;&gt;-->
            <!--<svg class="top-menu-iconfont" aria-hidden="true" style="cursor:pointer;">-->
              <!--<use :xlink:href="'#'+item.icon"></use>-->
            <!--</svg>-->
            <!--{{getMenuName(item)}}&lt;!&ndash;{{item.path}}&ndash;&gt;-->
           <!--</el-menu-item>-->
        <!--</el-menu>-->
      <!--</div>-->

        <!--<el-col :span="4" style="text-align: right;">
          <span class='username'>
            <el-dropdown trigger="hover" @command='setDialogInfo'>
              <span class="el-dropdown-link">
                {{getUsername()}}
                <i class="el-icon-caret-bottom el-icon&#45;&#45;right"></i>
              </span>
              <el-col :span="3" class='logo-container' style="width:8%;margin-top:-6px;">
                <img src="static/images/bg_icon/def_user.png" class='logo' alt="" style="height:40px;margin-right:10px;margin-top:10px;border-radius:50%;">
              </el-col>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item style="font-size:13px;" command='pass'>Your profile</el-dropdown-item>
                <el-dropdown-item style="font-size:13px;" command='info'>Help</el-dropdown-item>
                <el-dropdown-item style="font-size:13px;" command='logout'>Sign out</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span>
        </el-col>-->


      <div class="pull-right">
        <el-dropdown  style="height:50px;line-height:50px;cursor: pointer" @command='setDialogInfo'>
          <span class="el-dropdown-link" style="height: 50px;display: block;">
            <img src="static/images/bg_icon/def_user.png" alt="" style="border-radius:50%;height: 40px;">
            {{getUsername()}}<i class="el-icon-caret-bottom el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item style="font-size:13px;" command='pass'>Your profile</el-dropdown-item>
            <el-dropdown-item style="font-size:13px;" command='info'>Help</el-dropdown-item>
            <el-dropdown-item style="font-size:13px;" command='logout'>Sign out</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <div class="pull-right" style="margin-right: 20px;">
        <!--<el-select v-model="lang" placeholder="请选择" @change="changeLang">
          <el-option value="cn" label="中文"></el-option>
          <el-option value="en" label="en"></el-option>
        </el-select>-->
        <el-dropdown @command="changeLang" style="height:50px;line-height:50px;cursor: pointer">
          <span class="el-dropdown-link" style="height: 50px;display: block;">
            {{display}}<i class="el-icon-caret-bottom el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="zh_CN">中文</el-dropdown-item>
            <el-dropdown-item command="en_US">English</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <div class="pull-right" style="margin-right: 12px">
        <theme-picker></theme-picker>
      </div>

      <organization-panel class="pull-right" style="margin-right: 10px" ></organization-panel>

    </header>

    <el-dialog size="small" :title="dialog.title" :visible.sync="dialog.show_pass" :close-on-click-modal="false">
      <el-form style="margin:20px;width:80%;"
               label-width="100px"
               :model="dialog.user_info"
               :rules="dialog.user_info_rules"
               ref='user_info'>
        <el-form-item class='edit-form'
                      label="邮箱" prop='email'>
          <el-input v-model="dialog.user_info.email" disabled placeholder='常用邮箱'></el-input>
        </el-form-item>
        <el-form-item class='edit-form' label="用户名称" prop='username'>
          <el-input v-model="dialog.user_info.username" disabled placeholder='用户名'></el-input>
        </el-form-item>
        <el-form-item class='edit-form' label="当前密码" prop='old_password'>
          <el-input type='password' placeholder='当前密码'
            auto-complete='off' v-model="dialog.user_info.old_password"></el-input>
        </el-form-item>
        <el-form-item class='edit-form' label="新密码" prop='password'>
          <el-input
            type='password'
            placeholder='新密码'
            auto-complete='off'
            v-model="dialog.user_info.password"></el-input>
        </el-form-item>
        <el-form-item class='edit-form'
                      label="确认密码"
                      prop='password_confirm'>
          <el-input
            type='password'
            placeholder='确认密码'
            auto-complete='off'
            v-model="dialog.user_info.password_confirm"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="dialog.show_pass = false">取 消</el-button>
                <el-button type="primary" @click="updUserPass('user_info')">确 定</el-button>
            </span>
    </el-dialog>

    <el-dialog size="small" :title="dialog.title"
               :visible.sync="dialog.show_set">
      <el-form style="margin:20px;width:80%;"
               label-width="100px"
               v-model='dialog.set_info'
               ref='set_info'>
        <el-form-item label="登录方式">
          <el-select placeholder="请选择登录方式"
                     v-model='dialog.set_info.login_style'>
            <el-option label="单一登录" value="1"></el-option>
            <el-option label="多点登录" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="禁止修改密码">
          <el-select placeholder="请选择用户"
                     multiple
                     v-model='dialog.set_info.disabled_update_pass'>
            <!-- value的值的ID是number，disabled_update_pass的元素中的是字符串，
                所以在value上，需要拼装一个空串，来转化
                因为elementUI内部用了===
            -->
            <el-option
              v-for='(user,index) in dialog.set_info.select_users'
              :key='index'
              :label='user.username'
              :value='user.id+""'></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="dialog.show_set = false">取 消</el-button>
                <el-button type="primary" @click="onUpdateSetting">确 定</el-button>
            </span>
    </el-dialog>
  </div>
</template>

<script>
  import HeadNavJs from './HeadNav.js'
  import OrganizationPanel from "../../components/organization-panel/OrganizationPanel";
  export default HeadNavJs
</script>

<style scoped lang='less'>
  .logo-container {
    float: left;
    height: 50px;
    text-align: center;
  }
  .logo {
    height: 50px;
    max-width: 100%;
  }
  .fa-user {
    position: relative;
    top: -2px;
    margin-right: 4px;
  }
  .head-nav {
    padding-right: 20px;
    width: 100%;
    height: 50px;
    background: #fff;
    position: fixed;
    top: 0px;
    left: 0px;
    z-index: 2000;
    box-shadow: rgba(0, 0, 0, 0.08) 0px 1px 4px 0px;

    .menus {
      float: left;
    }

  .logout {
      width: 60px;
      height: 67px;
      line-height: 67px;
      text-align: center;
      float: right;
      cursor: pointer;
    }
  }
  .username {
    height: 50px;
    line-height: 50px;
    cursor: pointer;
    .el-dropdown {
      color: #FFF;
    }
  }
  .home .head-nav .el-menu-item {
    padding: 0 8px;
    height:67px ;
    line-height: 67px;
  }
  .home .head-nav .el-menu {
    border-bottom: 0 none;
  }

  .btn-toggle {
    float: left;
    width: 48px;
    height: 50px;
    line-height: 62px;
    color: #fff;
    text-align: center;
    background-color: #20a1ff;
    cursor: pointer;
  }
  .btn-toggle .el-icon-s-fold {
    font-size: 24px;
  }
</style>
