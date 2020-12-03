/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.common.doc.bean.model;

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
public class ApiModel {
    private String swagger;
    private Info info;
    private String host;
    private String basePath;
    private List<Tags> tags;
    private Map<String, Map<String,Path>> paths;
    private Map<String, Definitions> definitions;

}