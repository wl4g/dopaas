import it from "element-ui/src/locale/lang/it";
import {
    cache
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
            menusOfClassify: {},
            keyword: '',
            isKeyWordFocus: false,
            isLastVisible: false,
            isLastVisibleUpdateTime: 0,
        }
    },
    methods: {
        getMenuName(item) {
            return this.$i18n.locale == 'en_US' ? item.nameEn : item.nameZh;
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

                var routes = cache.get('rootDeepChildRoutes')
                if (routes) {
                    for (var i = 0; i < routes.length; i++) {
                        if (routes[i].path === rootPath && !routes[i].hidden) {
                            this.permission_routes = routes[i].children
                            break
                        }
                    }
                }
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
            this.$nextTick(function () {
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
            var routerList = cache.get('deepChildRoutes');
            var result = {};

            routerList.forEach(function (item) {
                if (!item.classify) { // Ignore no classify menu
                    return;
                }
                let classifyDict = self.dictutil.getDictByTypeAndValue("menu_classify_type", item.classify);
                if (classifyDict && item.routePath) {
                    let classifyData = result[classifyDict.value] || (result[classifyDict.value] = {});
                    classifyData['items'] = classifyData['items'] || [];
                    // 使用if-elseif是为了区分只搜索当前语言下的关键字
                    if (self.$i18n.locale === 'zh_CN' && item.nameZh.toUpperCase().includes(keyWords)) {
                        classifyData['items'].push(item);
                    } else if (self.$i18n.locale === 'en_US' && item.nameEn.toUpperCase().includes(keyWords)) {
                        classifyData['items'].push(item);
                    }
                    classifyData['classifyNameZh'] = classifyDict.label; // TODO rename to labelZh
                    classifyData['classifyNameEn'] = classifyDict.labelEn;
                }
            });

            // 清除无菜单items的分类
            var keys = Object.keys(result);
            for (var i = 0; i < keys.length; i++) {
                if (!result[keys[i]]['items'].length) {
                    try {
                        delete result[keys[i]]
                    } catch (e) {
                        console.error(e);
                    }
                }
            }

            console.debug(result);
            this.menusOfClassify = result;
        },

        getClassifyName(classifyData) {
            return this.$i18n.locale == 'en_US' ? classifyData.classifyNameEn : classifyData.classifyNameZh;
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
            self.lightBoxVisible = !self.lightBoxVisible || this.isLastVisible;
            this.isLastVisible = self.lightBoxVisible;
            this.isLastVisibleUpdateTime = new Date().getTime();
        });

        this.$root.$on('clickLightBoxVisibleChange', function () {
            if (self.$store.state.leftmenu.width === '1px') {
                return
            }
            // 当鼠标移动触发显示指令时，由于需要1s才能完成，若此时点击会触发隐藏指令会出现闪隐，就需要延迟500ms来防止
            let now = new Date().getTime();
            if (now - this.isLastVisibleUpdateTime >= 600) {
                self.lightBoxVisible = !self.lightBoxVisible;
                this.isLastVisibleUpdateTime = now;
            }
        });
    },
    mounted() {
        this.routList = cache.get('rootDeepChildRoutes');
    },
    watch: {
        $route(to, from) {
            this.updateCurMenu(to)
        }
    }
}
