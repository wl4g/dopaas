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
package com.wl4g.dopaas.common.bean.cmdb;

import com.wl4g.infra.core.bean.BaseBean;

public class K8sInstance extends BaseBean {

    private static final long serialVersionUID = -7546448616357790576L;

    private Long k8sId;
    private Long hostId;

    public Long getK8sId() {
        return k8sId;
    }

    public void setK8sId(Long k8sId) {
        this.k8sId = k8sId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}