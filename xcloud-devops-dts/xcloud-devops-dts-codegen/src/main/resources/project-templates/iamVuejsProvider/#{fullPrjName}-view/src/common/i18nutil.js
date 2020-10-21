export default {
    getPageTitle: function (target) {
        let lang = sessionStorage.getItem("authzPrincipalLangAttributeName");
        let pageTitle = target.name;
        if (!lang || lang.startsWith('zh_CN')) {
            pageTitle = target.displayName;
        }
        if (pageTitle) {
            return pageTitle + ' - XCloud DevOps'
        } else {
            return 'XCloud DevOps'
        }
    },

    getDisplayByLang(lang){
        switch (lang) {
            case 'zh_CN': return '中文';
            case 'en_US': return 'English';
            default: return '中文';
        }
    }
}
