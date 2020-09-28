<template>
<div class="left" :style="{'height':win_size.height,'width':$store.state.leftmenu.width}" id='admin-left'>
    <div id='left-menu' class="dynamic-menus">
        <el-row class="tac">
            <el-col :span="24">
                <el-menu :default-active="$route.path" class="el-menu-vertical-demo" active-text-color="#20a1ff">

                    <sidebar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path" />
                </el-menu>
            </el-col>
        </el-row>
        <div class="toggle-menu" @click='toggleMenu' :style='{left:$store.state.leftmenu.width}'>
            <i :class='[{"el-icon-arrow-left":$store.state.leftmenu.menu_flag},{"el-icon-arrow-right":!$store.state.leftmenu.menu_flag}]'></i>
        </div>
    </div>

    <transition name="sidebar-fade">
        <div class="sidebar-lightbox" v-show="lightBoxVisible">
            <div class="sidebar-lightbox-list">
                <div class="sidebar-lightbox-header" @mouseenter="beforeOpenMaskLayer" @mouseleave="resetMaskLayer" @click="toggleMaskLayer">
                    <i class="el-icon-s-grid"></i>
                    产品与服务
                    <i class="el-icon-arrow-right"></i>
                </div>

                <el-menu theme="dark" :default-active="$store.state.router.headerCurRouter" class="el-menu-demo" mode="horizontal" unique-opened router @select="parentLevelMenuClick">
                    <el-menu-item v-for='(item,index) in routList' :index="item.path" :key='item.path' v-if='!item.hidden && (($store.state.user.userinfo.access_status===1 && $store.state.user.userinfo.web_routers[item.path]) || $store.state.user.userinfo.access_status!==1)'>
                        <svg class="top-menu-iconfont" aria-hidden="true" style="cursor:pointer;">
                            <use :xlink:href="'#'+item.icon"></use>
                        </svg>
                        {{getMenuName(item)}}
                        <!--{{item.path}}-->
                    </el-menu-item>
                </el-menu>
            </div>
        </div>
    </transition>

    <div class="bottom-layer" v-show="lightBoxVisible" @click="parentLevelMenuClick"></div>

    <transition name="mask-fade">

      <div class="menu-list-mask" v-show="maskVisible">
        <div class="list-mask-search-bar" :class="isKeyWordFocus ? 'active': ''">
          <el-input class="list-mask-search-input" ref="maskSearchInput" v-model="keyword" placeholder="请输入关键字搜索" prefix-icon="el-icon-search" @keydown.native.enter="handleKeyWordSearch" @focus="isKeyWordFocus=true" @blur="isKeyWordFocus=false"></el-input>
        </div>
        <div class="mask-list">
          <div class="mask-list-item" v-for="(item,name) of routerGroupByClassify">
            <p class="mask-list-item-title">{{name}}</p>
            <ul class="mask-list-item-ul">
              <li v-for="n of item">
                <span class="mask-list-item-link" @click="handleRouteLinkClick(n.routePath)"> {{n.displayName}}</span>
              </li>
            </ul>
          </div>
        </div>

            <i class="mask-close-btn el-icon-close" @click="parentLevelMenuClick"></i>
        </div>
    </transition>
</div>
</template>

<script>
import LeftMenu from './LeftMenu.js'

export default LeftMenu
</script>

<style lang="less" scoped>
@import url(./LeftMenu.less);

.acm {
    background: #324057;
    color: #fff;
    text-align: center;
    line-height: 46px;
}

.sidebar-lightbox {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 1999;
    background-color: #fff;
    box-shadow: rgba(0, 0, 0, 0.2) 0px 1px 4px 0px;
    /*禁止复制*/
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
    user-select: none;
}

.sidebar-lightbox-list {
    height: 100%;
    overflow-y: auto;
}

.sidebar-lightbox-header {
    position: relative;
    display: flex;
    align-items: center;
    padding: 8px 18px;
    font-size: 12px;
    color: #333;
    line-height: 32px;
    cursor: pointer;
    background: rgb(247, 247, 247);
    border-bottom: 1px solid rgb(222, 222, 222);
}

.sidebar-lightbox-header .el-icon-s-grid {
    margin-right: 4px;
    color: #909399;
    font-size: 16px;
}

