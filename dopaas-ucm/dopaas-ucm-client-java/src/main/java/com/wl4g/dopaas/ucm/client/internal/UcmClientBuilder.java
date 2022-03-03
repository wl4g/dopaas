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
package com.wl4g.dopaas.ucm.client.internal;

import static java.util.Objects.nonNull;

import org.apache.commons.beanutils.BeanUtilsBean2;

import com.wl4g.dopaas.ucm.client.configmap.KubernetesConfigmapUcmClient;
import com.wl4g.dopaas.ucm.client.configmap.KubernetesConfigmapUcmClientConfig;
import com.wl4g.dopaas.ucm.client.console.UcmManagementConsole;
import com.wl4g.dopaas.ucm.client.event.ConfigEventListener;
import com.wl4g.dopaas.ucm.client.hlp.HlpUcmClient;
import com.wl4g.dopaas.ucm.client.hlp.HlpUcmClientConfig;
import com.wl4g.dopaas.ucm.client.recorder.ChangedRecorder;
import com.wl4g.dopaas.ucm.client.recorder.InMemoryChangedRecorder;
import com.wl4g.dopaas.ucm.client.rpc.RpcUcmClient;
import com.wl4g.dopaas.ucm.client.rpc.RpcUcmClientConfig;
import com.wl4g.dopaas.ucm.common.exception.UcmException;
import com.wl4g.shell.core.EmbeddedShellServerBuilder;
import com.wl4g.shell.core.handler.EmbeddedShellServer;

/**
 * {@link UcmClientBuilder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
public class UcmClientBuilder {

    /** UCM client configuration */
    private AbstractUcmClientConfig<?> config;

    /** Configuration refreshing {@link ConfigEventListener} */
    private ConfigEventListener[] listeners;

    /** {@link ChangedRecorder} */
    private ChangedRecorder recorder = new InMemoryChangedRecorder();

    /** Enable management console for {@link UcmManagementConsole} */
    private boolean enableManagementConsole = false;

    /** {@link EmbeddedShellServer} */
    private EmbeddedShellServer shellServer;

    /**
     * New create {@link UcmClientBuilder} instance
     * 
     * @return
     */
    public static UcmClientBuilder newBuilder() {
        return new UcmClientBuilder();
    }

    /**
     * Sets UCM client configuration of {@link UcmClientProperties#HLP}
     * 
     * @return
     */
    public UcmClientBuilder withConfig(AbstractUcmClientConfig<?> config) {
        if (nonNull(config)) {
            try {
                BeanUtilsBean2.getInstance().copyProperties(config, this.config);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return this;
    }

    /**
     * Sets UCM configuration refresh listeners.
     * 
     * @return
     */
    public UcmClientBuilder withListeners(ConfigEventListener... listeners) {
        if (nonNull(listeners)) {
            this.listeners = listeners;
        }
        return this;
    }

    /**
     * Sets use refresh configuration source of {@link InMemoryChangedRecorder}
     * 
     * @return
     */
    public UcmClientBuilder useInMemoryConfigStore() {
        this.recorder = new InMemoryChangedRecorder();
        return this;
    }

    /**
     * Enable startup management console.
     * 
     * @return
     */
    public UcmClientBuilder enableManagementConsole() {
        this.enableManagementConsole = true;
        return this;
    }

    /**
     * Build {@link UcmClient} with builder configuration.
     * 
     * @return
     */
    public UcmClient build() {
        UcmClient client = null;

        if (config instanceof HlpUcmClientConfig) {
            client = new HlpUcmClient((HlpUcmClientConfig) config, recorder, listeners);
        } else if (config instanceof RpcUcmClientConfig) {
            client = new RpcUcmClient((RpcUcmClientConfig) config, recorder, listeners);
        } else if (config instanceof KubernetesConfigmapUcmClientConfig) {
            client = new KubernetesConfigmapUcmClient((KubernetesConfigmapUcmClientConfig) config, recorder, listeners);
        } else {
            throw new Error("Shouldn't be here");
        }

        // Start management console
        if (enableManagementConsole) {
            startManagementConsole(((AbstractUcmClient) client).getWatcher());
        }

        return client;
    }

    /**
     * Enable starting management configuration console.
     * 
     * @param watcher
     */
    public void startManagementConsole(RefreshWatcher watcher) {
        try {
            this.shellServer = EmbeddedShellServerBuilder.newBuilder()
                    .withAppName(config.getConsolePrompt())
                    .register(new UcmManagementConsole(watcher))
                    .build();
            this.shellServer.start();
        } catch (Exception e) {
            throw new UcmException(e);
        }
    }

}