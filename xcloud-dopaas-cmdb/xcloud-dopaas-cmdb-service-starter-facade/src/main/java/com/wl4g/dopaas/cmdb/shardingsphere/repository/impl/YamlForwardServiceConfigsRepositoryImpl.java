/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.dopaas.cmdb.shardingsphere.repository.impl;

import com.wl4g.dopaas.cmdb.shardingsphere.common.domain.ForwardServiceConfigs;
import com.wl4g.dopaas.cmdb.shardingsphere.common.exception.ShardingSphereUIException;
import com.wl4g.dopaas.cmdb.shardingsphere.repository.ForwardServiceConfigsRepository;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of forward service configs repository.
 */
@Repository
public class YamlForwardServiceConfigsRepositoryImpl implements ForwardServiceConfigsRepository {
    
    private final File file;
    
    public YamlForwardServiceConfigsRepositoryImpl() {
        file = new File(new File(System.getProperty("user.home")), "shardingsphere-ui-forward-services.yaml");
    }
    
    @Override
    public ForwardServiceConfigs load() {
        if (!file.exists()) {
            return new ForwardServiceConfigs();
        }
        try (FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
            return new Yaml(new Constructor(ForwardServiceConfigs.class)).loadAs(inputStreamReader, ForwardServiceConfigs.class);
        } catch (IOException e) {
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "load forward services config error");
        }
    }
    
    @Override
    public void save(final ForwardServiceConfigs forwardServiceConfigs) {
        Yaml yaml = new Yaml();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            bufferedOutputStream.write(yaml.dumpAsMap(forwardServiceConfigs).getBytes());
            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "save registry config error");
        }
    }
}
