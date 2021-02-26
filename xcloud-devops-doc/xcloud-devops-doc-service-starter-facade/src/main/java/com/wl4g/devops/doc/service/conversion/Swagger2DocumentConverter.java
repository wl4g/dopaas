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
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	public XCloudDocumentModel convertFrom(Swagger document) {
		List<EnterpriseApi> enterpriseApis = new ArrayList<>();

		Map<String, Path> paths = document.getPaths();
		for (Map.Entry<String, Path> entry : paths.entrySet()) {
			Path path = entry.getValue();
			EnterpriseApi enterpriseApi = new EnterpriseApi();
			enterpriseApi.setUrl(entry.getKey());
			enterpriseApi.setName(entry.getKey());
			//

			Operation operation = null;
			if(path.getGet() != null){
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

			if(operation == null){
				continue;
			}
			enterpriseApi.setDescription(operation.getSummary());
			enterpriseApi.setName(operation.getSummary());

			List<EnterpriseApiProperties> properties = new ArrayList<>();

			//parameters
			List<Parameter> parameters = operation.getParameters();
			if(!CollectionUtils.isEmpty(parameters)){
				for(Parameter parameter : parameters){

					if(parameter instanceof QueryParameter){
						QueryParameter queryParameter = (QueryParameter)parameter;
						queryParameter.getType();
						EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
						enterpriseApiProperties.setName(parameter.getName());
						enterpriseApiProperties.setScope(REQUEST);
						enterpriseApiProperties.setType(queryParameter.getType());
						enterpriseApiProperties.setRequired(parameter.getRequired()? "1":"0");

						properties.add(enterpriseApiProperties);
					}else if(parameter instanceof BodyParameter){
						BodyParameter bodyParameter = (BodyParameter) parameter;
						String reference = bodyParameter.getSchema().getReference();
						String ref = reference.substring(14);

						convertBodyProperties(document,ref,properties,REQUEST);
					}


				}
			}


			Map<String, Response> responses = operation.getResponses();
			if(!CollectionUtils.isEmpty(responses)){
				Response response = responses.get("200");
				Model responseSchema = response.getResponseSchema();
				if(responseSchema!=null){
					String reference = response.getResponseSchema().getReference();
					String ref = reference.substring(14);
					convertBodyProperties(document,ref,properties,RESPONSE);
				}
			}

			enterpriseApi.setProperties(properties);

			enterpriseApis.add(enterpriseApi);

		}


		return new XCloudDocumentModel(enterpriseApis);
	}

	@Override
	public Swagger convertTo(XCloudDocumentModel document) {
		// TODO Auto-generated method stub
		return super.convertTo(document);
	}

	private void convertBodyProperties(Swagger document, String ref, List<EnterpriseApiProperties> properties, String scope) {
		Model model = document.getDefinitions().get(ref);
		Map<String, Property> propertiesModel = model.getProperties();

		for (Map.Entry<String, Property> entry : propertiesModel.entrySet()) {

			EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
			String key = entry.getKey();
			Property property = entry.getValue();

			enterpriseApiProperties.setName(key);
			enterpriseApiProperties.setType(property.getType());
			enterpriseApiProperties.setScope(scope);
			enterpriseApiProperties.setDescription(property.getDescription());
			enterpriseApiProperties.setRequired(property.getRequired()?"1":"0");

			//TODO children
			if(property instanceof RefProperty){
				String childRef = ((RefProperty) property).get$ref();
				childRef = childRef.substring(14);
				List<EnterpriseApiProperties> childProperties = new ArrayList<>();
				convertBodyProperties(document,childRef,childProperties,scope);
				enterpriseApiProperties.setChildren(childProperties);
			}

			if(property instanceof ArrayProperty){
				Property items = ((ArrayProperty) property).getItems();
				if(items instanceof RefProperty){
					String childRef = ((RefProperty) items).get$ref();
					childRef = childRef.substring(14);
					if(!ref.equals(childRef)){
						List<EnterpriseApiProperties> childProperties = new ArrayList<>();
						convertBodyProperties(document,childRef,childProperties,scope);
						enterpriseApiProperties.setChildren(childProperties);
					}
				}
			}

			properties.add(enterpriseApiProperties);
		}

	}

}
