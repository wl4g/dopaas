import it from "element-ui/src/locale/lang/it";
import {
  store as utilstore,
  store
} from '../../utils/'
import SidebarItem from './SidebarItem'


export default {
  name: 'left-menu',
  components: { SidebarItem },
  data() {
    return {
      permission_routes: [],
      win_size: {
        height: ''
      },
      routList: [],
      lightBoxVisible: false,
      maskVisible: false,
      routerGroupByClassify: {},
      keyword: '',
      isKeyWordFocus: false
    }
  },
  methods: {

    getMenuName(item) {
      return this.$i18n.locale == 'en_US' ? item.name : item.displayName;
    },

    setSize() {
      //this.win_size.height = (this.$$lib_$(window).height()) + 'px'
      this.win_size.height = '100%'
    },
    toggleMenu() {
      this.$store.dispatch(this.$store.state.leftmenu.menu_flag ? 'set_menu_close' : 'set_menu_open')
    },
    updateCurMenu(route) {
      route = route || this.$route
      if (route.matched.length) {
        var rootPath = route.matched[0].path
        var fullPath = route.path
        this.$store.dispatch('set_cur_route', {
          rootPath,
          fullPath
        });

        var routes = store.get('routList')
        if (routes) {
          for (var i = 0; i < routes.length; i++) {
            if (routes[i].path === rootPath && !routes[i].hidden) {
              this.permission_routes = routes[i].children
              break
            }
          }
        }

        // 跳转第一个有效路由
        // if(this.menu_list.length) {
        //   let target = this.menu_list[0];
        //   while (target.children){
        //     target = target.children
        //   }
        //   this.$router.push({ path: target.path});
        // }
      } else {
        //this.$router.push('/404')
      }
    },
    routerGo(route, item) {
      if (this.isExternal(route)) {
        window.location.href = route;
      } else {
        if (item.renderTarget && item.renderTarget == '_blank') {
          this.$router.push({ path: '/common/middleware', query: { url: item.meta.linkhref } })
        } else {
          let base = this.$route.matched[0].path;
          //this.$router.push({path: base + '/' + route})
          this.$router.push({ path: route })
        }
      }
    },

    isExternal(path) {
      return /^(https?:|mailto:|tel:)/.test(path)
    },

    beforeOpenMaskLayer() {
      this.timer = setTimeout(() => {
        this.openMaskLayer();
      }, 350)
    },
    openMaskLayer() {
      document.documentElement.addEventListener("click", this.handleDocElmClick)
      this.maskVisible = true;
      this.$nextTick(function(){
        this.$refs.maskSearchInput.$refs['input'].focus()
      })
    },
    resetMaskLayer() {
      this.timer && clearTimeout(this.timer);
    },

    closeMaskLayer() {
      this.maskVisible = false;
      document.documentElement.removeEventListener("click", this.handleDocElmClick)
    },
    toggleMaskLayer() {
      this.resetMaskLayer()

      if (this.maskVisible) {
        this.closeMaskLayer();
      } else {
        this.openMaskLayer();
      }
    },

    setRouterGroup() {
      var self = this;
      var keyWords = this.keyword.trim().toUpperCase();
      var routerList = utilstore.get('allRouter');
      var res = {};

      routerList.forEach(function (item) {
        var id = self.dictutil.getDictLabelByTypeAndValue("menu_classify_type", item.classify, null);
        if (id) {
          res[id] = res[id] || [];
          if (self.$i18n.locale === 'zh_CN' && item.displayName.toUpperCase().includes(keyWords) && item.routePath) {
            res[id].push(item);
          }
          if (self.$i18n.locale === 'en_US' && item.name.toUpperCase().includes(keyWords) && item.routePath) {
            res[id].push(item);
          }
        }
      })

      // 删除空的数组
      var keys = Object.keys(res);
      for (var i = 0, length = keys.length; i < length; i++) {
        if (!res[keys[i]].length) {
          try {
            delete res[keys[i]]
          } catch (e) {
            console.log(e)
          }
        }
      }

      this.routerGroupByClassify = res;
    },

    // 遮罩层菜单子项点击事件
    handleRouteLinkClick(path) {
      this.$router.push({ path: path })
      this.closeMaskLayer();
      this.lightBoxVisible = false;
    },

    handleKeyWordSearch(e) {
      this.setRouterGroup();
    },

    handleDocElmClick(e) {
      if (document.querySelector(".sidebar-lightbox-header").contains(e.target)) {
        return
      }
      if (document.querySelector(".menu-list-mask").contains(e.target)) {
        return
      }

      this.closeMaskLayer();
    },

    // lightbox的一级菜单点击事件
    parentLevelMenuClick() {
      this.lightBoxVisible = false;
      this.closeMaskLayer();
    }


  },
  created() {
    this.setSize()
    this.$$lib_$(window).resize(() => {
      this.setSize()
    })
    this.updateCurMenu();
    this.setRouterGroup();

    let self = this;
    this.$root.$on('lightBoxVisibleChange', function () {
      if (self.$store.state.leftmenu.width === '1px') {
        return
      }
      self.lightBoxVisible = !self.lightBoxVisible;
    })
  },
  mounted() {
    this.routList = utilstore.get('routList');
  },
  watch: {
    $route(to, from) {
      this.updateCurMenu(to)
    }
  }
}
