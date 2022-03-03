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

import static com.google.common.base.Charsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.google.common.io.Resources;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigProfile;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;
import com.wl4g.dopaas.common.bean.ucm.model.ReportChangedRequest.ChangedRecord;
import com.wl4g.dopaas.ucm.client.event.ConfigEventListener;
import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.client.recorder.ChangedRecorder;
import com.wl4g.dopaas.ucm.client.recorder.ReleasedWrapper;
import com.wl4g.infra.common.annotation.Reserved;
import com.wl4g.infra.common.io.FileEventWatcher;
import com.wl4g.infra.common.io.FileEventWatcher.FileChangedEvent;
import com.wl4g.infra.common.task.RunnerProperties;
import com.wl4g.infra.common.task.RunnerProperties.StartupMode;

/**
 * {@link KubernetesConfigmapRefreshWatcher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
@Reserved
public class KubernetesConfigmapRefreshWatcher extends AbstractRefreshWatcher<KubernetesConfigmapUcmClientConfig> {

    private FileEventWatcher watcher;

    public KubernetesConfigmapRefreshWatcher(KubernetesConfigmapUcmClientConfig config, ChangedRecorder recorder, ConfigEventListener... listeners) {
        super(new RunnerProperties(StartupMode.ASYNC), config, recorder, listeners);
    }

    @Override
    public boolean doReporting(Collection<ChangedRecord> records) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void startingPropertiesSet() {
        List<File> monitorDirs = config.getConfigDirs().stream().map(f -> new File(f)).collect(toList());
        watcher = new FileEventWatcher(monitorDirs);
        watcher.addListenrs(new FileChangedEventListener());
    }

    @Override
    protected void closingPropertiesSet() throws IOException {
        watcher.close();
    }

    @Override
    public void run() {
        watcher.run();
    }

    class FileChangedEventListener {
        @Subscribe
        public void onChanged(FileChangedEvent event) {
            log.debug("changed: {} -> {}", event.getEventType(), event.getSource());
            try {
                String filename = event.getSource().getFileName().toString();
                String filetype = StringUtils.getFilenameExtension(filename);
                String changedText = Resources.toString(event.getSource().toUri().toURL(), UTF_8);

                ReleaseConfigInfo release = new ReleaseConfigInfo();
                release.getSources().add(new ConfigSource(new ConfigProfile(filetype, filename), changedText));

                // TODO
                getPublisher().publishRefreshEvent(new ReleasedWrapper(release, null));
            } catch (IOException e) {
                log.error("Failed to process changed configuration.", e);
            }
        }
    }

}