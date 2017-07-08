
<#list columns as column>
				<th field="${column.beanField}" width="100" align="left" halign="center">${column.colComment}</th>
</#list>

<#-- <#macro mapperEl value>${r"#{"}${value}}</#macro> -->
<#macro mapperEl value>${r"${("}${value?uncap_first}</#macro>
<#macro mapperEl2 value>${value}${r")!''"}}</#macro>
<#list columns as column>
	<#if column.colJavaType == "java.lang.Integer" || column.colJavaType == "java.math.BigDecimal">
					<div class="fluidbox">
						<p class="p12 p-item">
							<label class="lab-item"><font class="red">*</font>${column.colComment}：</label>
							<input class="easyui-numberbox w280" type="text" id="${column.beanField}" name="${column.beanField}" value="<@mapperEl entityName/>.<@mapperEl2 column.beanField/>" data-options="required:true,max:请替换实际大小" >
						</p>
					</div>
					<br/>
	<#else>
					<div class="fluidbox">
						<p class="p12 p-item">
							<label class="lab-item"><font class="red">*</font>${column.colComment}：</label>
							<input class="easyui-validatebox txt w280" type="text" id="${column.beanField}" name="${column.beanField}" value="<@mapperEl entityName/>.<@mapperEl2 column.beanField/>" data-options="required:true,validType:'length[0,${column.characterMaximumLength}]'" >
						</p>
					</div>
					<br/>
	</#if>
</#list>


<#list columns as column>
					<div class="fluidbox">
						<p class="p12 p-item">
							<label class="lab-item"><font class="red">*</font>${column.colComment}：</label>
							<label><@mapperEl entityName/>.<@mapperEl2 column.beanField/></label>
						</p>
					</div>
					<br/>
</#list>