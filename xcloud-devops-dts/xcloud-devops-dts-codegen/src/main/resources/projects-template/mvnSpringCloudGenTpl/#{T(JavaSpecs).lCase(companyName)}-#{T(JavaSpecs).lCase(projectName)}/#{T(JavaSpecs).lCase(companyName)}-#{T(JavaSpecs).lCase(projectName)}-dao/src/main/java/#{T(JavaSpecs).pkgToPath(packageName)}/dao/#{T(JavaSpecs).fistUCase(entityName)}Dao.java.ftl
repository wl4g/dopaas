${watermark}
<#assign aDateTime = .now>
<#assign now = aDateTime?date>
package ${packageName}.dao.${moduleName};

import java.util.Date;
import ${packageName}.commons.bean.${moduleName}.${entityName?cap_first};

* ${entityName}
*
* @author ${author}
* @Date ${now}
*/
public class ${entityName?cap_first}Dao {

    int deleteByPrimaryKey(Integer id);

    int insertSelective(${entityName?cap_first} ${entityName?uncap_first});

    ${entityName?cap_first} selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(${entityName?cap_first} ${entityName?uncap_first});

    List<${entityName?cap_first}> list(@Param("name") String name);

}