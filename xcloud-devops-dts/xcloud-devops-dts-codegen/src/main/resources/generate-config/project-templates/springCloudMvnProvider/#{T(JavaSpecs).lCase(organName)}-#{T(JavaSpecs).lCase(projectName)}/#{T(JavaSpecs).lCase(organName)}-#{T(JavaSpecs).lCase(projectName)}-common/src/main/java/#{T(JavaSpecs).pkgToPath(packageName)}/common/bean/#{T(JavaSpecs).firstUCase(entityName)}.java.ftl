// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign now = aDateTime?date>
package ${packageName}.common.bean.${moduleName};

import java.util.Date;

/**
 * {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
public class ${entityName?cap_first} {

<#list genTableColumns as param>
	// ${param.columnComment}
	private ${param.attrType} ${param.attrName};

</#list>
<#list genTableColumns as param>
	public void set${param.attrName?cap_first}(${param.attrType} ${param.attrName}){
		this.${param.attrName} = ${param.attrName};
	}

	public ${param.attrType} get${param.attrName?cap_first}(){
		return this.${param.attrName};
	}

</#list>
}