package ${servicePackage}.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ${modelPackage}.${modelName};
import ${servicePackage}.${serviceName};
import ${entityPackage}.${entityName};

@Service(value = "${serviceName?uncap_first}")
public class ${serviceName}Impl implements I${serviceName} {
	private static Logger      log = LogManager.getLogger(${serviceName}Impl.class);
	
	@Resource
	private ${modelName} ${modelName?uncap_first};
    
     /**
     * 根据id取得<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}Id
     * @return
     */
    @Override
    public ServiceResult<${entityName}> get${entityName}ById(Integer ${entityName?uncap_first}Id) {
        ServiceResult<${entityName}> result = new ServiceResult<${entityName}>();
        try {
            result.setResult(${modelName?uncap_first}.get${entityName}ById(${entityName?uncap_first}Id));
        } catch (BusinessException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error("[I${serviceName}][get${entityName}ById]根据id["+${entityName?uncap_first}Id+"]取得<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>时出现未知异常：" + e.getMessage());
        } catch (Exception e) {
            result.setError(ConstantsEJS.SERVICE_RESULT_CODE_SYSERROR, ConstantsEJS.SERVICE_RESULT_EXCEPTION_SYSERROR);
            log.error("[I${serviceName}][get${entityName}ById]根据id["+${entityName?uncap_first}Id+"]取得<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>时出现未知异常：",
                e);
        }
        return result;
    }
    
    /**
     * 保存<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}
     * @return
     */
     @Override
     public ServiceResult<Integer> save${entityName}(${entityName} ${entityName?uncap_first}) {
     	ServiceResult<Integer> result = new ServiceResult<Integer>();
        try {
            result.setResult(${modelName?uncap_first}.save${entityName}(${entityName?uncap_first}));
        } catch (BusinessException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error("[I${serviceName}][save${entityName}]保存<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>时出现未知异常：" + e.getMessage());
        } catch (Exception e) {
            result.setError(ConstantsEJS.SERVICE_RESULT_CODE_SYSERROR, ConstantsEJS.SERVICE_RESULT_EXCEPTION_SYSERROR);
            log.error("[I${serviceName}][save${entityName}]保存<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>时出现未知异常：",
                e);
        }
        return result;
     }
     
     /**
     * 更新<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>
     * @param  ${entityName?uncap_first}
     * @return
     * @see ${servicePackage}.${serviceName}#update${entityName}(${entityName})
     */
     @Override
     public ServiceResult<Integer> update${entityName}(${entityName} ${entityName?uncap_first}) {
     	ServiceResult<Integer> result = new ServiceResult<Integer>();
        try {
            result.setResult(${modelName?uncap_first}.update${entityName}(${entityName?uncap_first}));
        } catch (BusinessException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error("[I${serviceName}][update${entityName}]更新<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>时出现未知异常：" + e.getMessage());
        } catch (Exception e) {
            result.setError(ConstantsEJS.SERVICE_RESULT_CODE_SYSERROR, ConstantsEJS.SERVICE_RESULT_EXCEPTION_SYSERROR);
            log.error("[I${serviceName}][update${entityName}]更新<#if tableComment?? && tableComment?length &gt; 0>${tableComment}<#else>${tableName}对象</#if>时出现未知异常：",
                e);
        }
        return result;
     }
}