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
package com.wl4g.dopaas.ucm.client.locator;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.dopaas.ucm.client.internal.AbstractUcmClient;
import com.wl4g.dopaas.ucm.client.internal.UcmClient;
import com.wl4g.dopaas.ucm.client.config.UcmClientProperties;
import com.wl4g.dopaas.ucm.client.repository.RefreshRecordsRepository;
import com.wl4g.dopaas.ucm.client.utils.NodeHolder;
import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.common.model.ReleaseConfigInfo;
import com.wl4g.dopaas.ucm.common.model.ReleaseConfigInfo.IniPropertySource;
import com.wl4g.dopaas.ucm.common.model.ReleaseConfigInfo.PlaintextPropertySource;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.dopaas.ucm.client.refresh.UcmContextRefresher.UCM_REFRESH_PROPERTY_SOURCE;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

/**
 * UCM application context initializer instructions.</br>
 * See:https://blog.csdn.net/leileibest_437147623/article/details/81074174
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月22日
 * @since
 */
@Order(0)
public class BootstrapUcmPropertySourceLocator implements PropertySourceLocator {

    final protected SmartLogger log = getLogger(getClass());

    /** {@link UcmClient} */
    protected final UcmClient client;

    public BootstrapUcmPropertySourceLocator(UcmClient client) {
        notNullOf(client, "ucmClient");
        this.client = client;
    }

    /**
     * Composite property sources.</br>
     * See:{@link UcmContextRefresher#addConfigToEnvironment}
     */
    @Override
    public PropertySource<?> locate(Environment environment) {
        log.info("UCM locate config is enabled environment for: {}", environment);

        CompositePropertySource composite = new CompositePropertySource(UCM_REFRESH_PROPERTY_SOURCE); // By-default
        if (environment instanceof ConfigurableEnvironment) {
            try {
                // Gets current refresh config source
                ReleaseConfigInfo source = getRefreshRepository().getCurrentReleaseSource();

                // Conversion configuration to spring property source.
                composite = convertToCompositePropertySource(source, UCM_REFRESH_PROPERTY_SOURCE);

            } catch (Throwable th) {
                String errtip = "Could not locate remote propertySource! causes by: {}";
                if (log.isDebugEnabled()) {
                    log.warn(errtip, getStackTrace(th));
                } else {
                    log.warn(errtip, getRootCausesString(th));
                }
            }
        }

        return composite;
    }

    /**
     * Gets {@link RefreshRecordsRepository}
     * 
     * @return
     */
    protected RefreshRecordsRepository getRefreshRepository() {
        return ((AbstractRefreshWatcher) ((GenericUcmClient) client).getWatcher()).getRepository();
    }

    /**
     * Conversion {@link ReleaseConfigInfo} to spring
     * {@link CompositePropertySource}
     * 
     * @param source
     * @param compositeSourceName
     * @return
     */
    protected CompositePropertySource convertToCompositePropertySource(ReleaseConfigInfo source, String compositeSourceName) {
        CompositePropertySource composite = new CompositePropertySource(compositeSourceName);
        for (PlaintextPropertySource ps : source.getReleases()) {
            // See:org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
            composite.addFirstPropertySource(ps.convertMapPropertySource());
        }
        return composite;
    }

}