package ${modelPackage};

import ${namespacePackage}.${daoName};
import ${entityPackage}.${entityName};

@Component
public class ${modelName} {

	private static org.apache.log4j.Logger log = org.apache.log4j.LogManager
                                                   .getLogger(${modelName}.class);
    
    @Resource
    private ${daoName} ${daoName?uncap_first};
    
    /**
     * 根据id取得<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}Id
     * @return
     */
    public ${entityName} get${entityName}ById(Integer ${entityName?uncap_first}Id) {
    	return ${daoName?uncap_first}.get(${entityName?uncap_first}Id);
    }
    
    /**
     * 保存<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}
     * @return
     */
     public Integer save${entityName}(${entityName} ${entityName?uncap_first}) {
     	this.dbConstrains(${entityName?uncap_first});
     	return ${daoName?uncap_first}.insert(${entityName?uncap_first});
     }
     
     /**
     * 更新<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}
     * @return
     */
     public Integer update${entityName}(${entityName} ${entityName?uncap_first}) {
     	this.dbConstrains(${entityName?uncap_first});
     	return ${daoName?uncap_first}.update(${entityName?uncap_first});
     }
     
     private void dbConstrains(${entityName} ${entityName?uncap_first}) {
     	<#list columns as column>
 		<#if column.colJavaType == 'java.lang.String'>
		${entityName?uncap_first}.set${column.beanField?cap_first}(StringUtil.dbSafeString(${entityName?uncap_first}.get${column.beanField?cap_first}(), <#if column.isNullable == 'NO' || column.isNullable == 'no'>false<#else>true</#if>, ${column.characterMaximumLength?c}));
 		</#if>
     	</#list>
     }
}