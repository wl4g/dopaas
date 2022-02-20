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
package com.wl4g.dopaas.lcdp.tools.hbase.rdbms.mapred;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Counter;

import com.wl4g.dopaas.lcdp.tools.hbase.bulk.mapred.BaseTransformMapper;
import com.wl4g.dopaas.lcdp.tools.hbase.rdbms.SimpleHfileToRdbmsExporter;
import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;

import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.*;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * HBASE to rmdb transform mapper.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class SimpleHfileToRdbmsMapper extends BaseTransformMapper {

    @Override
    public void map(ImmutableBytesWritable key, Result result, Context context) throws IOException, InterruptedException {
        Counter c = context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL);
        c.increment(1);

        LinkedHashMap<String, String> rowdata = new LinkedHashMap<>();
        rowdata.put("row", Bytes.toString(key.get()));

        Iterator<Cell> it = result.listCells().iterator();
        while (it.hasNext()) {
            Cell cell = it.next();
            byte[] qualifier = getCellFieldBytes(cell.getQualifierArray(), cell.getQualifierOffset(),
                    cell.getQualifierLength());
            byte[] value = getCellFieldBytes(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
            String _qualifier = Bytes.toString(qualifier);
            if (!HBaseTools.isIgnoreHbaseQualifier(_qualifier)) {
                rowdata.put(_qualifier, Bytes.toString(value));
            }
        }

        // Insert SQLs.
        try {
            String insertSql = SimpleHfileToRdbmsExporter.currentMigrator.buildInsertSQLs(rowdata);
            if (SimpleHfileToRdbmsExporter.verbose) {
                log.info(format("Inserting [%s]: %s", c.getValue(), insertSql));
            }
            SimpleHfileToRdbmsExporter.currentMigrator.getRepository().saveRow(insertSql);
            context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED).increment(1);
        } catch (Exception e) {
            log.error(e);
        }

    }

}