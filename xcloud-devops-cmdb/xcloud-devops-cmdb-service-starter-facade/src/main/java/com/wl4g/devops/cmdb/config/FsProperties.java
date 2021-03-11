/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.cmdb.config;

import com.wl4g.devops.common.constant.DocConstants;

public class FsProperties {

    private String basePath;

    private String baseUrl;

    public String getBaseFilePath(){
        return getBasePath()+"/file";
    }

    public String getBaseImgPath(){
        return getBasePath()+"/img";
    }

    public String getBaseFileUrl(){
        return getBaseUrl()+"/fs/downloadFile";
    }

    public String getBaseImgUrl(){
        //return getBaseUrl()+"/fs/downloadImg";
        return DocConstants.SHARE_LINK_BASEURI +"/fs/downloadImg";
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}