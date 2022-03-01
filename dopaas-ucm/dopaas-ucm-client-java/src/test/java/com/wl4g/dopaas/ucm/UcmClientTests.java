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
package com.wl4g.dopaas.ucm;

import static java.lang.System.out;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.wl4g.dopaas.ucm.client.configmap.ConfigmapUcmClientConfig;
import com.wl4g.dopaas.ucm.client.event.RefreshConfigEvent.RefreshContext;
import com.wl4g.dopaas.ucm.client.hlp.HlpUcmClientConfig;
import com.wl4g.dopaas.ucm.client.internal.UcmClient;
import com.wl4g.dopaas.ucm.client.internal.UcmClientBuilder;

/**
 * {@link UcmClientTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
public class UcmClientTests {

    @Test
    public void testHlpUcmClientRefresh() throws Exception {
        out.println("UCM hlp client starting ...");

        UcmClient client = UcmClientBuilder.newBuilder()
                .withConfig(new HlpUcmClientConfig().withBaseUri("http://localhost:17030")
                        .withCluster("my-orderservice-web")
                        .withLongPollTimeout(6000L))
                .enableManagementConsole()
                .withListeners(event -> {
                    out.println("On refresh configuration...");
                    RefreshContext context = event.getSource();
                    out.println("Refresh property: " + context.toString());

                    // Do refreshing
                    // ...

                    // add changed keys for testing
                    Set<String> changedKeys = new HashSet<>();
                    changedKeys.add("myconfig.task.threads");

                    // Commit changed.
                    context.commitChanged(changedKeys);
                })
                .build();

        client.start();

        // Exiting & shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Test
    public void testConfigmapUcmClientRefresh() throws Exception {
        out.println("UCM configmap client starting ...");

        UcmClient client = UcmClientBuilder.newBuilder()
                .withConfig(new ConfigmapUcmClientConfig().withCluster("my-orderservice-web"))
                .enableManagementConsole()
                .withListeners(event -> {
                    out.println("On refresh configuration...");
                    RefreshContext context = event.getSource();
                    out.println("Refresh property: " + context.toString());

                    // Do refreshing
                    // ...

                    // add changed keys for testing
                    Set<String> changedKeys = new HashSet<>();
                    changedKeys.add("myconfig.task.threads");

                    // Commit changed.
                    context.commitChanged(changedKeys);
                })
                .build();

        client.start();

        // Exiting & shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

}