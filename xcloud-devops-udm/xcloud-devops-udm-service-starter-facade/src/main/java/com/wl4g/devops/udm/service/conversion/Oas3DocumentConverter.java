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
package com.wl4g.devops.udm.service.conversion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.devops.common.bean.udm.EnterpriseApi;
import com.wl4g.devops.common.bean.udm.EnterpriseApiProperties;
import com.wl4g.devops.common.bean.udm.model.XCloudDocumentModel;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;

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
			// enterpriseApi.setDescription(pathItem.getDescription());

			List<EnterpriseApiProperties> properties = new ArrayList<>();

			Operation operation = null;
			if (pathItem.getGet() != null) {
				operation = pathItem.getGet();
				enterpriseApi.setMethod(enterpriseApi.getMethod() + "GET ");
			}
			if (pathItem.getPost() != null) {
				operation = operation == null ? pathItem.getPost() : operation;
				enterpriseApi.setMethod(enterpriseApi.getMethod() + "POST ");
			}
			if (pathItem.getPut() != null) {
				operation = operation == null ? pathItem.getPut() : operation;
				enterpriseApi.setMethod(enterpriseApi.getMethod() + "PUT ");
			}
			if (pathItem.getDelete() != null) {
				operation = operation == null ? pathItem.getDelete() : operation;
				enterpriseApi.setMethod(enterpriseApi.getMethod() + "DELETE ");
			}

			if (operation != null) {
				enterpriseApi.setDescription(operation.getSummary());

				// parameters
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

						// TODO ... more properties info
						properties.add(enterpriseApiProperties);
					}
				}

				// request body
				if (operation.getRequestBody() != null) {
					String requestBodyRef = getRequestBodyRef(operation);
					if (StringUtils.isNotBlank(requestBodyRef)) {
						convertBodyProperties(document, requestBodyRef, properties, REQUEST);
					}
				}

				// response body
				if (operation.getResponses() != null) {
					String responseBodyRef = getResponseBodyRef(operation);
					if (StringUtils.isNotBlank(responseBodyRef)) {
						convertBodyProperties(document, responseBodyRef, properties, RESPONSE);
					}
				}

			}

			// TODO ... more api info

			enterpriseApi.setProperties(properties);

			enterpriseApis.add(enterpriseApi);

		}

		return new XCloudDocumentModel(enterpriseApis);
	}

	@Override
	public String convertToJson(XCloudDocumentModel document) throws IOException {
		OpenAPI openAPI = convertTo(document);
		ObjectMapper mapper = Json.mapper();
		String json = mapper.writeValueAsString(openAPI);
		return json;
	}

	@Override
	public OpenAPI convertTo(XCloudDocumentModel document) {
		// TODO Auto-generated method stub
		OpenAPI openAPI = new OpenAPI();

		List<EnterpriseApi> enterpriseApis = document.getEnterpriseApis();

		Paths paths = new Paths();

		for (EnterpriseApi enterpriseApi : enterpriseApis) {
			PathItem item = new PathItem();
			item.setDescription(enterpriseApi.getDescription());
			item.setSummary(enterpriseApi.getDescription());

			List<EnterpriseApiProperties> requestProperties = getPropertiesByScope(enterpriseApi.getProperties(), REQUEST);
			List<EnterpriseApiProperties> responseProperties = getPropertiesByScope(enterpriseApi.getProperties(), RESPONSE);

			Operation operation = new Operation();
			operation.setSummary(enterpriseApi.getDescription());
			if (enterpriseApi.getMethod().contains("GET") || enterpriseApi.getMethod().contains("DELETE")) {
				List<Parameter> parameters = new ArrayList<>();
				for (EnterpriseApiProperties enterpriseApiProperties : requestProperties) {
					Parameter parameter = new Parameter();
					parameter.setName(enterpriseApiProperties.getName());
					parameter.setDescription(enterpriseApiProperties.getDescription());
					parameter.setRequired("1".equals(enterpriseApiProperties.getRequired()));
					parameter.setIn("query");

					if ("object".equals(enterpriseApiProperties.getType())) {
						// TODO
					} else {
						Schema schema = new Schema();
						schema.setType(enterpriseApiProperties.getType());
						parameter.setSchema(schema);
					}
					parameters.add(parameter);
				}

				operation.setParameters(parameters);
				if (enterpriseApi.getMethod().contains("GET")) {
					item.setGet(operation);
				}
				if (enterpriseApi.getMethod().contains("DELETE")) {
					item.setDelete(operation);
				}

			} else {
				setRequestBodyRef(openAPI, operation, requestProperties, enterpriseApi.getName() + "RequestBody");
				if (enterpriseApi.getMethod().contains("POST")) {
					item.setPost(operation);
				}
				if (enterpriseApi.getMethod().contains("PUT")) {
					item.setPut(operation);
				}

			}

			setResponseBodyRef(openAPI, operation, responseProperties, enterpriseApi.getName() + "ResponseBody");

			paths.addPathItem(enterpriseApi.getUrl(), item);
		}

		openAPI.setPaths(paths);

		return openAPI;
	}

	private void convertProperties(Schema schema, EnterpriseApiProperties enterpriseApiProperties) {

		enterpriseApiProperties.setName(schema.getTitle());
		enterpriseApiProperties.setDescription(schema.getDescription());
		enterpriseApiProperties.setScope(REQUEST);
		// TODO ... more properties info

		Map<String, Schema> schemaMap = schema.getProperties();
		if (CollectionUtils.isEmpty(schemaMap)) {
			return;
		}

		List<EnterpriseApiProperties> children = new ArrayList<>();
		enterpriseApiProperties.setChildren(children);

		for (Map.Entry<String, Schema> entry : schemaMap.entrySet()) {
			// String key = entry.getKey();
			Schema value = entry.getValue();
			EnterpriseApiProperties childEnterpriseApiProperties = new EnterpriseApiProperties();
			convertProperties(value, childEnterpriseApiProperties);
			children.add(childEnterpriseApiProperties);
		}

	}

	private void convertBodyProperties(OpenAPI document, String ref, List<EnterpriseApiProperties> properties, String scope) {
		Map<String, Schema> bodyProperties = document.getComponents().getSchemas().get(ref).getProperties();
		convertBodyProperties(document, bodyProperties, properties, scope);
	}

	private void convertBodyProperties(OpenAPI document, Map<String, Schema> bodyProperties,
			List<EnterpriseApiProperties> properties, String scope) {
		for (Map.Entry<String, Schema> entry : bodyProperties.entrySet()) {
			EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();
			enterpriseApiProperties.setName(entry.getKey());
			Schema value = entry.getValue();
			enterpriseApiProperties.setType(value.getType());
			enterpriseApiProperties.setDescription(value.getDescription());
			enterpriseApiProperties.setScope(scope);

			// TODO 递归 子属性
			if (StringUtils.isNotBlank(value.get$ref())) {
				List<EnterpriseApiProperties> children = new ArrayList<>();
				convertBodyProperties(document, value.get$ref().substring(21), children, scope);
				enterpriseApiProperties.setChildren(children);
			}

			properties.add(enterpriseApiProperties);

		}
	}

	// TODO 获取RequestBody的ref 获取的方式有点深，暂时这么写
	private String getRequestBodyRef(Operation operation) {
		if (operation == null || operation.getRequestBody() == null || operation.getRequestBody().getContent() == null
				|| operation.getRequestBody().getContent().values().size() <= 0
				|| operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema() == null
				|| StringUtils.isBlank(
						operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema().get$ref())
				|| operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema().get$ref()
						.length() <= 21) {
			return null;
		}
		return operation.getRequestBody().getContent().values().stream().findFirst().get().getSchema().get$ref().substring(21);
	}

	// TODO ResponseBody 获取的方式有点深，暂时这么写
	private String getResponseBodyRef(Operation operation) {
		if (operation == null || operation.getResponses() == null || operation.getResponses().get("200") == null
				|| operation.getResponses().get("200").getContent() == null
				|| operation.getResponses().get("200").getContent().values().size() <= 0
				|| operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema() == null
				|| StringUtils.isBlank(operation.getResponses().get("200").getContent().values().stream().findFirst().get()
						.getSchema().get$ref())
				|| operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema().get$ref()
						.length() <= 21) {
			return null;
		}
		return operation.getResponses().get("200").getContent().values().stream().findFirst().get().getSchema().get$ref()
				.substring(21);
	}

	private void setRequestBodyRef(OpenAPI openAPI, Operation operation, List<EnterpriseApiProperties> properties,
			String bodyName) {
		RequestBody requestBody = new RequestBody();
		operation.setRequestBody(requestBody);

		Content content = new Content();
		requestBody.setContent(content);

		MediaType item = new MediaType();
		Schema schema = new Schema();
		Schema schemasItem = new Schema();
		String ref = convertBackBodyProperties(openAPI, schemasItem, properties, REQUEST, bodyName);

		schema.set$ref(ref);
		item.setSchema(schema);
		content.addMediaType("application/json", item);// TODO

	}

	private void setResponseBodyRef(OpenAPI openAPI, Operation operation, List<EnterpriseApiProperties> properties,
			String bodyName) {
		ApiResponses apiResponses = new ApiResponses();
		operation.setResponses(apiResponses);

		ApiResponse apiResponse = new ApiResponse();
		apiResponses.addApiResponse("200", apiResponse);

		Content content = new Content();
		apiResponse.setContent(content);

		MediaType item = new MediaType();
		Schema schema = new Schema();
		Schema schemasItem = new Schema();
		String ref = convertBackBodyProperties(openAPI, schemasItem, properties, RESPONSE, bodyName);

		schema.set$ref(ref);

		item.setSchema(schema);
		content.addMediaType("*/*", item);

	}

	private String convertBackBodyProperties(OpenAPI document, Schema schemasItem, List<EnterpriseApiProperties> properties,
			String scope, String subref) {

		Components components = document.getComponents();
		if (null == components) {
			components = new Components();
		}
		document.setComponents(components);

		String ref = "#/components/schemas/" + subref;
		components.addSchemas(subref, schemasItem);

		schemasItem.setTitle(subref);

		Map<String, Schema> bodyProperties = new HashMap<>();
		convertBackBodyProperties(document, schemasItem, bodyProperties, properties, scope, subref);
		schemasItem.setProperties(bodyProperties);
		// TODO set ref or return ref
		// schemasItem.set$ref(ref);
		return ref;
	}

	private void convertBackBodyProperties(OpenAPI document, Schema schemasItem, Map<String, Schema> bodyProperties,
			List<EnterpriseApiProperties> properties, String scope, String subref) {
		for (EnterpriseApiProperties enterpriseApiProperties : properties) {

			Schema schema = new Schema();
			schema.setType(enterpriseApiProperties.getType());
			schema.setDescription(enterpriseApiProperties.getDescription());

			if (!CollectionUtils.isEmpty(enterpriseApiProperties.getChildren())) {
				Schema childSchema = new Schema();
				String obName = subref + "." + enterpriseApiProperties.getName();
				String ref = convertBackBodyProperties(document, childSchema, enterpriseApiProperties.getChildren(), scope,
						obName);
				schema.set$ref(ref);
			}

			bodyProperties.put(enterpriseApiProperties.getName(), schema);

		}
		schemasItem.setProperties(bodyProperties);

	}

}
