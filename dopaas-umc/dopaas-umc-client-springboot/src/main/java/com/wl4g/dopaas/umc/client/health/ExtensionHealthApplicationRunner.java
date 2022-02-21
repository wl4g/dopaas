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
package com.wl4g.dopaas.umc.client.health;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.component.common.log.SmartLogger;

/**
 * Compound health monitoring processor.<br/>
 * Note: if you change it into an internal class `@Component`, it doesn't seem
 * to work.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
public class ExtensionHealthApplicationRunner implements ApplicationRunner, DisposableBean {
    private static final long ACQ = 4_000L;

    private final SmartLogger log = getLogger(getClass());
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<Runnable> tasks = new ArrayList<>();
    private ExecutorService executor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.executor = Executors.newFixedThreadPool(2, (r) -> {
            Thread t = new Thread(r, ExtensionHealthApplicationRunner.class.getSimpleName() + "-" + System.currentTimeMillis());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        });
        doStart();
    }

    public void submit(Runnable task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    private void doStart() {
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException("Already started health indicator executor.");
        }
        log.info("Starting health indicator executor...");
        executor.submit(() -> {
            while (true) {
                try {
                    tasks.forEach((task) -> {
                        try {
                            task.run();
                        } catch (Exception e) {
                            log.error("Execution error.", e);
                        }
                    });
                    Thread.sleep(ACQ);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        log.info("Destroy health indicator executor...");
        if (running.compareAndSet(true, false)) {
            executor.shutdownNow();
        } else {
            log.warn("No startup health indicator executor.");
        }
    }

}