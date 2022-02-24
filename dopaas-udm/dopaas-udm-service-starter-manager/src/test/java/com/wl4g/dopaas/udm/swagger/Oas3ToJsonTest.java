package com.wl4g.dopaas.udm.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.infra.common.resource.ResourceUtils2;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.Test;

import java.io.IOException;

/**
 * @author vjay
 * @date 2021-03-01 10:22:00
 */
public class Oas3ToJsonTest {


    @Test
    public void test1() throws IOException {

        String json = ResourceUtils2.getResourceString(Oas3ToJsonTest.class, "oas3.json");

        OpenAPI openAPI = new OpenAPIV3Parser().readContents(json).getOpenAPI();

        ObjectMapper mapper = Json.mapper();
        //mapper.addMixIn(ServerVariable.class, Oas3ServerVariable.ServerVariableMixin.class);

        String s = mapper.writeValueAsString(openAPI);
        System.out.println(s);
    }




}
