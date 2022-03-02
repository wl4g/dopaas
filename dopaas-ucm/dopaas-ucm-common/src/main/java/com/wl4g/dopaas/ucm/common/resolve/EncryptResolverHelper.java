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

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;
import com.wl4g.dopaas.ucm.common.exception.UnknownEncryptResolverException;
import com.wl4g.infra.common.reflect.ObjectInstantiators;

/**
 * {@link EncryptResolverHelper}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-20
 * @sine v1.0.0
 * @see
 */
public class EncryptResolverHelper {

    public ConfigSource resolve(ConfigSource source) {
        Class<? extends UcmEncryptResolver> resolveClass = getPropertySourceOfType(source.getProfile().getType());
        notNull(resolveClass, UnknownEncryptResolverException.class, "Unsupported encrypt resolver of configuration by - %s",
                source.getProfile().getType());
        return ObjectInstantiators.newInstance(resolveClass).resolve(source);
    }

    /**
     * Gets {@link UcmEncryptResolver} class by type.
     * 
     * @param type
     * @return
     */
    private Class<? extends UcmEncryptResolver> getPropertySourceOfType(String type) {
        return PRPERTY_SOURCE_TYPE.entrySet()
                .stream()
                .filter(e -> e.getKey().stream().filter(t -> t.equalsIgnoreCase(type)).findFirst().isPresent())
                .map(e -> e.getValue())
                .findFirst()
                .orElse(null);
    }

    /**
     * Property source definitions of {@link UcmEncryptResolver}
     */
    public static final Map<List<String>, Class<? extends UcmEncryptResolver>> PRPERTY_SOURCE_TYPE = new ConcurrentHashMap<>();

    static {
        PRPERTY_SOURCE_TYPE.put(asList("yaml", "yml"), YamlMapEncryptResolver.class);
        PRPERTY_SOURCE_TYPE.put(asList("properties"), PropertiesEncryptResolver.class);
        PRPERTY_SOURCE_TYPE.put(asList("json"), JsonEncryptResolver.class);
        PRPERTY_SOURCE_TYPE.put(asList("hocon", "conf"), HoconEncryptResolver.class);
        PRPERTY_SOURCE_TYPE.put(asList("ini", "toml"), TomlEncryptResolver.class);
        PRPERTY_SOURCE_TYPE.put(asList("xml"), XmlEncryptResolver.class);
    }

}