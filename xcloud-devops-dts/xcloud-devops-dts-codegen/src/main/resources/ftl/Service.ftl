<#include "utils/annotation.ftl" />

<#assign aDateTime = .now>
<#assign aDate = aDateTime?date>

<@class_annotation class_name="${className}" author="${functionAuthor}" date="${aDate?iso_utc}" />

