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
package com.wl4g.dopaas.uci.analyses.tasks;

import static java.util.Objects.isNull;
import static org.springframework.util.Assert.isTrue;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.component.common.task.RunnerProperties;
import com.wl4g.component.common.task.RunnerProperties.StartupMode;
import com.wl4g.component.support.task.ApplicationTaskRunner;

/**
 * Default Codes analyzing task manager.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月19日
 * @since
 */
public final class DefaultTaskManager extends ApplicationTaskRunner<RunnerProperties> implements TaskManager {
    final private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Codes task repository.
     */
    final private ConcurrentMap<String, Future<File>> taskRepository;

    public DefaultTaskManager(int initCapacity) {
        super(new RunnerProperties(StartupMode.ASYNC));
        isTrue(initCapacity > 0, "initCapacity must >0");
        this.taskRepository = new ConcurrentHashMap<>(initCapacity);
    }

    @Override
    public boolean registerFuture(String taskId, Future<File> future) {
        if (log.isInfoEnabled()) {
            log.info("Add codesAnalyzing task future for: {}", future);
        }
        return isNull(taskRepository.putIfAbsent(taskId, future));
    }

    @Override
    public void run() {

    }

}