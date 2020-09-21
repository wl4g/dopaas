// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign now = aDateTime?date>
package ${packageName}.common.${moduleName}.${beanSubModulePackageName};

import java.util.Date;
import com.wl4g.components.core.bean.BaseBean;

/**
 * {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
public class ${entityName?cap_first} extends BaseBean {
	private static final long serialVersionUID = ${javaSpecs.genSerialVersionUID()}L;

<#list genTableColumns as param>
	<#if param.attrName != 'id' && param.attrName != 'createBy' && param.attrName != 'updateDate' && param.attrName != 'updateBy' && param.attrName != 'organizationCode'>
	// ${param.columnComment}
	private ${param.attrType} ${param.attrName};
	</#if>
</#list>

<#list genTableColumns as param>
	<#-- Ignore BaseBean fields-->
	<#if param.attrName != 'id' && param.attrName != 'createBy' && param.attrName != 'updateDate' && param.attrName != 'updateBy' && param.attrName != 'organizationCode'>
	public ${param.attrType} get${param.attrName?cap_first}(){
		return ${param.attrName};
	}

	public void set${param.attrName?cap_first}(${param.attrType?cap_first} ${param.attrName?uncap_first}){
		this.${param.attrName} = ${param.attrName};
	}

	public ${entityName?cap_first} with${param.attrName?cap_first}(${param.attrType?cap_first} ${param.attrName?uncap_first}){
		set${param.attrName?cap_first}(${param.attrName?uncap_first})
		return this;
	}
	</#if>

</#list>
}