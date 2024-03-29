// Generated by DoPaaS for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.dopaas.common.bean.udm;

import com.wl4g.component.core.bean.BaseBean;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * {@link EnterpriseApi}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date Dec 14, 2020
 * @since v1.0
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EnterpriseApi extends BaseBean {
    private static final long serialVersionUID = 519730850945914176L;

    /**
     * 
     */
    @NotNull
    private Long moduleId;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private String method = "";

    /**
     * 
     */
    private String bodyOption;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Long priority;

    /**
     * 
     */
    private Long status;

    /**
     * 
     */
    private Long lockerid;

    /**
     * 
     */
    private String locker;

    private List<EnterpriseApiProperties> properties;

    public EnterpriseApi() {
    }

    public EnterpriseApi withModuleId(Long moduleId) {
        setModuleId(moduleId);
        return this;
    }

    public EnterpriseApi withName(String name) {
        setName(name);
        return this;
    }

    public EnterpriseApi withUrl(String url) {
        setUrl(url);
        return this;
    }

    public EnterpriseApi withMethod(String method) {
        setMethod(method);
        return this;
    }

    public EnterpriseApi withBodyOption(String bodyOption) {
        setBodyOption(bodyOption);
        return this;
    }

    public EnterpriseApi withDescription(String description) {
        setDescription(description);
        return this;
    }

    public EnterpriseApi withPriority(Long priority) {
        setPriority(priority);
        return this;
    }

    public EnterpriseApi withStatus(Long status) {
        setStatus(status);
        return this;
    }

    public EnterpriseApi withLockerid(Long lockerid) {
        setLockerid(lockerid);
        return this;
    }

    public EnterpriseApi withLocker(String locker) {
        setLocker(locker);
        return this;
    }


}