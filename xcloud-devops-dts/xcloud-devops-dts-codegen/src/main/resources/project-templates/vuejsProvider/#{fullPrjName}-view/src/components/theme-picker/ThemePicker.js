import { store } from "../../utils";

const version = require('element-ui/package.json').version // element-ui version from node_modules
const ORIGINAL_THEME = '#409EFF' // default color

export default {
    name: 'theme-picker',
    data() {
        return {
            chalk: '', // content of theme-chalk css
            chalkCustom: '',
            theme: ''
        }
    },
    computed: {
        defaultTheme() {
            //this.$store.state.settings.theme
            return '#fff'
        }
    },
    watch: {
        defaultTheme: {
            handler: function (val, oldVal) {
                this.theme = val
            },
            immediate: true
        }
    },
    mounted() {
        // localStorage才是永久存储，除非手动清理
        // refer: https://developer.mozilla.org/zh-CN/docs/Web/API/Window/localStorage

        // Get from cache first
        let themeType = store.get("global_theme");
        if (!themeType) {
            // Sets by default rule
            if (new Date().getHours() >= 19 || new Date().getHours() < 6) {
                store.set("global_theme", (themeType = 'dark'));
            } else {
                store.set("global_theme", (themeType = 'light'));
            }
        }
        if (themeType == 'dark') {
            this.theme = '#191c23';
            this.setTheme(this.theme);
        } else {
            this.theme = '#fff';
            this.reset();
        }
    },
    methods: {
        changeTheme(themeType) {
            if (themeType == 'light') {
                this.theme = '#fff';
                this.reset();
            } else { // dark
                this.theme = '#191c23';
                this.setTheme(this.theme);
            }
            // Save theme.
            store.set("global_theme", themeType);
        },
        // updateStyle(style, oldCluster, newCluster) {
        //     debugger
        //     let newStyle = style
        //     oldCluster.forEach((color, index) => {
        //         newStyle = newStyle.replace(new RegExp(color, 'ig'), newCluster[index])
        //     })
        //     return newStyle
        // },
        getCSSString(url, variable) {
            return new Promise(resolve => {
                const xhr = new XMLHttpRequest()
                xhr.onreadystatechange = () => {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        this[variable] = xhr.responseText.replace(/@font-face{[^}]+}/, '')
                        resolve()
                    }
                }
                xhr.open('GET', url)
                xhr.send()
            })
        },
        // getThemeCluster(theme) {
        //     const tintColor = (color, tint) => {
        //         let red = parseInt(color.slice(0, 2), 16)
        //         let green = parseInt(color.slice(2, 4), 16)
        //         let blue = parseInt(color.slice(4, 6), 16)
        //         if (tint === 0) { // when primary color is in its rgb space
        //             return [red, green, blue].join(',')
        //         } else {
        //             red += Math.round(tint * (255 - red))
        //             green += Math.round(tint * (255 - green))
        //             blue += Math.round(tint * (255 - blue))
        //             red = red.toString(16)
        //             green = green.toString(16)
        //             blue = blue.toString(16)
        //             return `#${red}${green}${blue}`
        //         }
        //     }
        //     const shadeColor = (color, shade) => {
        //         let red = parseInt(color.slice(0, 2), 16)
        //         let green = parseInt(color.slice(2, 4), 16)
        //         let blue = parseInt(color.slice(4, 6), 16)
        //         red = Math.round((1 - shade) * red)
        //         green = Math.round((1 - shade) * green)
        //         blue = Math.round((1 - shade) * blue)
        //         red = red.toString(16)
        //         green = green.toString(16)
        //         blue = blue.toString(16)
        //         return `#${red}${green}${blue}`
        //     }
        //     const clusters = [theme]
        //     for (let i = 0; i <= 9; i++) {
        //         clusters.push(tintColor(theme, Number((i / 10).toFixed(2))))
        //     }
        //     clusters.push(shadeColor(theme, 0.1))
        //     return clusters
        // },
        async setTheme(val) {
            const $message = this.$message({
                message: '  Compiling the theme',
                customClass: 'theme-message',
                type: 'success',
                duration: 0,
                iconClass: 'el-icon-loading'
            });
            const setCustomStyle = () => {
                let styleTag = document.getElementById('custom-style');
                if (!styleTag) {
                    styleTag = document.createElement('style')
                    styleTag.setAttribute('id', 'custom-style')
                    document.head.appendChild(styleTag)
                }
                styleTag.innerText = this.chalkCustom
            };
            if (!this.chalkCustom) {
                const url = `/static/theme/dark.css`;
                await this.getCSSString(url, 'chalkCustom')
            }
            setCustomStyle();
            $message.close()
        },
        reset() {
            let styleTag = document.getElementById('custom-style');
            if (styleTag) {
                styleTag.parentElement.removeChild(styleTag);
            }
        }
    }
}
