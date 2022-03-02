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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * {@link ReleaseConfigInfo}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-11
 * @since
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseConfigInfo extends BaseConfigInfo {
    private static final long serialVersionUID = -4016863811283064989L;

    /** Release configuration content sources */
    @NotNull
    @NotEmpty
    private List<ConfigSource> sources = new ArrayList<>(1);

    /**
     * Publish configuration to services.
     */
    @NotNull
    @NotEmpty
    private List<ConfigInstance> instances = new ArrayList<>();

    public ReleaseConfigInfo(ReleaseConfigInfo release) {
        setZone(release.getZone());
        setCluster(release.getCluster());
        setMeta(release.getMeta());
        setSources(release.getSources());
        setInstances(release.getInstances());
    }

    @Override
    public void validate(boolean versionValidate, boolean releaseValidate) {
        super.validate(versionValidate, releaseValidate);
        notEmptyOf(getSources(), "sources");
        getSources().stream().forEach(rs -> rs.validate());
    }

    /**
     * {@link ConfigSource}
     *
     * @since
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigSource implements Serializable {
        private static final long serialVersionUID = -1190806923724681557L;

        /** Configuration property source profile. */
        private ConfigProfile profile;

        /** Configuration property source content text. */
        private String text;

        public void validate() {
            notNullOf(getProfile(), "profile");
            getProfile().validate();
            hasTextOf(getText(), "sourceContent");
        }

    }

    /**
     * {@link ConfigProfile}
     *
     * @since
     */
    @Getter
    @Setter
    @Wither
    public static class ConfigProfile implements Serializable {
        private static final long serialVersionUID = -3449133053778594018L;

        /**
         * Configuration property source type. for example: "YAML"
         */
        @NotNull
        @NotBlank
        private String type;

        /**
         * Configuration property source file. for example:"application-pro.yml"
         */
        @NotNull
        @NotBlank
        private String name;

        public ConfigProfile(@NotBlank String type, @NotBlank String name) {
            this.type = type;
            this.name = name;
            validate();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
        }

        public ConfigProfile validate() {
            notNullOf(getType(), "type");
            hasTextOf(getName(), "name");
            return this;
        }

    }

}