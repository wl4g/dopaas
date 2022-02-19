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

import static com.wl4g.component.common.collection.CollectionUtils2.safeArray;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseUtil.DEFUALT_COUNTER_GROUP;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseUtil.DEFUALT_COUNTER_PROCESSED;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseUtil.DEFUALT_COUNTER_TOTAL;
import static java.lang.String.format;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.lcdp.tools.hbase.rdbms.SimpleHfileToRdbmsExporter;

/**
 * HBASE to RDBMS transform mapper.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-02-18 v1.0.0
 * @since v1.0.0
 */
public class HfileToCsvMapper extends Mapper<Text, Result, Text, Text> {

    protected final SmartLogger log = getLogger(getClass());

    private static final Text oneValue = new Text();

    @Override
    public void map(Text key, Result result, Context context) throws IOException, InterruptedException {
        Counter c = context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL);
        c.increment(1);

//        // Transform to record map.
        Map<String, String> record = toRecordMap(key, result);
//        if (SimpleHfileToRdbmsExporter.verbose) {
//            log.info(format("Exporting csv [%s]: %s", c.getValue(), record));
//        }
//        //
//        try {
//            doWriteCsv(key, context, record);
//        } catch (Exception e) {
//            log.error(format("Failed to write csv record: {}", record), e);
//        }
    }

    private Map<String, String> toRecordMap(Text key, Result result) {
        Map<String, String> record = new LinkedHashMap<String, String>();
//        record.put("rowkey", Bytes.toString(key.get()));

        for (Cell cell : safeArray(Cell.class, result.rawCells())) {
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

    protected void doWriteCsv(ImmutableBytesWritable key, Context context, Map<String, String> record) throws Exception {
        // progressed increment
        Counter progressed = context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED);
        progressed.increment(1);

        StringBuilder line = new StringBuilder(record.size() * 8);
        // append Header
        if (progressed.getValue() <= 1) {
            record.keySet().forEach(columnName -> line.append(columnName).append(","));
            line.append("\n");
        }

        // append body
        record.values().forEach(columnValue -> line.append(columnValue).append(","));

        // Write to context
        oneValue.set(line.toString());
//        context.write(key, oneValue);

    }

}