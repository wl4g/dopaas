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
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.ucm.common.config.UcmConfigSource;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo;

import lombok.Getter;

/**
 * {@link ReleaseConfigSourceWrapper}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-28
 * @since
 */
@Getter
public class ReleaseConfigSourceWrapper {

    /**
     * Origin release of {@link ReleaseConfigInfo}
     */
    private transient final ReleaseConfigInfo release;

    /**
     * List of {@link UcmConfigSource}
     */
    private final List<UcmConfigSource> sources;

    public ReleaseConfigSourceWrapper(@NotNull ReleaseConfigInfo release, @NotEmpty List<UcmConfigSource> sources) {
        this.release = notNullOf(release, "release");
        this.sources = notEmptyOf(sources, "sources");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
    }

}