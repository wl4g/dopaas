// ${watermark}

${javaSpecs.escapeCopyright(copyright)}

<#assign aDateTime = .now>
<#assign now = aDateTime?date>

package ${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName};

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.wl4g.components.core.bean.BaseBean;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

<#list attrTypes as attrType>
import ${attrType};
</#list>

/**
 * {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${now}
 * @since ${since}
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
//@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "new${entityName?cap_first}")
@ApiModel("${comments}")<#--TODO 解决换行和双引号 写在basespecs用javaspecs-->
@ColumnWidth(40)
public class ${entityName?cap_first} extends BaseBean {
	private static final long serialVersionUID = ${javaSpecs.genSerialVersionUID()}L;
<#list genTableColumns as param>
	<#if param.attrName != 'id' && param.attrName != 'createBy' && param.attrName != 'updateDate' && param.attrName != 'updateBy' && param.attrName != 'organizationCode'>

	/**
	 * ${param.columnComment}
	 */
    @ApiModelProperty("${param.columnComment}")<#--TODO 解决换行和双引号 写在basespecs用javaspecs-->
    @ExcelProperty(value = { "${param.columnComment}" })
	private ${javaSpecs.toSimpleJavaType(param.attrType)} ${param.attrName};
	</#if>
</#list>

	public ${entityName?cap_first}() {}
<#list genTableColumns as param>
	<#-- Ignore super(BaseBean) fields. -->
	<#if param.attrName != 'id' && param.attrName != 'createBy' && param.attrName != 'updateDate' && param.attrName != 'updateBy' && param.attrName != 'organizationCode'>

	public ${entityName?cap_first} with${param.attrName?cap_first}(${javaSpecs.toSimpleJavaType(param.attrType)?cap_first} ${param.attrName?uncap_first}) {
		set${param.attrName?cap_first}(${param.attrName?uncap_first});
		return this;
	}
	</#if>
</#list>
}