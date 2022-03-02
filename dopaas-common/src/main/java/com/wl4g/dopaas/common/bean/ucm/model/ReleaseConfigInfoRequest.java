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
package com.wl4g.dopaas.common.bean.ucm.model;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigProfile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link ReleaseConfigInfoRequest}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-17
 * @since
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReleaseConfigInfoRequest extends BaseConfigInfo {
    private static final long serialVersionUID = -4016863811283064989L;

    /**
     * Configuration files. (like spring.profiles)
     */
    @NotNull
    @NotEmpty
    private List<ConfigProfile> profiles = new ArrayList<>(2);

    /**
     * {@link ConfigInstance} of current myself SCM client application.
     */
    @NotNull
    private ConfigInstance instance;

    @Override
    public void validate(boolean versionValidate, boolean releaseValidate) {
        super.validate(versionValidate, releaseValidate);
        notNullOf(getInstance(), "configNode");
        getInstance().validation();
        notEmptyOf(getProfiles(), "profiles");
        safeList(getProfiles()).stream().forEach(p -> p.validate());
    }

}