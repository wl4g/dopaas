// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign now = aDateTime?date>
package ${packageName}.common.bean.${moduleName};

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
	private static final long serialVersionUID = 6815608076300843748L;

<#list genTableColumns as param>
	<#if param.attrName != 'id' && param.attrName != 'createBy' && param.attrName != 'updateDate' && param.attrName != 'updateBy' && param.attrName != 'organizationCode'>
		// ${param.columnComment}
		private ${param.attrType} ${param.attrName};
	</#if>
</#list>
<#list genTableColumns as param>
	<#--ignore baseBean fields-->
	<#if param.attrName != 'id' && param.attrName != 'createBy' && param.attrName != 'updateDate' && param.attrName != 'updateBy' && param.attrName != 'organizationCode'>
		public void set${param.attrName?cap_first}(${param.attrType} ${param.attrName}){
		this.${param.attrName} = ${param.attrName};
		}

		public ${param.attrType} get${param.attrName?cap_first}(){
		return this.${param.attrName};
		}
	</#if>
</#list>
}