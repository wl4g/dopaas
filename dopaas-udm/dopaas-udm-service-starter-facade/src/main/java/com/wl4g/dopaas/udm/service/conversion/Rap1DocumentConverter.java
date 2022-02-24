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
package com.wl4g.dopaas.udm.service.conversion;

import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.serialize.JacksonUtils;
import com.wl4g.dopaas.common.bean.udm.EnterpriseApi;
import com.wl4g.dopaas.common.bean.udm.EnterpriseApiProperties;
import com.wl4g.dopaas.common.bean.udm.model.XCloudDocumentModel;
import com.wl4g.dopaas.udm.model.Rap1Model;
import com.wl4g.dopaas.udm.model.Rap1ModelJson;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Rap1DocumentConverter}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-16
 * @sine v1.0
 * @see
 */
public class Rap1DocumentConverter extends AbstractDocumentConverter<Rap1Model> {
	@Override
	public XCloudDocumentModel convertFrom(String documentJson) {

		if (documentJson.startsWith("http")) {
			// http://rap.anjiancloud.repo/workspace/myWorkspace.do?projectId=29#2099

			String url = documentJson;
			if (!documentJson.contains("queryRAPModel")) {
				String prefix = "";
				if (documentJson.startsWith("https")) {
					prefix = "https://";
				} else {
					prefix = "http://";
				}

				documentJson = documentJson.replaceAll("http://", "");
				documentJson = documentJson.replaceAll("https://", "");

				String domain = documentJson.substring(0, documentJson.indexOf("/"));

				String args = documentJson.substring(documentJson.indexOf("projectId=") + 10);
				if (args.contains("#")) {
					args = args.substring(0, args.indexOf("#"));
				}

				if (args.contains("&")) {
					args = args.substring(0, args.indexOf("&"));
				}
				url = prefix + domain + "/api/queryRAPModel.do?projectId=" + args;
			}

			Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
			factory.setReadTimeout(10000);
			factory.setConnectTimeout(6000);
			factory.setMaxResponseSize(65535);
			// factory.setSslContext(sslContext);
			RestTemplate restTemplate = new RestTemplate(factory);
			documentJson = restTemplate.getForObject(url, String.class);
		}

		Rap1ModelJson rap1ModelJson = JacksonUtils.parseJSON(documentJson, Rap1ModelJson.class);
		Assert2.notNullOf(rap1ModelJson, "rap1ModelJson");
		rap1ModelJson.setModelJSON(rap1ModelJson.getModelJSON().replaceAll("\\\\'", "'"));
		Rap1Model rap1Model = JacksonUtils.parseJSON(rap1ModelJson.getModelJSON(), Rap1Model.class);
		return convertFrom(rap1Model);
	}

	@Override
	public ConverterProviderKind kind() {
		return ConverterProviderKind.RAP1;
	}

	@Override
	public XCloudDocumentModel convertFrom(Rap1Model document) {
		// TODO Auto-generated method stub
		List<EnterpriseApi> enterpriseApis = new ArrayList<>();
		List<Rap1Model.Model> moduleList = document.getModuleList();
		for (Rap1Model.Model model : moduleList) {
			List<Rap1Model.Action> actionList = model.getActionList();
			if (CollectionUtils.isEmpty(actionList)) {
				actionList = new ArrayList<>();
			}

			if (!CollectionUtils.isEmpty(model.getPageList())) {
				for (Rap1Model.Model page : model.getPageList()) {
					actionList.addAll(page.getActionList());
				}
			}
			if (CollectionUtils.isEmpty(actionList)) {
				continue;
			}

			for (Rap1Model.Action action : actionList) {
				EnterpriseApi enterpriseApi = new EnterpriseApi();
				enterpriseApi.setDescription(action.getDescription());
				enterpriseApi.setName(action.getName());
				enterpriseApi.setMethod(action.getRequestType());
				enterpriseApi.setUrl(action.getRequestUrl());

				List<EnterpriseApiProperties> properties = new ArrayList<>();
				List<Rap1Model.Parameter> requestParameterList = action.getRequestParameterList();
				if (!CollectionUtils.isEmpty(requestParameterList)) {
					buildProperties(requestParameterList, properties, REQUEST);
				}
				List<Rap1Model.Parameter> responseParameterList = action.getResponseParameterList();
				if (!CollectionUtils.isEmpty(responseParameterList)) {
					buildProperties(responseParameterList, properties, RESPONSE);
				}

				enterpriseApi.setProperties(properties);
				enterpriseApis.add(enterpriseApi);
			}
		}
		return new XCloudDocumentModel(enterpriseApis);
	}

	private void buildProperties(List<Rap1Model.Parameter> requestParameterList, List<EnterpriseApiProperties> properties,
			String scope) {
		for (Rap1Model.Parameter parameter : requestParameterList) {
			EnterpriseApiProperties enterpriseApiProperties = new EnterpriseApiProperties();

			enterpriseApiProperties.setName(parameter.getIdentifier());
			enterpriseApiProperties.setDescription(parameter.getName());
			enterpriseApiProperties.setType(parameter.getDataType());
			enterpriseApiProperties.setRule(parameter.getValidator());
			enterpriseApiProperties.setScope(scope);

			List<Rap1Model.Parameter> children = parameter.getParameterList();
			if (!CollectionUtils.isEmpty(children)) {
				enterpriseApiProperties.setChildren(new ArrayList<>());
				buildProperties(children, enterpriseApiProperties.getChildren(), scope);
			}
			properties.add(enterpriseApiProperties);
		}
	}

	@Override
	public Rap1Model convertTo(XCloudDocumentModel document) {
		// TODO Auto-generated method stub
		return null;
	}

}
