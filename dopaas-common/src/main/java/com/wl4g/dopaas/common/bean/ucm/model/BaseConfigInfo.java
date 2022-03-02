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
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static java.lang.String.format;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HostAndPort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link BaseConfigInfo}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-17
 * @since
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseConfigInfo implements Serializable {
    private static final long serialVersionUID = -299157686801700764L;

    /**
     * Data center zone name. for example: "cn-south1-a1"
     */
    @NotNull
    @NotBlank
    private String zone;

    /**
     * Application service name. (cluster name)
     */
    @NotNull
    @NotBlank
    private String cluster;

    /**
     * Configuration version and release info
     */
    private ConfigMeta meta = new ConfigMeta();

    /**
     * Validation
     * 
     * @param validVersion
     * @param validRelease
     */
    public void validate(boolean validVersion, boolean validRelease) {
        hasTextOf(getZone(), "zone");
        hasTextOf(getCluster(), "cluster");
        getMeta().validate(validVersion, validRelease);
    }

    /**
     * {@link ConfigMeta}
     *
     * @since
     */
    public static class ConfigMeta implements Serializable {
        private static final long serialVersionUID = -4826329110329773259L;

        /**
         * Release version required.
         */
        @NotBlank
        @NotNull
        private String version;

        /**
         * Release configuration ID.
         */
        @NotBlank
        @NotNull
        private String releaseId;

        public ConfigMeta() {
            super();
        }

        public ConfigMeta(String releaseId, String version) {
            super();
            this.releaseId = releaseId;
            this.version = version;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            if (!StringUtils.isEmpty(version) && !"NULL".equalsIgnoreCase(version)) {
                this.version = version;
            }
        }

        public String getReleaseId() {
            return releaseId;
        }

        public void setReleaseId(String releaseId) {
            if (!StringUtils.isEmpty(releaseId) && !"NULL".equalsIgnoreCase(releaseId)) {
                this.releaseId = releaseId;
            }
        }

        public String asText() {
            return getReleaseId().concat("@").concat(getVersion());
        }

        @Override
        public String toString() {
            return asText();
        }

        public void validate(boolean validVersion, boolean validReleaseId) {
            if (validVersion) {
                hasTextOf(getVersion(), "versionId");
            }
            if (validReleaseId) {
                hasTextOf(getReleaseId(), "releaseId");
            }
        }

        public static ConfigMeta of(String metaString) {
            if (!StringUtils.isEmpty(metaString) && metaString.contains("@")) {
                String arr[] = String.valueOf(metaString).split("@");
                return new ConfigMeta(arr[0], arr[1]);
            }
            throw new IllegalStateException(format("releaseMetaString: %s", metaString));
        }

    }

    /**
     * {@link ConfigInstance}
     *
     * @since
     */
    public static class ConfigInstance implements Serializable {
        private static final long serialVersionUID = -4826329780329773259L;

        @NotBlank
        @NotNull
        private String host;

        @NotBlank
        @NotNull
        private String serviceId;

        public ConfigInstance() {
            super();
        }

        public ConfigInstance(String host, String serviceId) {
            super();
            this.host = host;
            this.serviceId = serviceId;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            if (!StringUtils.isEmpty(host) && !"NULL".equalsIgnoreCase(host)) {
                this.host = host;
            }
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            if (!StringUtils.isEmpty(serviceId) && !"NULL".equalsIgnoreCase(serviceId)) {
                this.serviceId = serviceId;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((host == null) ? 0 : host.hashCode());
            result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConfigInstance other = (ConfigInstance) obj;
            if (host == null) {
                if (other.host != null)
                    return false;
            } else if (!host.equals(other.host))
                return false;
            if (serviceId == null) {
                if (other.serviceId != null)
                    return false;
            } else if (!serviceId.equals(other.serviceId))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return getHost().concat(":").concat(getServiceId());
        }

        public void validation() {
            notNull(getHost(), "`host` is not allowed to be null.");
            notNull(getServiceId(), "`port` is not allowed to be null.");
            HostAndPort.fromString(toString());
        }

        public static boolean eq(ConfigInstance i1, ConfigInstance i2) {
            return (i1 != null && i2 != null && StringUtils.equals(i1.toString(), i2.toString()));
        }

    }

}