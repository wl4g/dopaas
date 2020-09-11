<#include "utils/annotation.ftl" />
<#include "utils/package.ftl" />
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="web" sub_module_name="${subModuleName}" />

<#--import-->
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" sub_module_name="${subModuleName}" />.${className};
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" sub_module_name="${subModuleName}" />.${className}Service;


<@class_annotation class_name="${className}ServiceImpl" author="${functionAuthor}" date="${aDate?iso_utc}" />
@RestController
@RequestMapping("/${className?uncap_first}")
public class ${className}Controller extends BaseController {

    @Autowired
    private ${className}Service ${className?uncap_first}Service;

    @RequestMapping(value = "/list")
    public RespBase&lt;?> list(PageModel pm, String name) {
        RespBase\lObject> resp = RespBase.create();
        resp.setData(${className?uncap_first}Service.page(pm, name));
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase\l?> save(@RequestBody ${className} ${className?uncap_first}) {
        RespBase\lObject> resp = RespBase.create();
        ${className?uncap_first}Service.save(${className?uncap_first});
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase\l?> detail(Integer id) {
        RespBase\lObject> resp = RespBase.create();
        resp.setData(${className?uncap_first}Service.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase\l?> del(Integer id) {
        RespBase\lObject> resp = RespBase.create();
        ${className?uncap_first}Service.del(id);
        return resp;
    }

}
