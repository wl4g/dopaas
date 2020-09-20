// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#macro class_annotation class_name author date>
/**
 * ${class_name}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
</#macro>
<#--{包名}/{模块名}/{分层(dao,entity,service,web)}/{子模块名}-->
<#macro class_package package_name module_name demixing_package>${package_name}.${module_name}.${demixing_package}</#macro>
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" />;

<#--import-->
import com.wl4g.components.data.page.PageModel;
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" />.${entityName};

<@class_annotation class_name="${entityName}Service" author="${functionAuthor}" date="${aDate?iso_utc}" />
public interface ${entityName}Service {

    PageModel page(PageModel pm, String name);

    void save(${entityName} ${entityName?uncap_first});

    ${entityName} detail(Integer id);

    void del(Integer id);
}

