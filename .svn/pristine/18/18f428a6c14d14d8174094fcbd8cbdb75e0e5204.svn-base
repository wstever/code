package ${namespacePackage};

import ${entityPackage}.${entityName};
<#-- 
import java.util.List;
-->

@Repository
public interface ${daoName} {
 
 	<#list columns as column>
 		<#if keyColumn?? && column.dbColumn == keyColumn><#assign keyJavaType=column.colJavaType keyField=column.beanField></#if>
 	</#list>
 	<#if keyColumn??>
	${entityName} get(${keyJavaType} ${keyField});
	
	Integer insert(${entityName} ${entityName?uncap_first});
	
	Integer update(${entityName} ${entityName?uncap_first});
	<#-- 
	Integer updateNotNull(${entityName} ${entityName?uncap_first});
	-->
 	</#if>
 	<#-- 
 	List<${entityName}> list(${entityName} ${entityName?uncap_first});
 	-->
}