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
package com.wl4g.dopaas.ucm.client.configmap;

import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.util.ArrayList;
import java.util.List;

import com.wl4g.dopaas.ucm.client.internal.AbstractUcmClientConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * Based on Kubernetes ConfigMap change event UCM client properties.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月3日
 * @since
 */
@Getter
@Setter
public class ConfigmapUcmClientConfig extends AbstractUcmClientConfig<ConfigmapUcmClientConfig> {
    private static final long serialVersionUID = -2133451846066162424L;

    private List<String> configDirs = new ArrayList<String>() {
        private static final long serialVersionUID = 6295803107322975801L;
        {
            add(DEF_CONFIG_DIR);
        }
    };

    public static final String DEF_CONFIG_DIR = USER_DIR.concat("../conf/");

}