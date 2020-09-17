<template>
  <!--<el-color-picker-->
          <!--v-model="theme"-->
          <!--:predefine="['#409EFF', '#1890ff', '#304156','#212121','#11a983', '#13c2c2', '#6959CD', '#f5222d', ]"-->
          <!--class="theme-picker"-->
          <!--popper-class="theme-picker-dropdown"-->
  <!--&gt;-->
  <!--</el-color-picker>-->
  <el-dropdown @command="changeTheme" style="height:50px;line-height:50px;cursor: pointer">
    <!--<div class="picker-trigger">-->
      <!--<span class="picker-color">-->
        <!--<span class="picker-color-inner" :style="{backgroundColor: theme}"></span>-->
      <!--</span>-->
      <!--<span class="picker-icon el-icon-arrow-down"></span>-->
    <!--</div>-->
    <div>
      <svg t="1579313766225" class="picker-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2348" width="22" height="22"><path d="M311.224 428.616V40.96C186.696 93.99 87.406 195.29 36.936 321.096l274.286 274.286 0.002-166.766z" fill="#AFE164" p-id="2349"></path><path d="M428.616 311.22L702.904 36.936C643.84 13.164 579.474 0 512 0c-71.314 0-139.154 14.628-200.776 40.96v387.656L428.616 311.22z" fill="#D7E664" p-id="2350"></path><path d="M983.04 311.22H595.384l117.394 117.396 274.286 274.286C1010.836 643.84 1024 579.474 1024 512c0-71.314-14.628-139.154-40.96-200.78z" fill="#FFAA64" p-id="2351"></path><path d="M712.776 595.384V983.04c124.526-53.03 223.816-154.33 274.286-280.136L712.776 428.616v166.768z" fill="#FF6469" p-id="2352"></path><path d="M595.384 712.776L321.096 987.064C380.16 1010.834 444.526 1024 512 1024c71.316 0 139.154-14.628 200.776-40.96V595.384L595.384 712.776z" fill="#C3B4FA" p-id="2353"></path><path d="M428.616 712.776H40.96c53.03 124.526 154.33 223.816 280.136 274.286l274.286-274.286H428.616z" fill="#7DD2F0" p-id="2354"></path><path d="M311.224 595.384L36.936 321.096C13.164 380.16 0 444.526 0 512c0 71.314 14.628 139.154 40.96 200.776h387.656L311.224 595.384z" fill="#7DE6C3" p-id="2355"></path><path d="M595.384 311.22H983.04C930.01 186.696 828.71 87.404 702.904 36.936L428.616 311.22h166.768z" fill="#FFE669" p-id="2356"></path></svg>
      <i class="el-icon-caret-bottom el-icon--right"></i>
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="light" :disabled="theme=='#fff'">{{$t('message.common.light')}}</el-dropdown-item>
      <el-dropdown-item command="dark" :disabled="theme=='#191c23'">{{$t('message.common.night')}}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import ThemePickerJs from './ThemePicker.js'

  export default ThemePickerJs
</script>
<style lang='less'>
  .theme-message,
  .theme-picker-dropdown {
    z-index: 99999 !important;
  }
  .theme-picker .el-color-picker__trigger {
    height: 26px !important;
    width: 26px !important;
    padding: 2px;
  }
  .theme-picker-dropdown .el-color-dropdown__link-btn {
    display: none;
  }

  .picker-trigger {
    position: relative;
    display: inline-block;
    height: 40px;
    width: 40px;
    padding: 4px;
    font-size: 0;
    cursor: pointer;
    vertical-align: middle;
  }
  .picker-color {
    position: relative;
    display: block;
    box-sizing: border-box;
    border: 1px solid #dedede;
    border-radius: 2px;
    width: 100%;
    height: 100%;
    text-align: center;
  }
  .picker-color-inner {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
  }
  .picker-icon {
    display: inline-block;
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    color: #409EFF;
    text-align: center;
    font-size: 12px;
    transform: translate3d(-50%,-50%,0);
  }

  .picker-svg {
    vertical-align: middle;
  }

</style>
