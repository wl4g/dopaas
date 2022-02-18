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
package com.wl4g.dopaas.lcdp.tools.hbase.bulk.mapred;

import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseUtil.DEFUALT_COUNTER_GROUP;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseUtil.DEFUALT_COUNTER_PROCESSED;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseUtil.DEFUALT_COUNTER_TOTAL;
import static java.lang.String.format;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;

import com.wl4g.dopaas.lcdp.tools.hbase.rdbms.SimpleHfileToRdbmsExporter;

/**
 * HBASE to RDBMS transform mapper.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-02-18 v1.0.0
 * @since v1.0.0
 */
public class HfileToCsvMapper extends TableMapper<Text, Text> {
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void map(ImmutableBytesWritable key, Result result, Context context) throws IOException, InterruptedException {
        Counter c = context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL);
        c.increment(1);
        try {
            Map<String, String> record = toRecord(key, result);
            if (SimpleHfileToRdbmsExporter.verbose) {
                log.info(format("Exporting [%s]: %s", c.getValue(), record));
            }
            writeCsv(record);
            context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED).increment(1);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private Map<String, String> toRecord(ImmutableBytesWritable key, Result result) {
        Map<String, String> record = new LinkedHashMap<String, String>();
        record.put("row", Bytes.toString(key.get()));

        for (Cell cell : result.rawCells()) {
            byte[] qualifier = cell.getQualifierArray();
            byte[] column = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);
            if (qualifier != null && qualifier.length != 0) {
                if (value == null || value.length == 0) {
                    record.put(Bytes.toString(column), "");
                } else {
                    record.put(Bytes.toString(column), Bytes.toString(value));
                }
            }
        }
        return record;
    }

    private void writeCsv(Map<String, String> record) {
        log.info("WriteCsv: " + record.toString());
    }

}