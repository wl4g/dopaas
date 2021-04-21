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

package com.wl4g.dopaas.cmdb.shardingspherev4.repository.impl;

import com.wl4g.dopaas.cmdb.shardingspherev4.common.domain.CenterConfigs;
import com.wl4g.dopaas.cmdb.shardingspherev4.common.exception.ShardingSphereUIException;
import com.wl4g.dopaas.cmdb.shardingspherev4.repository.CenterConfigsRepository;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of Center configs repository.
 */
@Repository
public class YamlCenterConfigsRepositoryImpl implements CenterConfigsRepository {

	private final File file;

	public YamlCenterConfigsRepositoryImpl() {
		file = new File(new File(System.getProperty("user.home")), "shardingsphere-ui-configs.yaml");
	}

	@Override
	public CenterConfigs load() {
		if (!file.exists()) {
			return new CenterConfigs();
		}

		try (FileInputStream fileInputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
			return new Yaml(new Constructor(CenterConfigs.class)).loadAs(inputStreamReader, CenterConfigs.class);
		} catch (IOException e) {
			throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "load center config error");
		}

	}

	@Override
	public void save(final CenterConfigs centerConfigs) {
		Yaml yaml = new Yaml();
		try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			bufferedOutputStream.write(yaml.dumpAsMap(centerConfigs).getBytes());
			bufferedOutputStream.flush();
		} catch (IOException e) {
			throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "save center config error");
		}
	}

}
