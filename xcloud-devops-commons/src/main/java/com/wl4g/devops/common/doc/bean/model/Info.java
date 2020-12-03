/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.common.doc.bean.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Auto-generated: 2020-12-02 16:4:17
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Getter
@Setter
public class Info {

    private String version;
    private String title;
    private Contact contact;
    private License license;


    @Getter
    @Setter
    public static class Contact {
        private String name;
        private String url;
        private String email;


    }

    @Getter
    @Setter
    public static class License {

    }

}

