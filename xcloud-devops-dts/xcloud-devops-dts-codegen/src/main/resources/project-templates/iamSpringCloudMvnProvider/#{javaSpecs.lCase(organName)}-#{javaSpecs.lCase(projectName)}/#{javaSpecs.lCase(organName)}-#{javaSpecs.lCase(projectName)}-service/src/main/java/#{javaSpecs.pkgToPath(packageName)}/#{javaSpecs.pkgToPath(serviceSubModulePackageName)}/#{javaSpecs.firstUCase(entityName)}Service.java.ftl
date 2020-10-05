// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package ${packageName}.${serviceSubModulePackageName};

<#--import-->
import com.wl4g.components.data.page.PageModel;
import ${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName}.${entityName?cap_first};

/**
 * ${comments} service of {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
public interface ${entityName}Service {

    /**
     * ${comments} page query.
     *
     * @param pm
     * @param ${entityName?uncap_first}
     * @return 
     */
    PageModel page(PageModel pm, ${entityName} ${entityName?uncap_first});

    /**
     * ${comments} save.
     *
     * @param ${entityName?uncap_first}
     * @return 
     */
    int save(${entityName} ${entityName?uncap_first});

    /**
     * ${comments} detail query.
     *
     * @param id
     * @return 
     */
    ${entityName} detail(Long id);

    /**
     * ${comments} delete.
     *
     * @param id
     * @return 
     */
    int del(Long id);

}

