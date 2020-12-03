/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.common.bean.doc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Auto-generated: 2020-12-02 16:4:17
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Getter
@Setter
public class Path {

    private List<String> tags;
    private String summary;
    private String operationId;
    private List<String> consumes;
    private List<String> produces;
    private List<Parameters> parameters;
    private Map<String, Responses> responses;
    private boolean deprecated;
    @JsonProperty("x-order")
    private String xOrder;



    @Getter
    @Setter
    public static class Parameters {

        private String in;
        private String name;
        private String description;
        private boolean required;
        private Schema schema;


        //TODO
        public static class Schema {
            private String $ref;


        }
    }

    @Getter
    @Setter
    public static class Responses {
        private String description;
        private Parameters.Schema schema;
    }

}