// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>
<#--package name-->
package ${packageName}.${serviceSubModPkgName};

<#--import-->
import com.wl4g.components.data.page.PageModel;
import ${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModPkgName}.${entityName?cap_first};

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
    PageModel${r"<"}${entityName}> page(PageModel${r"<"}${entityName}> pm, ${entityName} ${entityName?uncap_first});

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
    ${entityName} detail(${javaSpecs.toSimpleJavaType(pk.attrType)} ${pk.attrName});

    /**
     * ${comments} delete.
     *
     * @param id
     * @return 
     */
    int del(${javaSpecs.toSimpleJavaType(pk.attrType)} ${pk.attrName});

}

