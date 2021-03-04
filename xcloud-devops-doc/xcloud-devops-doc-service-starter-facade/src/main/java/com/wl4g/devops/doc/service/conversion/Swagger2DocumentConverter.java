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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.devops.common.bean.doc.EnterpriseApi;
import com.wl4g.devops.common.bean.doc.EnterpriseApiProperties;
import com.wl4g.devops.common.bean.doc.model.XCloudDocumentModel;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import io.swagger.v3.core.util.Json;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * {@link Swagger2DocumentConverter}
 *
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-16
 * @sine v1.0
 * @see
 */
public class Swagger2DocumentConverter extends AbstractDocumentConverter<Swagger> {

    @Override
    public ConverterProviderKind kind() {
        return ConverterProviderKind.SWAGGER2;
    }

    @Override
    public XCloudDocumentModel convertFrom(String documentJson) {
        Swagger swagger = new SwaggerParser().parse(documentJson);
        return convertFrom(swagger);
    }

    @Override
    public XCloudDocumentModel convertFrom(Swagger swagger) {
        List<EnterpriseApi> enterpriseApis = new ArrayList<>();

        Map<String, Path> paths = swagger.getPaths();
        for (Map.Entry<String, Path> entry : paths.entrySet()) {
            Path path = entry.getValue();
            EnterpriseApi enterpriseApi = new EnterpriseApi();
            enterpriseApi.setUrl(entry.getKey());
            enterpriseApi.setName(entry.getKey());
            //

            Operation operation = null;
            if (path.getGet() != null) {
                operation = path.getGet();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "GET ");
            }
            if (path.getPost() != null) {
                operation = path.getPost();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "POST ");
            }
            if (path.getPut() != null) {
                operation = path.getPut();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "PUT ");
            }
            if (path.getDelete() != null) {
                operation = path.getDelete();
                enterpriseApi.setMethod(enterpriseApi.getMethod() + "DELETE ");
            }

            if (operation == null) {
                continue;
            }
            enterpriseApi.setDescription(operation.getSummary());
            enterpriseApi.setName(operation.getSummary());

            List<EnterpriseApiProperties> properties = new ArrayList<>();

            //parameters
            List<Parameter> parameters = operation.getParameters();
            if (!CollectionUtils.isEmpty(parameters)) {
                for (Parameter parameter : parameters) {

                    if (parameter instanceof QueryParameter) {
                        QueryParameter queryParameter = (QueryParameter) parameter;
                        EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
                        enterpriseApiProperties.setName(parameter.getName());
                        enterpriseApiProperties.setScope(REQUEST);
                        enterpriseApiProperties.setType(queryParameter.getType());
                        enterpriseApiProperties.setRequired(parameter.getRequired() ? "1" : "0");
                        properties.add(enterpriseApiProperties);
                    } else if (parameter instanceof BodyParameter) {
                        BodyParameter bodyParameter = (BodyParameter) parameter;
                        String reference = bodyParameter.getSchema().getReference();
                        String ref = reference.substring(14);

                        convertBodyProperties(swagger, ref, properties, REQUEST);
                    }
                }
            }
            Map<String, Response> responses = operation.getResponses();
            if (!CollectionUtils.isEmpty(responses)) {
                Response response = responses.get("200");
                Model responseSchema = response.getResponseSchema();
                if (responseSchema != null) {
                    String reference = responseSchema.getReference();
                    String ref = reference.substring(14);
                    convertBodyProperties(swagger, ref, properties, RESPONSE);
                }
            }
            enterpriseApi.setProperties(properties);
            enterpriseApis.add(enterpriseApi);
        }
        return new XCloudDocumentModel(enterpriseApis);
    }


    private void convertBodyProperties(Swagger swagger, String ref, List<EnterpriseApiProperties> properties, String scope) {
        Model model = swagger.getDefinitions().get(ref);
        Map<String, Property> propertiesModel = model.getProperties();

        for (Map.Entry<String, Property> entry : propertiesModel.entrySet()) {

            EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
            String key = entry.getKey();
            Property property = entry.getValue();

            enterpriseApiProperties.setName(key);
            enterpriseApiProperties.setType(property.getType());
            enterpriseApiProperties.setScope(scope);
            enterpriseApiProperties.setDescription(property.getDescription());
            enterpriseApiProperties.setRequired(property.getRequired() ? "1" : "0");

            //TODO children
            if (property instanceof RefProperty) {
                String childRef = ((RefProperty) property).get$ref();
                childRef = childRef.substring(14);
                List<EnterpriseApiProperties> childProperties = new ArrayList<>();
                convertBodyProperties(swagger, childRef, childProperties, scope);
                enterpriseApiProperties.setChildren(childProperties);
            }

            if (property instanceof ArrayProperty) {
                Property items = ((ArrayProperty) property).getItems();
                if (items instanceof RefProperty) {
                    String childRef = ((RefProperty) items).get$ref();
                    childRef = childRef.substring(14);
                    if (!ref.equals(childRef)) {
                        List<EnterpriseApiProperties> childProperties = new ArrayList<>();
                        convertBodyProperties(swagger, childRef, childProperties, scope);
                        enterpriseApiProperties.setChildren(childProperties);
                    }
                }
            }
            properties.add(enterpriseApiProperties);
        }
    }

    //==========================================================================================

    @Override
    public String convertToJson(XCloudDocumentModel document) throws IOException {
        Swagger swagger = convertTo(document);
        ObjectMapper mapper = Json.mapper();
        String json = mapper.writeValueAsString(swagger);
        return json;
    }

    @Override
    public Swagger convertTo(XCloudDocumentModel document) {
        Swagger swagger = new Swagger();
        List<EnterpriseApi> enterpriseApis = document.getEnterpriseApis();

        Map<String, Path> paths = new LinkedHashMap<String, Path>();
        for (EnterpriseApi enterpriseApi : enterpriseApis) {
            Path path = new Path();
            Operation operation = new Operation();

            List<EnterpriseApiProperties> properties = enterpriseApi.getProperties();
            List<EnterpriseApiProperties> requestProperties = getPropertiesByScope(properties, REQUEST);
            List<EnterpriseApiProperties> responseProperties = getPropertiesByScope(properties, RESPONSE);

            if(enterpriseApi.getMethod().contains("GET") || enterpriseApi.getMethod().contains("DELETE")){
                List<Parameter> parameters = new ArrayList<Parameter>();

                for(EnterpriseApiProperties enterpriseApiProperties : requestProperties){
                    //TODO 除了QueryParameter，还有其他
                    QueryParameter queryParameter = new QueryParameter();
                    queryParameter.setName(enterpriseApiProperties.getName());
                    queryParameter.setType(enterpriseApiProperties.getType());
                    queryParameter.setRequired("1".equals(enterpriseApiProperties.getRequired()));
                    parameters.add(queryParameter);
                }
                operation.setParameters(parameters);

                if(enterpriseApi.getMethod().contains("GET")){
                    path.setGet(operation);
                }
                if(enterpriseApi.getMethod().contains("DELETE")){
                    path.setDelete(operation);
                }
            }

            if(enterpriseApi.getMethod().contains("POST") || enterpriseApi.getMethod().contains("PUT")){
                convertBackOperation(swagger, operation,enterpriseApi.getName(), responseProperties);
            }

            paths.put(enterpriseApi.getUrl() ,path);
        }
        swagger.setPaths(paths);


        return swagger;
    }


    private void convertBackOperation(Swagger swagger,Operation operation,String objName,List<EnterpriseApiProperties> properties){
        Map<String, Response> responses = new LinkedHashMap<String, Response>();

        Response response = new Response();

        Model responseSchema = new RefModel();

        String ref = "#/definitions/" + objName;
        responseSchema.setReference(ref);//TODO

        response.setResponseSchema(responseSchema);

        responses.put("200",response);

        convertBackBodyProperties(swagger, ref, properties,false);

        operation.setResponses(responses);
    }


    private void convertBackBodyProperties(Swagger swagger,String ref, List<EnterpriseApiProperties> enterpriseApiProperties, boolean isArray) {

        Map<String, Model> definitions = swagger.getDefinitions();
        if(CollectionUtils.isEmpty(definitions)){
            definitions = new LinkedHashMap<String, Model>();
        }
        Model model = definitions.get(ref);
        if(Objects.isNull(model)){
            model = new RefModel();
        }

        Map<String, Property> properties = new HashMap<>();
        model.setProperties(properties);

        for(EnterpriseApiProperties apiProperties : enterpriseApiProperties){
            if(isArray){
                ArrayProperty arrayProperty = new ArrayProperty();
                RefProperty property = new RefProperty();
                arrayProperty.setItems(property);
                properties.put(apiProperties.getName(),property);
                property.setDescription(apiProperties.getDescription());
                property.setRequired("1".equals(apiProperties.getRequired()));
                property.setType(apiProperties.getType());
            }else{
                RefProperty property = new RefProperty();
                properties.put(apiProperties.getName(),property);
                property.setDescription(apiProperties.getDescription());
                property.setRequired("1".equals(apiProperties.getRequired()));
                property.setType(apiProperties.getType());
            }
            if(!CollectionUtils.isEmpty(apiProperties.getChildren())){
                boolean childIsArray = "array".equalsIgnoreCase(apiProperties.getType());
                convertBackBodyProperties(swagger,ref+ "."+apiProperties.getName(),apiProperties.getChildren(),childIsArray);
            }
        }
    }

}
