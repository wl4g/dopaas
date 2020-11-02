import { cache } from 'utils/'

export default {
    //==========判断是否拥有该权限==========
    hasPermit: function (permission) {
        if (!permission) {//type can not be null
            return false;
        }
        let menus = cache.get("deepChildRoutes");
        let menu = menus.find(n => {
            return n.permission == permission;
        });
        if (menu) {
            return true;
        } else {
            return false;
        }
    },
    getRoutePathByPermission: function (permission) {
        if (!permission) {//type can not be null
            return false;
        }
        let menus = cache.get("deepChildRoutes");
        let menu = menus.find(n => {
            return n.permission == permission;
        });
        if (menu) {
            return menu.routePath;
        }
        return null;
    },
}
