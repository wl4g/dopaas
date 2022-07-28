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
package com.wl4g.dopaas.lcdp.tools.hbase.rdbms.handler;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.Iterator;
import java.util.Map;

import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

/**
 * Oracle11g sql builder
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public class Oracle11gHandler extends RdbmsHandler {

    public Oracle11gHandler(CommandLineFacade line) {
        super(line);
    }

    @Override
    public String buildInsertSQLs(Map<String, String> fields) {
        StringBuffer sql = new StringBuffer("INSERT INTO ");
        sql.append("`");
        sql.append(getTableName());
        sql.append("`(");
        Iterator<String> itk = fields.keySet().iterator();
        while (itk.hasNext()) {
            String field = itk.next();
            sql.append("`");
            sql.append(field);
            sql.append("`");
            if (itk.hasNext()) {
                sql.append(",");
            }
        }
        sql.append(")VALUES(");
        Iterator<String> itv = fields.values().iterator();
        while (itv.hasNext()) {
            String value = itv.next();
            boolean isNumber = isNumeric(value);
            if (!isNumber) {
                sql.append("'");
            }
            sql.append(value);
            if (!isNumber) {
                sql.append("'");
            }
            if (itv.hasNext()) {
                sql.append(",");
            }
        }
        sql.append(");");
        return sql.toString();
    }

    @Override
    public String getDriverClass() {
        return "oracle.jdbc.driver.OracleDriver";
    }

}