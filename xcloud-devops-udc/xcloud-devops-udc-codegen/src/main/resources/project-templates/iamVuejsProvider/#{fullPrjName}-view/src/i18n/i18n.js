import Vue from 'vue'
import locale from 'element-ui/lib/locale'
import VueI18n from 'vue-i18n'
import messages from './langs'
Vue.use(VueI18n)

// Usage: this.$i18n.locale = "en_US" // Update
function getlang() {
    let lang = sessionStorage.getItem("authzPrincipalLangAttributeName");
    if (!lang && navigator.language) {
        lang = navigator.language.toUpperCase();
        if (lang == 'ZH-CN' || lang == 'ZH_CN' || lang == 'CN' || lang == 'ZH') {
            lang = 'zh_CN'
        }
    } else {
        lang = 'zh_CN'
    }
    return lang;
}

const i18n = new VueI18n({
    locale: getlang(),
    messages
})

locale.i18n((key, value) => i18n.t(key, value))

export default i18n
