<#include "utils/annotation.ftl" />
<#include "utils/package.ftl" />
<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="service" sub_module_name="${subModuleName}" />

<#--import-->
import com.wl4g.components.data.page.PageModel;
import <@class_package package_name="${packageName}" module_name="${moduleName}" demixing_package="bean" sub_module_name="${subModuleName}" />.${className};

<@class_annotation class_name="${className}Service" author="${functionAuthor}" date="${aDate?iso_utc}" />
public interface ${className}Service {

    PageModel page(PageModel pm, String name);

    void save(${className} ${className?uncap_first});

    ${className} detail(Integer id);

    void del(Integer id);
}

