<#--{包名}/{模块名}/{分层(dao,entity,service,web)}/{子模块名}-->
<#--package com.wl4g.devops.dts.codegen.service;-->
<#macro class_package package_name module_name demixing_package sub_module_name>
${package_name}.${module_name}.${demixing_package}.${((sub_module_name!'')?length>0)?string((sub_module_name!''),".${sub_module_name}")}</#macro>