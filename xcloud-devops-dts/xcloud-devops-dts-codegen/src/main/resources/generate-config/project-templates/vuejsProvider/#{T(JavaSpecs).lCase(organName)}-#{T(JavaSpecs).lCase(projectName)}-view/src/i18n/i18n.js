import Vue from 'vue'
import locale from 'element-ui/lib/locale'
import VueI18n from 'vue-i18n'
import messages from './langs'
Vue.use(VueI18n)

// 通过this.$i18n.locale = "en"修改语言
function getlang() {
  let lang = sessionStorage.getItem("authzPrincipalLangAttributeName");
  if(lang){
    if(lang == 'zh_CN'){
      lang = 'zh_CN'
    }
  }else if(navigator.language){
    lang = navigator.language.toUpperCase();
    if(lang == 'ZH-CN' || lang == 'ZH_CN' || lang == 'CN' || lang == 'ZH'){
      lang = 'zh_CN'
    }
  }else{
    lang = 'zh_CN'
  }
  return lang;
}


const i18n = new VueI18n({
  //locale: localStorage.getItem("language") || 'cn', //初始未选择默认 cn 中文
  //locale: sessionStorage.getItem("authzPrincipalLangAttributeName") || navigator.language ,
  locale: getlang(),
  messages
})
locale.i18n((key, value) => i18n.t(key, value))

export default i18n
