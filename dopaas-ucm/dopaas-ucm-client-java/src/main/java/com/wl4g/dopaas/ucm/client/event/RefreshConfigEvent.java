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
package com.wl4g.dopaas.ucm.client.event;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.nonNull;

import java.util.Set;

import javax.annotation.Nullable;

import com.wl4g.dopaas.ucm.client.event.RefreshConfigEvent.RefreshContext;
import com.wl4g.dopaas.ucm.client.recorder.ChangedRecorder;
import com.wl4g.dopaas.ucm.client.recorder.ReleasedWrapper;

/**
 * {@link RefreshConfigEvent}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-18
 * @since
 */
public class RefreshConfigEvent extends GenericUcmEvent<RefreshContext> {
    private static final long serialVersionUID = 1026288899828948496L;

    public RefreshConfigEvent(RefreshContext context) {
        super(context);
    }

    /**
     * {@link RefreshContext}
     *
     * @since
     */
    public static class RefreshContext {

        /** {@link ReleaseConfigInfo} */
        private final ReleasedWrapper release;

        /** {@link ChangedRecorder} */
        protected final ChangedRecorder recorder;

        public RefreshContext(ReleasedWrapper release, ChangedRecorder recorder) {
            this.release = notNullOf(release, "release");
            this.recorder = notNullOf(recorder, "recorder");
        }

        /**
         * Commit changed property config keys.
         * 
         * @param changedKeys
         * @param source
         */
        public void commitChanged(@Nullable Set<String> changedKeys) {
            if (nonNull(changedKeys)) {
                recorder.save(changedKeys, release);
            }
        }

    }

}