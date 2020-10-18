<#-- 全局宏定义 -->
<#assign currentDate = .now?date>
<#assign filteredTabColumns = javaSpecs.filterColumns(genTableColumns)><#-- 需过滤掉内置的字段(BaseBean) -->
<#assign attrTypes = javaSpecs.transformColumns(filteredTabColumns, "attrType")>
<#-- 是否有Date类型的字段(需@JsonFormat) -->
<#assign hasAttrNameOfDate = javaSpecs.hasFieldValue(attrTypes, "java.util.Date")>
<#-- 是否有非空的字段(需@NotNull) -->
<#assign hasAttrNameOfNotNull = javaSpecs.hasFieldValue(javaSpecs.transformColumns(filteredTabColumns, "noNull"), "1")>
// ${watermark}

${javaSpecs.wrapMultiComment(copyright)}

package ${organType}.${organName}.${projectName}.common.${moduleName}.${beanSubModulePackageName};

<#if javaSpecs.isConfOr(extOpts, "gen.swagger.ui", "officialOas", "bootstrapSwagger2")>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
</#if>
import com.wl4g.components.core.bean.BaseBean;
<#if hasAttrNameOfDate == true>
import com.fasterxml.jackson.annotation.JsonFormat;
</#if>
<#if hasAttrNameOfNotNull == true>
import javax.validation.constraints.NotNull;
</#if>
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
<#assign isExportExcel = javaSpecs.isConf(tExtOpts, "gen.tab.export-excel", "true")>
<#if isExportExcel == true>
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
</#if>

<#list javaSpecs.distinctList(attrTypes) as attrType>
    <#if !attrType?starts_with("java.lang")>
import ${attrType};
    </#if>
</#list>
/**
 * {@link ${entityName?cap_first}}
 *
 * @author ${author}
 * @version ${version}
 * @Date ${currentDate}
 * @since ${since}
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
<#if javaSpecs.isConfOr(extOpts, "gen.swagger.ui", "officialOas", "bootstrapSwagger2")>
@ApiModel("${javaSpecs.cleanComment(comments)}")<#-- 转义换行和双引号 -->
</#if>
<#if isExportExcel == true>
@ColumnWidth(40)
</#if>
public class ${entityName?cap_first} extends BaseBean {
    private static final long serialVersionUID = ${javaSpecs.genSerialVersionUID()}L;
<#list filteredTabColumns as col>

    /**
     * ${col.columnComment}
     */
    <#if javaSpecs.isConfOr(extOpts, "gen.swagger.ui", "officialOas", "bootstrapSwagger2")>
    @ApiModelProperty("${javaSpecs.cleanComment(col.columnComment)}")
    </#if>
	<#if isExportExcel == true>
    @ExcelProperty(value = { "${javaSpecs.cleanComment(col.columnComment)}" })
    </#if>
    <#if col.attrType == 'java.util.Date'>
        <#if col.simpleColumnType == 'datetime'>
            <#assign datePattern = 'yyyy-MM-dd HH:mm:ss'>
        <#else>
            <#assign datePattern = 'yyyy-MM-dd'>
        </#if>
    @JsonFormat(pattern = "${datePattern}")
    </#if>
    <#if col.noNull == '1'>
    @NotNull
    </#if>
    private ${javaSpecs.toSimpleJavaType(col.attrType)} ${col.attrName};
</#list>

    public ${entityName?cap_first}() {
    }
<#list filteredTabColumns as col>

    public ${entityName?cap_first} with${col.attrName?cap_first}(${javaSpecs.toSimpleJavaType(col.attrType)?cap_first} ${col.attrName?uncap_first}) {
        set${col.attrName?cap_first}(${col.attrName?uncap_first});
        return this;
    }
</#list>
}