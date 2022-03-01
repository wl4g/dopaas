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

import static com.github.rholder.retry.StopStrategies.neverStop;
import static com.github.rholder.retry.WaitStrategies.randomWait;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Collection;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotNull;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.wl4g.dopaas.common.bean.ucm.model.ReportChangedRequest.ChangedRecord;
import com.wl4g.dopaas.ucm.client.event.CheckpointConfigEvent;
import com.wl4g.dopaas.ucm.client.event.ConfigEventListener;
import com.wl4g.dopaas.ucm.client.event.RefreshConfigEvent;
import com.wl4g.infra.common.log.SmartLogger;

/**
 * {@link ReportingConfigListener}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-21
 * @since
 */
public class ReportingConfigListener implements ConfigEventListener {

    final protected SmartLogger log = getLogger(getClass());

    /** {@link RefreshWatcher} */
    protected final AbstractRefreshWatcher<? extends AbstractUcmClientConfig<?>> watcher;

    /** UCM client reporting handler */
    protected final ConfigReportingHandler handler;

    public ReportingConfigListener(@NotNull AbstractRefreshWatcher<? extends AbstractUcmClientConfig<?>> watcher) {
        this.watcher = notNullOf(watcher, "watcher");
        this.handler = new ConfigReportingHandler();
    }

    @Override
    public void onRefresh(RefreshConfigEvent event) {
        // Ignore
    }

    @Override
    public void onCheckpoint(CheckpointConfigEvent event) {
        handleRetrievableReporting();
    }

    /**
     * DO retrievable execution reporting
     */
    protected void handleRetrievableReporting() {
        if (!watcher.getRecorder().getChangedAll().isEmpty()) {
            log.info("UCM retrievable reporting ...");
            try {
                newReportingRetryer().call(handler);
            } catch (Exception e) {
                log.error("Failed to UCM reporting.", e);
            }
        }
    }

    /**
     * New create reporting {@link Retryer}
     * 
     * @return
     */
    protected Retryer<Boolean> newReportingRetryer() {
        AbstractUcmClientConfig<?> config = watcher.getUcmConfig();
        return RetryerBuilder.<Boolean> newBuilder()
                .retryIfExceptionOfType(Throwable.class)// Exception-retry-source
                .retryIfResult(res -> (nonNull(res) && !res)) // Retrial-condition
                .withWaitStrategy(randomWait(config.getRetryReportingMinDelay(), MILLISECONDS, config.getRetryReportingMaxDelay(),
                        MILLISECONDS)) // Waiting-interval
                .withStopStrategy(neverStop()) // stop-retries
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        // Discard/cleanup after maximum attempt.(if necessary)
                        long threshold = config.getRetryReportingFastFailThreshold();
                        if (threshold > 0 && attempt.getAttemptNumber() > threshold) {
                            String errmsg = format("Reporting retries max threshold(%s), discarded refresh changed record!!!",
                                    attempt.getAttemptNumber());
                            // throw new ReportRetriesCountOutException(errmsg);
                            log.warn(errmsg);
                            watcher.getRecorder().pollAll();
                        }
                        // Publishing reporting
                        watcher.getPublisher().publishReportingEvent(attempt);
                    }
                })
                .build();
    }

    /**
     * {@link ConfigReportingHandler}
     *
     * @since
     */
    class ConfigReportingHandler implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            Collection<ChangedRecord> records = watcher.getRecorder().getChangedAll();
            boolean result = watcher.doReporting(records);
            if (result) { // Success and cleanup
                records = watcher.getRecorder().pollAll();
                log.debug("Reporting success and cleaned for records: {}", records);
            }
            return result;
        }
    }

}