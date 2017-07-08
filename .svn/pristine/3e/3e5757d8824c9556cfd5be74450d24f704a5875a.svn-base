package ${servicePackage};

import ${entityPackage}.${entityName};

public interface I${serviceName} {

	/**
     * 根据id取得<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}Id
     * @return
     */
    ServiceResult<${entityName}> get${entityName}ById(Integer ${entityName?uncap_first}Id);
    
    /**
     * 保存<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}
     * @return
     */
     ServiceResult<Integer> save${entityName}(${entityName} ${entityName?uncap_first});
     
     /**
     * 更新<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}
     * @return
     */
     ServiceResult<Integer> update${entityName}(${entityName} ${entityName?uncap_first});
}