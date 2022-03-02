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
package com.wl4g.dopaas.ucm.client.recorder;

import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;

import lombok.Getter;
import lombok.ToString;

/**
 * {@link ReleasedWrapper}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-28
 * @since
 */
@Getter
@ToString
public class ReleasedWrapper {

    /**
     * Origin release of {@link ReleaseConfigInfo}
     */
    private final ReleaseConfigInfo release;

    /** Resolved release configuration sources */
    @NotNull
    @NotEmpty
    private List<ConfigSource> resolvedSources = new ArrayList<>(1);

    public ReleasedWrapper(@NotNull ReleaseConfigInfo release, @NotEmpty List<ConfigSource> sources) {
        this.release = notNullOf(release, "release");
        this.resolvedSources = notEmptyOf(sources, "sources");
    }

}