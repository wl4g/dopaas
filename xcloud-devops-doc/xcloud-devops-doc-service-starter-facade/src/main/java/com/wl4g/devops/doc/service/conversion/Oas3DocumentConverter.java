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
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
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
            enterpriseApi.setDescription(pathItem.getDescription());

            List<EnterpriseApiProperties> properties = new ArrayList<>();


            List<Parameter> parameters;
            if(pathItem.getGet() != null) {
                parameters = pathItem.getGet().getParameters();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "GET ");
            }
            else if(pathItem.getPost() != null){
                parameters = pathItem.getPost().getParameters();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "POST ");
            }else if(pathItem.getPut() != null){
                parameters = pathItem.getPut().getParameters();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "PUT ");
            }else if(pathItem.getDelete() != null){
                parameters = pathItem.getDelete().getParameters();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "DELETE ");
            }else{
                parameters = new ArrayList<>();
            }

            //List<Parameter> parameters = pathItem.getParameters();
            for (Parameter parameter : parameters) {
                EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
                enterpriseApiProperties.setName(parameter.getName());
                enterpriseApiProperties.setDescription(parameter.getDescription());
                enterpriseApiProperties.setRequired(parameter.getRequired() ? "1" : "0");

                Schema schema = parameter.getSchema();
                if (null != schema) {
                    convertProperties(schema, enterpriseApiProperties);
                }

                //TODO ... more properties info

                properties.add(enterpriseApiProperties);

            }

            //TODO ... more api info

            enterpriseApi.setProperties(properties);

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

}