.sidebar-lightbox-header .el-icon-arrow-right {
    position: absolute;
    top: 16px;
    right: 12px;
    color: rgb(222, 222, 222);
    font-size: 16px;
}

.menu-list-mask {
    position: fixed;
    top: 50px;
    left: 190px;
    bottom: 0;
    z-index: 1998;
    display: flex;
    flex-direction: column;
    padding: 24px 32px;
    width: 980px;
    background: rgb(247, 247, 247);
}

.mask-list {
    flex: 1 1 0%;
    overflow-y: auto;
}

.mask-list-item {
    display: inline-block;
    width: 25%;
    padding-right: 12px;
    margin-bottom: 20px;
    vertical-align: top;
    box-sizing: border-box;
}

.mask-list-item-title {
    margin-bottom: 8px;
    font-weight: bold;
    line-height: 32px;
    color: rgb(51, 51, 51);
}

.mask-list-item-ul {
    list-style: none;
    line-height: 30px;
}

.mask-list-item-link {
    cursor: pointer;
    color: #999999;
}

.mask-list-item-link:hover {
    color: #323231;
}

.bottom-layer {
    position: fixed;
    left: 0;
    right: 0;
    top: 50px;
    bottom: 0;
    z-index: 1998;
    background: rgba(0, 0, 0, .2);
}

.list-mask-search-bar {
    margin-bottom: 24px;
}

.mask-close-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    font-size: 20px;
    color: rgb(51, 51, 51);
    cursor: pointer;
}
</style><style lang="less">
.dynamic-menus .el-menu {
    background-color: #eae8e4;
    border-right: 0 none;
}

.dynamic-menus .el-menu-item,
.dynamic-menus /deep/ .el-submenu__title {
    height: 48px;
    line-height: 48px;
    /*禁止复制*/
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
    user-select: none;
}

.dynamic-menus .el-menu-item:focus,
.dynamic-menus .el-menu-item:hover,
.dynamic-menus .el-menu-item.is-active {
    color: #20a1ff;
    background-color: #fff;
}

.dynamic-menus .el-submenu__title:hover {
    background-color: #efdede;
}

.dynamic-menus .el-menu-item .icon,
.dynamic-menus .el-submenu__title .icon {
    margin-right: 8px;
}

.dynamic-menus .el-menu a:hover {
    text-decoration: none;
}

.dynamic-menus .el-menu-item:focus,
.dynamic-menus .el-menu-item:hover,
.dynamic-menus .el-menu-item.is-active {
    background-color: #DEDEDE;
}

.dynamic-menus .el-menu .svg-icon {
    margin-right: 6px;
}

.sidebar-lightbox .el-menu.el-menu--horizontal {
    border-bottom: 0 none;
}

.sidebar-lightbox .el-menu--horizontal>.el-menu-item {
    float: none;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 12px;
}

.sidebar-lightbox .el-menu--horizontal>.el-menu-item.is-active {
    color: #20a1ff;
    border-bottom: 2px solid transparent;
    background-color: rgb(222, 222, 222);
}

.sidebar-lightbox .el-menu--horizontal>.el-menu-item:not(.is-disabled):focus,
.sidebar-lightbox .el-menu--horizontal>.el-menu-item:not(.is-disabled):hover,
.sidebar-lightbox .el-menu--horizontal>.el-submenu .el-submenu__title:hover {
    background-color: rgba(0, 0, 0, 0.05);
}

.list-mask-search-input .el-input__inner {
    background-color: transparent;
    border-top: 0 none;
    border-right: 0 none;
    border-left: 0 none;
    border-radius: 0;
}

.list-mask-search-input .el-input__prefix {
    left: 0;
}

.list-mask-search-input .el-input__icon {
    font-size: 16px;
}

.list-mask-search-bar.active .el-input__icon {
    color: #409EFF;
}

.mask-fade-enter-active {
    transition: all 0.25s cubic-bezier(0, 0, 0.2, 1) 0s;
}

.mask-fade-enter {
    transform: translateX(-1240px);
}

.sidebar-fade-enter-active {
    transition: all 0.25s ease-in-out 0s;
}

.sidebar-fade-enter {
    transform: translateX(-100%);
}
</style>
