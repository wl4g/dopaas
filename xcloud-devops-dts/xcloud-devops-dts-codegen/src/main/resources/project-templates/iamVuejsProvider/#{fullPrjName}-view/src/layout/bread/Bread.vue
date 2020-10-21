<template>
  <div class='bread'>
    <!-- <strong>
      {{strong}}
    </strong> -->
    <el-breadcrumb separator="/" class='el-bread'>
      <!--<el-breadcrumb-item :to="{ path: '/' }">{{$t('message.common.home')}}</el-breadcrumb-item>-->
      <!--<el-breadcrumb-item :to="{ path: item.path }"
        v-for='(item,index) in $route.matched'
        :key='index'>{{item.name}}
      </el-breadcrumb-item>-->
      <el-breadcrumb-item :to="{ path: item.path }"
        v-for='(item,index) in dealPath($route.path)'
        :key='index'>{{item.name}}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script>
  import {
    cache
  } from '../../utils/'

  export default {
    name: 'bread',
    data () {
      return {
        strong: ''
      }
    },
    methods: {
      getPageText (name) {
        return name.replace('编辑', this.$route.query.id ? '修改' : '添加')
      },

      getMenuName(routePath){
        var routerList = cache.get('deepChildRoutes');
        let route = routerList.find(n => {
          return n.routePath === routePath;
        });
        if(route){
          let lang = this.$i18n.locale;
          if(lang == 'en_US'){
            return route.name;
          }else{
            return route.displayName;
          }
        }else{
          return;
        }
      },

      dealPath(route){
        let result = [];
        let routes = route.split("/");
        for(let i = 0; i<routes.length; i++){
          if(!routes[i]){
            continue;
          }
          let r = '';
          for(let j = 0; j<= i; j++){
            if(routes[j]){
              r = r + '/' + routes[j]
            }
          }
          let menuName = this.getMenuName(r);
          result.push({
            path: r,
            name: menuName ? menuName : i,
          });
        }
        return result;
      }


    },
    mounted () {

    },
    created () {
      if (this.$route.matched.length) {
        var name = this.$route.matched[this.$route.matched.length - 1].name
        this.strong = this.getPageText(name)
      }
    },
    watch: {
      $route (to, from) {
        this.strong = this.getPageText(to.name)
      }
    }
  }
</script>

<style scoped lang='less'>
  .bread {
    margin-bottom: 4px;
    height: 26px;
    line-height: 26px;
    .el-bread {
      display: inline-block;
      float: right;
      text-align: right;
      line-height: 26px;
    }
  }
</style>
