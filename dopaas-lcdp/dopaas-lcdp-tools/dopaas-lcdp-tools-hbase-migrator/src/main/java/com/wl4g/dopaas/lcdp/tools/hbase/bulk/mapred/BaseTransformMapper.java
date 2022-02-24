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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

/**
 * Abstract HBASE HFile transform mapping processing.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月5日
 * @since
 */
public abstract class BaseTransformMapper extends TableMapper<ImmutableBytesWritable, Put> {
    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
        context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL).increment(1);

        // Loading raw table data to perform processing.
        Optional<Put> opt = transform(row, value);
        if (opt.isPresent()) {
            context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED).increment(1);
            context.write(row, opt.get());
        }
    }

    /**
     * Transform processing writable data row.
     * 
     * @param key
     * @param result
     * @return
     * @throws IOException
     */
    protected Optional<Put> transform(ImmutableBytesWritable key, Result result) throws IOException {
        String row = Bytes.toString(key.get());
        if (doFilter(row, result)) {
            Put put = notNullOf(doCreatePut(row), "newPut");
            Iterator<Cell> it = result.listCells().iterator();
            while (it.hasNext()) {
                Cell cell = it.next();
                byte[] family = getCellFieldBytes(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                byte[] qualifier = getCellFieldBytes(cell.getQualifierArray(), cell.getQualifierOffset(),
                        cell.getQualifierLength());
                byte[] value = getCellFieldBytes(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                appendCell(put, family, qualifier, value, cell.getTimestamp(), cell.getTypeByte(), it.hasNext());
            }
            return Optional.of(put);
        }
        return Optional.empty();
    }

    /**
     * Does filtering require the current row?
     * 
     * @param row
     * @param result
     * @return
     */
    protected boolean doFilter(String row, Result result) {
        return true;
    }

    /**
     * New create put for data conversion
     * 
     * @param row
     * @return
     */
    protected Put doCreatePut(String row) {
        return new Put(Bytes.toBytes(row));
    }

    /**
     * Append column data to put.
     * 
     * @param put
     * @param family
     * @param qualifier
     * @param value
     * @param timestamp
     * @param type
     * @param hasNextQualifier
     */
    private void appendCell(Put put, byte[] family, byte[] qualifier, byte[] value, long timestamp, byte type,
            boolean hasNextQualifier) throws IOException {
        Cell newCell = CellUtil.createCell(put.getRow(), family, qualifier, timestamp, type, value);
        put.add(newCell);
    }

}