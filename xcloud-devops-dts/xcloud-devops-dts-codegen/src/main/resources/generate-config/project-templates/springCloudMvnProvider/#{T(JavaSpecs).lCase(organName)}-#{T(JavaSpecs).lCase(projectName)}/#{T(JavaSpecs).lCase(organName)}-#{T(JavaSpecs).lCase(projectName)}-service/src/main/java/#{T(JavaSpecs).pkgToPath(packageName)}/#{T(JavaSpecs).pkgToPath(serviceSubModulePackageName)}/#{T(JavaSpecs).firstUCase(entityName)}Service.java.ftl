// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package ${packageName}.${serviceSubModulePackageName};

<#--import-->
import com.wl4g.components.data.page.PageModel;
import ${packageName}.common.${beanSubModulePackageName}.${entityName?cap_first};

/**
* {@link ${entityName?cap_first}}
*
* @author ${author}
* @version ${version}
* @Date ${now}
* @since ${since}
*/
public interface ${entityName}Service {

    PageModel page(PageModel pm, ${entityName} ${entityName?uncap_first});

    void save(${entityName} ${entityName?uncap_first});

    ${entityName} detail(Integer id);

    void del(Integer id);
}

