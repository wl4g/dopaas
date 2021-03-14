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
package com.wl4g.paas.udm.plugin.swagger.jaxrs2;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.Test;

import com.wl4g.paas.udm.plugin.swagger.jaxrs2.OpenAPISorter;

import static org.junit.Assert.assertEquals;

public class OpenApiSorterTest {

	@Test
	public void testSort() {
        ObjectSchema schema1 = new ObjectSchema();
        schema1.addProperties("s1-2", new StringSchema());
        schema1.addProperties("s1-1", new StringSchema());

        ObjectSchema schema2 = new ObjectSchema();
        schema2.addProperties("s2-2", new StringSchema());
        schema2.addProperties("s2-1", new StringSchema());

        Components components = new Components()
                .addSchemas("s2", schema2)
                .addSchemas("s1", schema1)
                .addResponses("k2", new ApiResponse())
                .addResponses("k1", new ApiResponse())
                .addParameters("k2", new Parameter())
                .addParameters("k1", new Parameter())
                .addExamples("k2", new Example())
                .addExamples("k1", new Example())
                .addRequestBodies("k2", new RequestBody())
                .addRequestBodies("k1", new RequestBody())
                .addHeaders("k2", new Header())
                .addHeaders("k1", new Header())
                .addSecuritySchemes("k2", new SecurityScheme())
                .addSecuritySchemes("k1", new SecurityScheme())
                .addLinks("k2", new Link())
                .addLinks("k1", new Link())
                .addCallbacks("k2", new Callback())
                .addCallbacks("k1", new Callback());

        Paths paths = new Paths()
                .addPathItem("p2", new PathItem())
                .addPathItem("p1", new PathItem());

        OpenAPI api = new OpenAPI()
                .components(components)
                .paths(paths);

        api = OpenAPISorter.sort(api);

        assertEquals("s1-1", api.getComponents().getSchemas()
                .values().stream()
                .findFirst().get().getProperties()
                .keySet().stream().findFirst().get());
        assertEquals("s2-1", api.getComponents().getSchemas()
                .values().stream()
                .skip(1).findFirst().get().getProperties()
                .keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getResponses().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getParameters().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getExamples().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getRequestBodies().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getHeaders().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getSecuritySchemes().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getLinks().keySet().stream().findFirst().get());
        assertEquals("k1", api.getComponents().getCallbacks().keySet().stream().findFirst().get());
        assertEquals("p1", api.getPaths().keySet().stream().findFirst().get());
	}
	
}