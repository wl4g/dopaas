<template>
  <div class="home">
    <head-nav></head-nav>
    <div class="left-fixed-right-auto">
      <left-menu></left-menu>
      <div
        class="right-content"
        :style="{ marginLeft: $store.state.leftmenu.width }"
      >
        <div class="content">
          <!-- <tabs></tabs> -->
          <bread></bread>
          <keep-alive>
            <router-view></router-view>
          </keep-alive>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import HeadNav from "../head-nav/HeadNav.vue";
import LeftMenu from "../left-menu/LeftMenu.vue";
import Bread from "../bread/Bread.vue";

export default {
  name: "layout",
  components: {
    HeadNav,
    LeftMenu,
    Bread,
  },
};
</script>

<style lang="less" scoped>
.content {
  /*background: #f1f2f7;*/
  background: #fff;
  padding: 8px 16px;
}

.right-content {
  padding-top: 50px;
  // margin-bottom: 60px;
}
</style>
