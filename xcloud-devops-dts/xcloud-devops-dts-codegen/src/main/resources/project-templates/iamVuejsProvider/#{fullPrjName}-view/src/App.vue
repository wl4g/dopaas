<template>
  <div id="app">
    <transition name="bounce">
      <keep-alive exclude="Init">
        <router-view></router-view>
      </keep-alive>
    </transition>
  </div>
</template>

<script>
export default {
  name: "app",
  components: {},
  data() {
    return {};
  },
  methods: {
    onTestRestFulApi() {
      this.$api_test_getArticle({
        pathParams: {
          id: 1,
        },
        fn: () => {},
      });
    },
    onUpdateTabs() {
      this.$store.dispatch("update_tabs", {
        route: this.$route,
      });
    },
    init() {
      this.onUpdateTabs();
    },
  },
  mounted() {
    this.init();
  },
  watch: {
    $route(to, from) {
      this.init();
    },
  },
};
</script>

<style lang="less">
html,
body {
  height: 100%;
}

#app {
  height: 100%;
}

body {
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #333;
}

* {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

button {
  position: relative;
  z-index: 50;
}

.el-select__tags {
  z-index: 55 !important;
  /*!important指定优先*/
}

.el-pager {
  position: relative;
  z-index: 50;
}

.left-menu {
  position: relative;
  z-index: 50;
}

.el-dialog {
  position: relative;
  z-index: 3000;
}

.bounce-enter-active {
  animation: bounce-in 0.5s;
  -webkit-animation: bounce-in 0.5s;
}

.bounce-leave-active {
  animation: bounce-out 0.2s;
  -webkit-animation: bounce-out 0.2s;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
  }

  50% {
    transform: scale(1.05);
  }

  100% {
    transform: scale(1);
  }
}

@keyframes bounce-out {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(0.95);
  }

  100% {
    transform: scale(0);
  }
}

p {
  margin: 0;
}

.query {
  display: flex;
  justify-content: space-between;
  line-height: 28px;
  margin-bottom: 8px;
}

.query-left {
  display: flex;
  align-items: center;
}

.line {
  margin-right: 6px;
  width: 4px;
  height: 18px;
  background: #6cb33e;
}

.searchbar.el-form .el-form-item--mini.el-form-item {
  margin-bottom: 4px;
}

.link {
  font-size: 12px;
  color: #409eff;
  cursor: pointer;
}

.link:focus,
.link:hover {
  color: #66b1ff;
}

/* element ui 扩展样式*/
.el-table th,
.el-table th.is-leaf {
  color: #1f2d3d;
  background-color: #eef1f6;
}

.el-table.el-table--mini td,
.el-table.el-table--mini th {
  padding: 3px 0;
}

.el-pagination {
  margin-top: 4px;
}

.el-dialog__wrapper .el-dialog__header {
  padding: 10px 12px;
}

.el-dialog__wrapper .el-dialog__title {
  font-size: 14px;
  font-weight: bold;
}

.el-dialog__wrapper .el-dialog__headerbtn {
  top: 12px;
}

.el-dialog__wrapper .el-dialog__body {
  padding: 16px 10px;
}

.el-form .el-form-item__label {
  font-size: 12px;
}

.el-bread .el-breadcrumb__inner a,
.el-bread .el-breadcrumb__inner.is-link {
  color: #7a7b7e;
}

.el-bread .el-breadcrumb__item:last-child .el-breadcrumb__inner,
.el-bread .el-breadcrumb__item:last-child .el-breadcrumb__inner a,
.el-bread .el-breadcrumb__item:last-child .el-breadcrumb__inner a:hover,
.el-bread .el-breadcrumb__item:last-child .el-breadcrumb__inner:hover {
  color: #20a1ff;
}

.el-pagination.is-background {
  padding: 2px 0;
}

.home .el-pagination.is-background .btn-next,
.home .el-pagination.is-background .btn-prev,
.home .el-pagination.is-background .el-pager li {
  margin: 0 2px;
}

body .el-loading-mask {
  background: rgba(0, 0, 0, 0.4);
}

::-webkit-scrollbar {
  /*滚动条整体样式*/
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-thumb {
  /*滚动条里面小方块*/
  border-radius: 10px;
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  background: #535353;
}

::-webkit-scrollbar-track {
  /*滚动条里面轨道*/
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  background: #ededed;
}

.el-icon-question {
  color: #31546554;
  font-size: 17px;
}

.top-menu-iconfont {
  width: 1.3em;
  height: 1.3em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}

.left-menu-iconfont {
  width: 1.2em !important;
  height: 1.2em !important;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}

/*右侧弹出的dialog样式===========start===========*/
.dialogOnRightPage {
  .el-dialog__wrapper {
    transition-duration: 0.3s;
  }

  .dialog-fade-enter-active {
    animation: none !important;
  }

  .dialog-fade-leave-active {
    animation: none !important;
  }

  .dialogOnRight {
    float: right;
    height: 100%;
    margin: 0 auto 0;
  }

  .dialog-fade-enter-active .dialogOnRight,
  .dialog-fade-leave-active .dialogOnRight {
    animation-fill-mode: forwards;
  }

  .dialog-fade-enter-active .dialogOnRight {
    animation-duration: 0.3s;
    animation-name: anim-open;
    animation-timing-function: cubic-bezier(0.6, 0, 0.4, 1);
  }

  .dialog-fade-leave-active .dialogOnRight {
    animation-duration: 0.3s;
    animation-name: anim-close;
  }

  @keyframes anim-open {
    0% {
      transform: translate3d(100%, 0, 0);
      opacity: 0;
    }

    100% {
      transform: translate3d(0, 0, 0);
      opacity: 1;
    }
  }

  @keyframes anim-close {
    0% {
      transform: translate3d(0, 0, 0);
      opacity: 1;
    }

    100% {
      transform: translate3d(100%, 0, 0);
      opacity: 0;
    }
  }
}

/*右侧弹出的dialog样式===========end===========*/

/* Organization style */
.organization-popover {
  background-color: #092a47 !important;

  .title-center {
    position: absolute;
    top: 5%;
    left: 5%;

    p {
      color: rgb(230, 230, 230);
      font-weight: bold;
      margin: 5px 0;
    }

    p:nth-child(1) {
      font-size: 16px;
    }
  }
}

#organization_container {
  width: 600px;
  height: 500px;
  position: absolute;
  margin-top: 60px;
}

.organization-popover[x-placement^="bottom"] .popper__arrow::after {
  border-bottom-color: #092a47 !important;
}

aside {
  background: rgb(231, 243, 254);
  padding: 8px 24px;
  margin-bottom: 20px;
  border-radius: 2px;
  display: block;
  line-height: 18px;
  font-size: 14px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
  color: #2c3e50;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  a {
    color: #337ab7;
    cursor: pointer;
    font-size: 14px;

    &:hover {
      color: rgb(69, 169, 255);
    }
  }

  #organization_container {
    width: 700px;
    height: 500px;
    position: absolute;
    margin-top: 60px;
  }

  .organization-popover[x-placement^="bottom"] .popper__arrow::after {
    border-bottom-color: #092a47 !important;
  }
}
#nprogress .bar {
  z-index: 9999 !important;
}
</style>
