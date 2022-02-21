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
package com.wl4g.dopaas.lcdp.tools.hbase.util;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.startsWith;

/**
 * {@link CsvUtil}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-02-20 v1.0.0
 * @since v1.0.0
 */
public abstract class CsvUtil {

    public static final String ESCAPE = "\"";

    public static String escapeCsv(String value) {
        if (contains(value, ",") && !(startsWith(value, ESCAPE) && endsWith(value, ESCAPE))) {
            return ESCAPE.concat(value).concat(ESCAPE);
        }
        return value;
    }

    public static byte[] escapeCsv(byte[] value) {
        return escapeCsv(new String(value, UTF_8)).getBytes(UTF_8);
    }

}
