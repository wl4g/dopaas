export default {
    getCurrentLang(){
        return sessionStorage.getItem("authzPrincipalLangAttributeName");
    },
    getPageTitle: function (target) {
        let lang = sessionStorage.getItem("authzPrincipalLangAttributeName");
        let pageTitle = target.name;
        if (!lang || lang.startsWith('zh_CN')) {
            pageTitle = target.displayName;
        }
        if (pageTitle) {
            return pageTitle + ' - PaaS'
        } else {
            return 'PaaS'
        }
    },
    getDisplayByLang(lang) {
        switch (lang) {
            case 'zh_CN': return '简体中文';
            case 'en_US': return 'US English';
            default: return '简体中文';
        }
    }
}
