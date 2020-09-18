// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#macro class_annotation class_name author date>
/**
 * ${class_name}
 *
 * @author ${author}
 * @Date ${date}
 */
</#macro>
<#macro class_package package_name module_name demixing_package>${package_name}.${module_name}.${demixing_package}</#macro>

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>

<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="web" />;

<#--import-->
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" />.${entityName};
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" />.${entityName}Service;


<@class_annotation class_name="${entityName}ServiceImpl" author="${functionAuthor}" date="${aDate?iso_utc}" />
@RestController
@RequestMapping("/${entityName?uncap_first}")
public class ${entityName}Controller extends BaseController {

    @Autowired
    private ${entityName}Service ${entityName?uncap_first}Service;

    @RequestMapping(value = "/list")
    public RespBase${r"<"}?> list(PageModel pm, String name) {
        RespBase${r"<"}Object> resp = RespBase.create();
        resp.setData(${entityName?uncap_first}Service.page(pm, name));
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase${r"<"}?> save(@RequestBody ${entityName} ${entityName?uncap_first}) {
        RespBase${r"<"}Object> resp = RespBase.create();
        ${entityName?uncap_first}Service.save(${entityName?uncap_first});
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase${r"<"}?> detail(Integer id) {
        RespBase${r"<"}Object> resp = RespBase.create();
        resp.setData(${entityName?uncap_first}Service.detail(id));
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase${r"<"}?> del(Integer id) {
        RespBase${r"<"}Object> resp = RespBase.create();
        ${entityName?uncap_first}Service.del(id);
        return resp;
    }

}
