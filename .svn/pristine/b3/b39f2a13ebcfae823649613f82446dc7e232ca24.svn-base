<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<#macro mapperEl value>${r"#{"}${value}}</#macro>
<#if keyColumn??>
	<#list columns as column>
		<#if column.dbColumn == keyColumn><#assign beanKeyField = column.beanField></#if>
	</#list>
</#if>
<mapper namespace="${namespacePackage}.${daoName}">
	<resultMap id="${resultMapId}" type="${entityPackage}.${entityName}">
		<#list columns as column>
			<result property="${column.beanField}" column="${column.dbColumn}" />
		</#list>
	</resultMap>
	
	<select id="get" parameterType="Integer" resultMap="${resultMapId}">
		select
		   *
		from `${tableName}`
		<#if keyColumn??>
		where `${keyColumn}` = <@mapperEl beanKeyField/>
		</#if>
	</select>
	
	<#if keyColumn??>
	<update id="update" parameterType="${entityPackage}.${entityName}">
        update `${tableName}` 
    	<set>
		<#list columns as column>
			<if test="${column.beanField} != null">`${column.dbColumn}`= <@mapperEl column.beanField/><#if column_index != (columns?size - 1)>,</#if></if>
		</#list>
	    </set>
        where `${keyColumn}` = <@mapperEl beanKeyField/>
	</update>
	
	<insert id="insert" parameterType="${entityPackage}.${entityName}" useGeneratedKeys="true" keyProperty="${beanKeyField}" keyColumn="${keyColumn}">
		insert into 
		`${tableName}`
		(
		<#list columns as column>
			<#if column.dbColumn != keyColumn>`${column.dbColumn}`<#if column_index != (columns?size - 1)>,</#if></#if>
		</#list>
		)
		values
		(
	    <#list columns as column>
			<#if column.dbColumn != keyColumn><@mapperEl column.beanField/><#if column_index != (columns?size - 1)>,</#if></#if>
		</#list>
		)
	</insert>
	</#if>
	
	<#-- 
	<select id="list" parameterType="${entityPackage}.${entityName}" resultMap="${resultMapId}">
		select
		<#list columns as column>
			`${column.dbColumn}`<#if column_index != (columns?size - 1)>,</#if>
		</#list>
		from `${tableName}`
		<where>
			<#list columns as column>
				<if test="${column.beanField} != null">and `${column.dbColumn}`= <@mapperEl column.beanField/></if>
			</#list>
		</where>
	</select>
	-->
</mapper>