/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
 */
package com.wl4g.devops.doc.plugin;

import static java.lang.System.out;
import java.io.IOException;

import org.junit.Test;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

/**
 * {@link SwaggerSerializeTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-10
 * @sine v1.0
 * @see
 */
public class SwaggerSerializeTests {
	static String json = "{\"swagger\":\"2.0\",\"info\":{\"description\":\"Api Documentation\",\"version\":\"1.0\",\"title\":\"Api Documentation\",\"termsOfService\":\"urn:tos\",\"contact\":{},\"license\":{\"name\":\"Apache 2.0\",\"url\":\"http://www.apache.org/licenses/LICENSE-2.0\"}},\"host\":\"localhost:8080\",\"basePath\":\"/\",\"tags\":[{\"name\":\"basic-error-controller\",\"description\":\"Basic Error Controller\"}],\"paths\":{\"/error\":{\"get\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingGET\",\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"},\"404\":{\"description\":\"Not Found\"}}},\"head\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingHEAD\",\"consumes\":[\"application/json\"],\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"204\":{\"description\":\"No Content\"},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"}}},\"post\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingPOST\",\"consumes\":[\"application/json\"],\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"201\":{\"description\":\"Created\"},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"},\"404\":{\"description\":\"Not Found\"}}},\"put\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingPUT\",\"consumes\":[\"application/json\"],\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"201\":{\"description\":\"Created\"},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"},\"404\":{\"description\":\"Not Found\"}}},\"delete\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingDELETE\",\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"204\":{\"description\":\"No Content\"},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"}}},\"options\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingOPTIONS\",\"consumes\":[\"application/json\"],\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"204\":{\"description\":\"No Content\"},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"}}},\"patch\":{\"tags\":[\"basic-error-controller\"],\"summary\":\"errorHtml\",\"operationId\":\"errorHtmlUsingPATCH\",\"consumes\":[\"application/json\"],\"produces\":[\"text/html\"],\"responses\":{\"200\":{\"description\":\"OK\",\"schema\":{\"$ref\":\"#/definitions/ModelAndView\"}},\"204\":{\"description\":\"No Content\"},\"401\":{\"description\":\"Unauthorized\"},\"403\":{\"description\":\"Forbidden\"}}}}},\"definitions\":{\"ModelAndView\":{\"type\":\"object\",\"properties\":{\"empty\":{\"type\":\"boolean\"},\"model\":{\"type\":\"object\"},\"modelMap\":{\"type\":\"object\",\"additionalProperties\":{\"type\":\"object\"}},\"reference\":{\"type\":\"boolean\"},\"status\":{\"type\":\"string\",\"enum\":[\"ACCEPTED\",\"ALREADY_REPORTED\",\"BAD_GATEWAY\",\"BAD_REQUEST\",\"BANDWIDTH_LIMIT_EXCEEDED\",\"CHECKPOINT\",\"CONFLICT\",\"CONTINUE\",\"CREATED\",\"DESTINATION_LOCKED\",\"EXPECTATION_FAILED\",\"FAILED_DEPENDENCY\",\"FORBIDDEN\",\"FOUND\",\"GATEWAY_TIMEOUT\",\"GONE\",\"HTTP_VERSION_NOT_SUPPORTED\",\"IM_USED\",\"INSUFFICIENT_SPACE_ON_RESOURCE\",\"INSUFFICIENT_STORAGE\",\"INTERNAL_SERVER_ERROR\",\"I_AM_A_TEAPOT\",\"LENGTH_REQUIRED\",\"LOCKED\",\"LOOP_DETECTED\",\"METHOD_FAILURE\",\"METHOD_NOT_ALLOWED\",\"MOVED_PERMANENTLY\",\"MOVED_TEMPORARILY\",\"MULTIPLE_CHOICES\",\"MULTI_STATUS\",\"NETWORK_AUTHENTICATION_REQUIRED\",\"NON_AUTHORITATIVE_INFORMATION\",\"NOT_ACCEPTABLE\",\"NOT_EXTENDED\",\"NOT_FOUND\",\"NOT_IMPLEMENTED\",\"NOT_MODIFIED\",\"NO_CONTENT\",\"OK\",\"PARTIAL_CONTENT\",\"PAYLOAD_TOO_LARGE\",\"PAYMENT_REQUIRED\",\"PERMANENT_REDIRECT\",\"PRECONDITION_FAILED\",\"PRECONDITION_REQUIRED\",\"PROCESSING\",\"PROXY_AUTHENTICATION_REQUIRED\",\"REQUESTED_RANGE_NOT_SATISFIABLE\",\"REQUEST_ENTITY_TOO_LARGE\",\"REQUEST_HEADER_FIELDS_TOO_LARGE\",\"REQUEST_TIMEOUT\",\"REQUEST_URI_TOO_LONG\",\"RESET_CONTENT\",\"SEE_OTHER\",\"SERVICE_UNAVAILABLE\",\"SWITCHING_PROTOCOLS\",\"TEMPORARY_REDIRECT\",\"TOO_EARLY\",\"TOO_MANY_REQUESTS\",\"UNAUTHORIZED\",\"UNAVAILABLE_FOR_LEGAL_REASONS\",\"UNPROCESSABLE_ENTITY\",\"UNSUPPORTED_MEDIA_TYPE\",\"UPGRADE_REQUIRED\",\"URI_TOO_LONG\",\"USE_PROXY\",\"VARIANT_ALSO_NEGOTIATES\"]},\"view\":{\"$ref\":\"#/definitions/View\"},\"viewName\":{\"type\":\"string\"}},\"title\":\"ModelAndView\"},\"View\":{\"type\":\"object\",\"properties\":{\"contentType\":{\"type\":\"string\"}},\"title\":\"View\"}}}";

	@Test
	public void deserializeSwagger1() throws IOException {
		// out.println(SwaggerParser.class.getProtectionDomain().getCodeSource().getLocation());
		Swagger swagger = new SwaggerParser().parse(json);
		out.println(toJSONString(swagger));
	}

}
