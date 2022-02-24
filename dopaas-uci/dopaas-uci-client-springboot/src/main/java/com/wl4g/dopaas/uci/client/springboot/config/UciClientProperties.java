/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.client.springboot.config;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.dopaas.common.constant.UciConstants.DEFAULT_META_HEADER_PREFIX;
import static com.wl4g.dopaas.common.constant.UciConstants.DEFAULT_META_NAME;
import static java.lang.String.format;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.springframework.context.ApplicationContext;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link UciClientProperties}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-20
 * @sine v1.0
 * @see
 */
@Getter
@Setter
public class UciClientProperties {

    private String metaHeaderName; // e.g: X-Api-Meta-CmdbFacade

    private File defaultMetaFile;

    public UciClientProperties(@NotNull final ApplicationContext actx) {
        notNullOf(actx, "applicationContext");
        String appName = actx.getEnvironment().getRequiredProperty("spring.application.name");
        applyMetaInfoHeadName(appName);
        applyDefaultMetaFile(appName);
    }

    private void applyMetaInfoHeadName(String appName) {
        setMetaHeaderName(DEFAULT_META_HEADER_PREFIX + toHump(appName));
    }

    private void applyDefaultMetaFile(String appName) {
        setDefaultMetaFile(new File(format("/opt/apps/acm/%s-package/%s-master-bin/%s", appName, appName, DEFAULT_META_NAME)));
    }

    /**
     * <pre>
     *  System.out.println(toCanonical("cmdb-facade")) => CmdbFacade
     * </pre>
     */
    private static String toHump(String appName) {
        StringBuilder buf = new StringBuilder(appName.length());
        boolean lastFound = false;
        for (char ch : appName.substring(1).toCharArray()) {
            if (ch == '_' || ch == '-') {
                lastFound = true;
            } else if (lastFound) {
                buf.append(String.valueOf(ch).toUpperCase());
                lastFound = false;
            } else {
                buf.append(ch);
            }
        }
        return appName.substring(0, 1).toUpperCase().concat(buf.toString());
    }

}
