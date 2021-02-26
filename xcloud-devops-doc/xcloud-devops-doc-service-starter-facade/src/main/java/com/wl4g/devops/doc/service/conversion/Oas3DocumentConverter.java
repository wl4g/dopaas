/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Reference to website: http://wl4g.com
 */
package com.wl4g.devops.doc.service.conversion;

import com.wl4g.devops.common.bean.doc.EnterpriseApi;
import com.wl4g.devops.common.bean.doc.EnterpriseApiProperties;
import com.wl4g.devops.common.bean.doc.model.XCloudDocumentModel;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link Oas3DocumentConverter}
 *
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-16
 * @sine v1.0
 * @see
 */
public class Oas3DocumentConverter extends AbstractDocumentConverter<OpenAPI> {

    @Override
    public ConverterProviderKind kind() {
        return ConverterProviderKind.OAS3;
    }

    @Override
    public XCloudDocumentModel convertFrom(String documentJson) {
        return convertFrom(new OpenAPIV3Parser().readContents(documentJson).getOpenAPI());
    }

    @Override
    public XCloudDocumentModel convertFrom(OpenAPI document) {
        List<EnterpriseApi> enterpriseApis = new ArrayList<>();

        Paths paths = document.getPaths();
        Set<String> strings = paths.keySet();
        for (String key : strings) {
            PathItem pathItem = paths.get(key);

            EnterpriseApi enterpriseApi = new EnterpriseApi();
            enterpriseApi.setUrl(key);
            //enterpriseApi.setDescription(pathItem.getDescription());

            List<EnterpriseApiProperties> properties = new ArrayList<>();

            Operation operation = null;
            if (pathItem.getGet() != null) {
                operation = pathItem.getGet();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "GET ");
            }
            if (pathItem.getPost() != null) {
                operation = pathItem.getPost();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "POST ");
            }
            if (pathItem.getPut() != null) {
                operation = pathItem.getPut();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "PUT ");
            }
            if (pathItem.getDelete() != null) {
                operation = pathItem.getDelete();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "DELETE ");
            }

            if (operation != null) {
                enterpriseApi.setDescription(operation.getSummary());

                //parameters
                if (!CollectionUtils.isEmpty(operation.getParameters())) {
                    for (Parameter parameter : operation.getParameters()) {
                        EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
                        enterpriseApiProperties.setName(parameter.getName());
                        enterpriseApiProperties.setDescription(parameter.getDescription());
                        enterpriseApiProperties.setScope(REQUEST);
                        enterpriseApiProperties.setRequired(parameter.getRequired() ? "1" : "0");

                        Schema schema = parameter.getSchema();
                        if (null != schema && StringUtils.isNotBlank(schema.getTitle())) {
                            convertProperties(schema, enterpriseApiProperties);
                        } else if (null != schema) {
                            enterpriseApiProperties.setType(schema.getType());
                        }

                        //TODO ... more properties info
                        properties.add(enterpriseApiProperties);
                    }
                }

                //request body
                if (operation.getRequestBody() != null) {
                    String requestBodyRef = getRequestBodyRef(operation);
                    if(StringUtils.isNotBlank(requestBodyRef)){
                        convertBodyProperties(document, requestBodyRef, properties, REQUEST);
                    }
                }

                //response body
                if (operation.getResponses() != null) {
                    String responseBodyRef = getResponseBodyRef(operation);
                    if(StringUtils.isNotBlank(responseBodyRef)){
                        convertBodyProperties(document, responseBodyRef, properties, RESPONSE);
                    }
                }

            }

            //TODO ... more api info

            enterpriseApi.setProperties(properties);

            enterpriseApis.add(enterpriseApi);

        }


        return new XCloudDocumentModel(enterpriseApis);
    }

    @Override
    public OpenAPI convertTo(XCloudDocumentModel document) {
        // TODO Auto-generated method stub
        return super.convertTo(document);
    }

    private void convertProperties(Schema schema, EnterpriseApiProperties enterpriseApiProperties) {

        enterpriseApiProperties.setName(schema.getTitle());
        enterpriseApiProperties.setDescription(schema.getDescription());
        enterpriseApiProperties.setScope(REQUEST);
        //TODO ... more properties info

        Map<String, Schema> schemaMap = schema.getProperties();
        if (CollectionUtils.isEmpty(schemaMap)) {
            return;
        }

        List<EnterpriseApiProperties> children = new ArrayList<>();
        enterpriseApiProperties.setChildren(children);

        for (Map.Entry<String, Schema> entry : schemaMap.entrySet()) {
            //String key = entry.getKey();
            Schema value = entry.getValue();
            EnterpriseApiProperties childEnterpriseApiProperties = new EnterpriseApiProperties();
            convertProperties(value, childEnterpriseApiProperties);
            children.add(childEnterpriseApiProperties);
        }

    }

    private void convertBodyProperties(OpenAPI document, String ref, List<EnterpriseApiProperties> properties, String scope) {
        Map<String, Schema> bodyProperties = document.getComponents().getSchemas().get(ref).getProperties();
        convertBodyProperties(document,bodyProperties, properties, scope);
    }

    private void convertBodyProperties(OpenAPI document,Map<String, Schema> bodyProperties, List<EnterpriseApiProperties> properties, String scope) {
        for (Map.Entry<String, Schema> entry : bodyProperties.entrySet()) {
            EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
            enterpriseApiProperties.setName(entry.getKey());
            Schema value = entry.getValue();
            enterpriseApiProperties.setType(value.getType());
            enterpriseApiProperties.setDescription(value.getDescription());
            enterpriseApiProperties.setScope(scope);

            //TODO 递归 子属性
            if(StringUtils.isNotBlank(value.get$ref())){
                List<EnterpriseApiProperties> children = new ArrayList<>();
                convertBodyProperties(document,value.get$ref().substring(21),children,scope);
                enterpriseApiProperties.setChildren(children);
            }


            properties.add(enterpriseApiProperties);

        }
    }

    //TODO 获取RequestBody的ref 获取的方式有点深，暂时这么写
    private String getRequestBodyRef(Operation operation) {
        if (operation == null || operation.getRequestBody() == null || operation.getRequestBody().getContent() == null
                || operation.getRequestBody().getContent().values().size() <= 0
                || operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema() == null
                || StringUtils.isBlank(operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema().get$ref())
                || operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema().get$ref().length() <= 21) {
            return null;
        }
        return operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema().get$ref().substring(21);
    }

    //TODO ResponseBody 获取的方式有点深，暂时这么写
    private String getResponseBodyRef(Operation operation) {
        if(operation == null || operation.getResponses() == null || operation.getResponses().get("200") == null
        || operation.getResponses().get("200").getContent() == null
        || operation.getResponses().get("200").getContent().values().size() <=0
        ||operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema() == null
        ||StringUtils.isBlank(operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema().get$ref())
        ||operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema().get$ref().length() <= 21){
            return null;
        }
        return operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema().get$ref().substring(21);
    }


}
