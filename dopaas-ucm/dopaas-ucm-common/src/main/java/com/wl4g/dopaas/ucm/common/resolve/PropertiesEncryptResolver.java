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

import static com.google.common.base.Charsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;

import lombok.Getter;

/**
 * {@link PropertiesEncryptResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class PropertiesEncryptResolver extends AbstractEncryptResolver {
    private static final long serialVersionUID = 1755382479743018762L;

    @Override
    public ConfigSource doResolve(ConfigSource source) {
        Properties props = new Properties();
        try {
            props.load(new ByteArrayInputStream(source.getText().getBytes(UTF_8)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        Map<String, Object> propsMap = new HashMap<>();
        props.forEach((k, v) -> propsMap.put((String) k, v));
        resolveHierarchyCipherProperty(propsMap, kv -> decryptCipherProperty(kv));
        return new ConfigSource(source.getProfile(), props.toString());
    }

}