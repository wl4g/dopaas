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

import java.io.CharArrayWriter;
import java.util.Map;

import org.springframework.boot.json.YamlJsonParser;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;

import lombok.Getter;

/**
 * {@link YamlEncryptResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class YamlEncryptResolver extends AbstractEncryptResolver {
    private static final long serialVersionUID = -4793468560178245882L;

    @Override
    public ConfigSource doResolve(ConfigSource source) {
        Map<String, Object> yamlMap = new YamlJsonParser().parseMap(source.getText());
        resolveHierarchyCipherProperty(yamlMap, kv -> decryptCipherProperty(kv));
        return new ConfigSource(source.getProfile(), toYamlString(yamlMap));
    }

    private String toYamlString(Map<String, Object> yamlMap) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (CharArrayWriter writer = new CharArrayWriter(4 * yamlMap.size());) {
            yaml.dump(yamlMap, writer);
            return new String(writer.toCharArray());
        }
    }

}