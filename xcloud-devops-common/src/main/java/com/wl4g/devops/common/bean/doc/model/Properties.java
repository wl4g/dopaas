/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.common.bean.doc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Auto-generated: 2020-12-02 16:4:17
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Getter
@Setter
public class Properties {

    private Map<String, Propertie> properties;

    @Getter
    @Setter
    public static class Propertie{
         private String type;
         private String format;
         private Boolean readOnly;
         private Propertie.Items items;


        @Getter
        @Setter
        public static class Items {
            private String $ref;
            public void set$ref(String $ref) {
                this.$ref = $ref;
            }
            public String get$ref() {
                return $ref;
            }

        }
     }


}