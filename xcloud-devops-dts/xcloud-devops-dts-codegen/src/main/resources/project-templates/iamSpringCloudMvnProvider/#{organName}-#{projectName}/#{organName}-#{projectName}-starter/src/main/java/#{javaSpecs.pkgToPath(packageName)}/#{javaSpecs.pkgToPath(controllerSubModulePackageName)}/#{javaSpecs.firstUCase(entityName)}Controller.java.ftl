// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
package ${packageName}.${controllerSubModulePackageName};

<#--import-->
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.*;

<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
<#elseif javaSpecs.isConf(extraOptions, "gen.swagger.ui", "officialOas")>
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
</#if>

import ${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first};
import ${packageName}.${serviceSubModulePackageName}.${entityName?cap_first}Service;

/**
* {@link ${entityName?cap_first}}
*
* @author ${author}
* @version ${version}
* @Date ${now}
* @since ${since}
*/
<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
@Api(tags = { "${moduleName}/${functionSimpleName}" }, description = "${comments}", value = "${functionName}")
</#if>
@RestController
@RequestMapping("/${entityName?lower_case}")
public class ${entityName}Controller extends BaseController {

    @Autowired
    private ${entityName?cap_first}Service ${entityName?uncap_first}Service;

    <#if javaSpecs.isConfOr(extraOptions, "gen.swagger.ui", "bootstrapSwagger2", "officialOas")>
    @ApiOperation(value = "查询${functionSimpleName}信息（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码，取值范围：1 <= pageNum", dataType="int32", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "单页数据记录，当pageSize=0时返回所有记录", dataType="int32", defaultValue = "10"), })
    </#if>
    @RequestMapping(value = "/list", method = { GET })
    public RespBase${r"<"}?> list(PageModel pm, ${entityName?cap_first} ${entityName?uncap_first}) {
        RespBase${r"<"}Object> resp = RespBase.create();
        resp.setData(${entityName?uncap_first}Service.page(pm, ${entityName?uncap_first}));
        return resp;
    }

    <#if javaSpecs.isConfOr(extraOptions, "gen.swagger.ui", "bootstrapSwagger2", "officialOas")>
    @ApiOperation(value = "新增${functionSimpleName}信息")
    </#if>
    @RequestMapping(value = "/save", method = { POST, PUT })
    public RespBase${r"<"}?> save(@RequestBody ${entityName?cap_first} ${entityName?uncap_first}) {
        RespBase${r"<"}Object> resp = RespBase.create();
        ${entityName?uncap_first}Service.save(${entityName?uncap_first});
        return resp;
    }

    <#if javaSpecs.isConfOr(extraOptions, "gen.swagger.ui", "bootstrapSwagger2", "officialOas")>
    @ApiOperation(value = "查询${functionSimpleName}详细信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "${functionSimpleName}信息ID", dataType="int64", required = true) })
    </#if>
    @RequestMapping(value = "/detail", method = { GET })
    public RespBase${r"<"}?> detail(Long id) {
        RespBase${r"<"}Object> resp = RespBase.create();
        resp.setData(${entityName?uncap_first}Service.detail(id));
        return resp;
    }

    <#if javaSpecs.isConfOr(extraOptions, "gen.swagger.ui", "bootstrapSwagger2", "officialOas")>
    @ApiOperation(value = "删除${functionSimpleName}信息")
    @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "待删除的${functionSimpleName}信息ID", dataType="int64", required = true) })
    </#if>
    @RequestMapping(value = "/del", method = { POST, DELETE })
    public RespBase${r"<"}?> del(Long id) {
        RespBase${r"<"}Object> resp = RespBase.create();
        ${entityName?uncap_first}Service.del(id);
        return resp;
    }

}
