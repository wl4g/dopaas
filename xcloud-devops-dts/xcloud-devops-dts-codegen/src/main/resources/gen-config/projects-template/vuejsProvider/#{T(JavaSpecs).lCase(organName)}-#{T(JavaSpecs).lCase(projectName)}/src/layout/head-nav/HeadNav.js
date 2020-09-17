import {
    store as utilstore
} from '../../utils/'

import store from '../../store'
import ThemePicker from '../../components/theme-picker'
import OrganizationPanel from '../../components/organization-panel'

export default {
    name: 'head-nav',
    components: {
        'organization-panel': OrganizationPanel,

        'theme-picker': ThemePicker
    },
    data() {
        return {
            lang: 'zh_CN',
            dialog: {
                show_access: false,
                show_set: false,
                show_pass: false,
                title: '修改密码',
                user_info: this.$store.state.user.userinfo,
                set_info: {
                    login_style: '',
                    disabled_update_pass: [],
                    select_users: []
                },
                user_info_rules: {
                    old_password: [{
                        required: true,
                        message: '旧密码不能为空！',
                        trigger: 'blur'
                    }],
                    password: [{
                        required: true,
                        message: '新密码不能为空！',
                        trigger: 'blur'
                    }, {
                        trigger: 'blur',
                        validator: (rule, value, callback) => {
                            if (value === '') {
                                callback(new Error('请再次输入密码'))
                            } else {
                                if (this.dialog.user_info.password !== '') {
                                    this.$refs.user_info.validateField('password_confirm')
                                }
                                callback()
                            }
                        }
                    }],
                    password_confirm: [{
                        required: true,
                        message: '确认密码不能为空！',
                        trigger: 'blur'
                    }, {
                        trigger: 'blur',
                        validator: (rule, value, callback) => {
                            if (value === '') {
                                callback(new Error('请再次输入密码'))
                            } else if (value !== this.dialog.user_info.password) {
                                callback(new Error('两次输入密码不一致!'))
                            } else {
                                callback()
                            }
                        }
                    }]
                },
            },
            routList: []
        }
    },
    mounted() {
        // this.setDialogInfo('access');
        this.routList = utilstore.get('routList');
        // this.onGetSetting();
    },
    methods: {
        getMenuName(item) {
            return this.$i18n.locale == 'en_US' ? item.name : item.displayName;
        },
        changeLang(lang) {
            if (lang) {
                this.$i18n.locale = lang;
            } else {
                this.$i18n.locale = this.lang;
            }
            sessionStorage.setItem("authzPrincipalLangAttributeName", this.$i18n.locale);
            this.$$api_iam_applylocale({
                data: {
                    lang: this.$i18n.locale
                },
                fn: data => {

                },
            })
        },
        getUsername() {
            //TODO bug
            //return "there has bug";
            return utilstore.get('userinfo.username')
        },
        logout() {
            this.$confirm('确认安全退出登录?', '确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$store.dispatch('remove_userinfo').then(() => {
                    this.$$api_iam_logout({
                        data: {},
                        fn: data => {
                            utilstore.remove('userinfo.username');
                            //clean router
                            //utilstore.remove("routList");
                            //store.commit('update_routList', { routList: [] })

                            //window.location.href = IAM.Core.getIamBaseUri() + "/logout";
                            this.$router.push('/login');
                            
                            location.reload();
                        },
                    });
                });
            }).catch(() => {
                /*this.$message({
                  type: 'info',
                  message: '已取消退出'
                });*/
            });
        },

        /**
         * 弹出框-修改密码或者系统设置
         * @param {string} cmditem 弹框类型
         */
        setDialogInfo(cmditem) {
            if (!cmditem) {
                console.log('test')
                this.$message('菜单选项缺少command属性')
                return
            }
            switch (cmditem) {
                case 'info':
                    this.$router.push({
                        path: '/demo/user/edit',
                        query: {
                            id: this.$store.state.user.userinfo.id
                        }
                    })
                    break
                case 'pass':
                    this.dialog.show_pass = true
                    this.dialog.title = '修改密码'
                    break
                case 'set':
                    this.onGetSetting()
                    this.dialog.show_set = true
                    this.dialog.title = '系统设置'
                    break
                case 'logout':
                    this.logout()
                    break
            }
        },

        /**
         * 修改密码
         * @param  {object} userinfo 当前修改密码的表单信息
         */
        updUserPass(userinfo) {
            this.$refs[userinfo].validate((valid) => {
                if (valid) {
                    this.$$api_user_updatePass({
                        data: {
                            old_password: this.dialog[userinfo].old_password,
                            password: this.dialog[userinfo].password,
                            password_confirm: this.dialog[userinfo].password_confirm
                        },
                        fn: data => {
                            this.dialog.show_pass = false
                            this.$message.success('修改成功！')
                        }
                    })
                }
            })
        },
        /**
         * 获取系统设置信息
         */
        onGetSetting() {
            // 获取系统设置信息
            if (this.$store.state.user.userinfo.pid === 0) {
                this.$$api_system_getSetting({
                    fn: data => {
                        if (data.setting_info.disabled_update_pass) {
                            data.setting_info.disabled_update_pass = data.setting_info.disabled_update_pass.split(',')
                        } else {
                            data.setting_info.disabled_update_pass = []
                        }
                        data.setting_info.login_style = data.setting_info.login_style + ''
                        this.dialog.set_info = data.setting_info
                    }
                })
            } else {
                this.$message.error('只有管理员才能操作！')
            }
        },
        /**
         * 修改系统设置信息
         */
        onUpdateSetting() {
            // console.log(this.dialog.set_info.login_style);
            // console.log(this.dialog.set_info.disabled_update_pass);
            // console.log(this.dialog.set_info.id);

            this.$$api_system_updateSetting({
                data: {
                    id: this.dialog.set_info.id,
                    login_style: this.dialog.set_info.login_style,
                    disabled_update_pass: this.dialog.set_info.disabled_update_pass && this.dialog.set_info.disabled_update_pass.length ? this.dialog.set_info.disabled_update_pass.join(',') : ''
                },
                fn: data => {
                    this.dialog.show_set = false
                }
            })
        },

        // 切换侧边栏菜单汇总页显示状态
        toggleSidebarLightbox(){
            this.$root.$emit('lightBoxVisibleChange')
        }
    }
}
