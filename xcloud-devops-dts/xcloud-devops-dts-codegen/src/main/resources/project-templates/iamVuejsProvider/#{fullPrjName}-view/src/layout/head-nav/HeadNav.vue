<template>
  <div>
    <header class="head-nav">
      <div class="btn-toggle" @mouseenter="toggleSidebarLightbox" @click="clickToggleSidebarLightbox">
        <i class="el-icon-s-fold"></i>
      </div>

      <div class='logo-container'>
        <img data-v-12af00ba="" src="static/images/logo.png" alt="" class="logo">
      </div>

      <div class="pull-right">
        <el-dropdown  style="height:50px;line-height:50px;cursor: pointer" @command='setDialogInfo'>
          <span class="el-dropdown-link" style="height: 50px;display: block;">
            <img src="static/images/bg_icon/def_user.png" alt="" style="border-radius:50%;height: 40px;">
            {{getUsername()}}<i class="el-icon-caret-bottom el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item style="font-size:13px;" command='pass'>{{$t('message.common.profile')}}</el-dropdown-item>
            <el-dropdown-item style="font-size:13px;" command='info'>{{$t('message.common.help')}}</el-dropdown-item>
            <el-dropdown-item style="font-size:13px;" command='logout'>{{$t('message.common.logout')}}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <div class="pull-right" style="margin-right: 20px;">
        <el-dropdown @command="changeLang" style="height:50px;line-height:50px;cursor: pointer">
          <span class="el-dropdown-link" style="height: 50px;display: block;">
            {{display}}<i class="el-icon-caret-bottom el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="zh_CN">简体中文</el-dropdown-item>
            <el-dropdown-item command="en_US">US English</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <div class="pull-right" style="margin-right:12px">
        <theme-picker></theme-picker>
      </div>

      <organization-panel class="pull-right" style="margin-right:8px" ></organization-panel>

      <!-- <div class="pull-right" style="margin:10px 30px 0 0;width:240px;height:35px;">
        <el-form :model="searchParams" @submit.native.prevent>
            <el-form-item>
                <el-input v-model="searchParams.keyword" :placeholder="$t('message.common.searchInput')"
                    prefix-icon="el-icon-search" style="border-radius:0;"
                    clearable @input="change($event)" autocomplete="off"></el-input>
            </el-form-item>
        </el-form>
      </div> -->
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
    width: 160px;
    height: 30px;
    margin: 12px;
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
