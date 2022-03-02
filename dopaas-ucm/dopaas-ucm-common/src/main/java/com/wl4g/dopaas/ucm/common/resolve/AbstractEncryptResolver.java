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
package com.wl4g.dopaas.ucm.common.resolve;

import static com.wl4g.dopaas.ucm.common.UCMConstants.KEY_CIPHER_PREFIX;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.isCompatibleType;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;
import com.wl4g.dopaas.ucm.common.exception.UcmException;
import com.wl4g.infra.common.bean.KeyValue;
import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.infra.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.infra.common.log.SmartLogger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Generic origin base property source.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class AbstractEncryptResolver implements UcmEncryptResolver {
    private static final long serialVersionUID = -5037062685017411482L;

    protected final SmartLogger log = getLogger(getClass());

    @Override
    public ConfigSource resolve(ConfigSource source) {
        log.debug("Resolving release configuration cipher source ...");
        return doResolve(source);
    }

    /**
     * DO read & resolving property source.
     * 
     * @param source
     * @return
     */
    protected abstract ConfigSource doResolve(ConfigSource source);

    /**
     * Resolving cipher release source property value.
     * 
     * @param key
     * @param value
     * @return
     */
    protected String decryptCipherProperty(KeyValue kv) {
        String key = kv.getKey();
        String value = kv.getValue();
        if (!isBlank(key) && key.startsWith(KEY_CIPHER_PREFIX)) {
            try {
                // TODO using dynamic cipherKey??
                byte[] cipherKey = AES128ECBPKCS5.getEnvCipherKey("DEVOPS_CIPHER_KEY");
                String cipherText = value.substring(KEY_CIPHER_PREFIX.length());

                // TODO fromHex()??
                String plainVal = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(cipherText)).toString();
                log.debug("Decryption property key: {}, cipherText: {}, plainText: {}", key, value, plainVal);

                return plainVal;
            } catch (Exception e) {
                throw new UcmException(format("Cannot decrypt cipher property. '%s' -> '%s'", key, value), e);
            }
        }
        return value;
    }

    /**
     * Resolving hierarchy cipher all properties.
     * 
     * @param jsonMap
     */
    @SuppressWarnings({ "unchecked" })
    public static void resolveHierarchyCipherProperty(Map<String, Object> jsonMap, Function<KeyValue, String> resolver) {
        jsonMap.forEach((key, value) -> {
            if (isNull(value)) {
                return;
            }
            Class<?> cls = value.getClass();
            if (isCompatibleType(cls, String.class)) {
                resolver.apply(new KeyValue(key, value));
            } else if (isCompatibleType(cls, Map.class)) {
                resolveHierarchyCipherProperty((Map<String, Object>) value, resolver);
            } else if (isCompatibleType(cls, List.class)) {
                ((List<Object>) value).forEach(e -> {
                    if (isCompatibleType(e.getClass(), Map.class)) {
                        resolveHierarchyCipherProperty((Map<String, Object>) e, resolver);
                    } else {
                        resolver.apply(new KeyValue(key, (String) e));
                    }
                });
            } else if (cls.isArray()) {
                for (Object val : ((Object[]) value)) {
                    if (isCompatibleType(cls, String.class)) {
                        resolver.apply(new KeyValue(key, (String) val));
                    }
                }
            } else {
                resolveHierarchyCipherProperty((Map<String, Object>) value, resolver);
            }
        });
    }

}