${watermark}
<#assign aDateTime = .now>
<#assign now = aDateTime?date>
package ${packageName}.commons.bean.${moduleName};

import java.util.Date;

* ${entityName}
*
* @author ${author}
* @Date ${now}
*/
public class ${entityName} {

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