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
package com.wl4g.devops.gateway.server.task;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.log.SmartLoggerFactory;
import com.wl4g.devops.components.tools.common.task.GenericTaskRunner;
import com.wl4g.devops.components.tools.common.task.RunnerProperties;
import com.wl4g.devops.gateway.server.route.IRouteCacheRefresh;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.TimeUnit;

/**
 * Application generic local scheduler & task runner.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @see {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
 * @see <a href=
 * "http://www.doc88.com/p-3922316178617.html">ScheduledThreadPoolExecutor
 * Retry task OOM resolution</a>
 * @since
 */
public class ApplicationTaskRunner<C extends RunnerProperties> extends GenericTaskRunner<C>
        implements ApplicationRunner, DisposableBean {

    @Autowired
    private IRouteCacheRefresh iRouteCacheRefresh;

    final private SmartLogger log = SmartLoggerFactory.getLogger(getClass());

    public ApplicationTaskRunner() {
        super();
    }

    public ApplicationTaskRunner(C config) {
        super(config);
    }

    @Override
    public void destroy() throws Exception {
        super.close();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        super.start();
    }

    @Override
    public void run() {
        getWorker().scheduleWithFixedDelay(() -> {
            try {
                log.info("flushRoutesPermanentToMemery");
                iRouteCacheRefresh.flushRoutesPermanentToMemery();
            }catch (Exception e){
                log.error("fresh fail", e);
            }
        }, 1, 50, TimeUnit.SECONDS);
    }
}