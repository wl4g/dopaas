/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.dopaas.lcdp.dbop.service.evaluate;

import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.dopaas.lcdp.dbop.service.evaluate.metadata.MetadataResolver;

/**
 * {@link ClickhouseImageEvaluator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
public class ClickhouseImageEvaluator extends StandardImageEvaluator {

    public ClickhouseImageEvaluator(EvaluatorSpec config, JdbcTemplate jdbcTemplate, MetadataResolver resolver) {
        super(config, jdbcTemplate, resolver);
    }

}
