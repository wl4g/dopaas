// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>

package ${packageName}.controller.${moduleName};

<#--import-->
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${packageName}.common.bean.${moduleName}.${entityName?cap_first};
import ${packageName}.service.${moduleName}.${entityName?cap_first}Service;

/**
* {@link ${entityName?cap_first}}
*
* @author ${author}
* @version ${version}
* @Date ${now}
* @since ${since}
*/
@RestController
@RequestMapping("/${entityName?uncap_first}")
public class ${entityName}Controller extends BaseController {

    @Autowired
    private ${entityName}Service ${entityName?uncap_first}Service;

    @RequestMapping(value = "/list")
    public RespBase${r"<"}?> list(PageModel pm, ${entityName} ${entityName?uncap_first}) {
        RespBase${r"<"}Object> resp = RespBase.create();
        resp.setData(${entityName?uncap_first}Service.page(pm, ${entityName?uncap_first}));
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
