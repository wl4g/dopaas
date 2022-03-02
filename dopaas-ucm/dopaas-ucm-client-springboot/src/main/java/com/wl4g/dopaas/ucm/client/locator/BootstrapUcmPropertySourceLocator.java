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

import static com.wl4g.dopaas.ucm.client.refresh.UcmContextRefresher.UCM_REFRESH_PROPERTY_SOURCE;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import java.util.Map;

import org.springframework.boot.json.YamlJsonParser;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;
import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.client.internal.AbstractUcmClient;
import com.wl4g.dopaas.ucm.client.internal.UcmClient;
import com.wl4g.dopaas.ucm.client.recorder.ChangedRecorder;
import com.wl4g.dopaas.ucm.client.recorder.ReleasedWrapper;
import com.wl4g.dopaas.ucm.client.refresh.UcmContextRefresher;
import com.wl4g.dopaas.ucm.common.resolve.JsonEncryptResolver;
import com.wl4g.infra.common.log.SmartLogger;

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

    protected final SmartLogger log = getLogger(getClass());

    /** {@link UcmClient} */
    protected final UcmClient client;

    public BootstrapUcmPropertySourceLocator(UcmClient client) {
        this.client = notNullOf(client, "ucmClient");
    }

    /**
     * Composite property sources.</br>
     * See:{@link UcmContextRefresher#addConfigToEnvironment}
     */
    @Override
    public PropertySource<?> locate(Environment environment) {
        log.info("UCM locate config is enabled environment for: {}", environment);

        CompositePropertySource composite = new CompositePropertySource(UCM_REFRESH_PROPERTY_SOURCE);
        if (environment instanceof ConfigurableEnvironment) {
            try {
                // Gets current changed release configuration.
                ReleasedWrapper current = getChangedRecorder().current();

                // Conversion add configuration to spring property source.
                composite = addToCompositePropertySource(current.getRelease(), UCM_REFRESH_PROPERTY_SOURCE);
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
     * Gets {@link ChangeRecorder}
     * 
     * @return
     */
    protected ChangedRecorder getChangedRecorder() {
        return ((AbstractRefreshWatcher<?>) ((AbstractUcmClient) client).getWatcher()).getRecorder();
    }

    /**
     * Conversion {@link ReleaseConfigInfo} to spring
     * {@link CompositePropertySource}
     * 
     * @param release
     * @param compositeSourceName
     * @return
     */
    protected CompositePropertySource addToCompositePropertySource(ReleaseConfigInfo release, String compositeSourceName) {
        CompositePropertySource composite = new CompositePropertySource(compositeSourceName);
        for (ConfigSource cs : release.getSources()) {
            // see:org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
            composite.addFirstPropertySource(toSpringPropertySource(cs));
        }
        return composite;
    }

    private PropertySource<?> toSpringPropertySource(ConfigSource cs) {
        if (equalsAnyIgnoreCase(cs.getProfile().getType(), "YAML", "YML")) {
            Map<String, Object> yamlMap = new YamlJsonParser().parseMap(cs.getText());
            return new MapPropertySource(cs.getProfile().getName(), yamlMap);
        } else if (equalsAnyIgnoreCase(cs.getProfile().getType(), "JSON")) {
            Map<String, Object> jsonMap = parseJSON(cs.getText(), JsonEncryptResolver.DEFAULT_REFTYPE);
            return new MapPropertySource(cs.getProfile().getName(), jsonMap);
        }
        // TODO
        // ...
        return null;
    }

}