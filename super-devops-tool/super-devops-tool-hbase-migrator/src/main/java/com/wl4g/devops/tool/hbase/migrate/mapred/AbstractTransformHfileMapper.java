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
package com.wl4g.devops.tool.hbase.migrate.mapred;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import com.wl4g.devops.tool.common.utils.lang.Assert;

import java.io.IOException;
import java.util.Optional;

/**
 * Abstract HBASE HFile transform mapping processing.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月5日
 * @since
 */
public abstract class AbstractTransformHfileMapper extends TableMapper<ImmutableBytesWritable, Put> {

	final public static String DEFUALT_COUNTER_GROUP = AbstractTransformHfileMapper.class.getSimpleName() + "@CounterGroup";
	final public static String DEFUALT_COUNTER_TOTAL = "Total@Counter";
	final public static String DEFUALT_COUNTER_FILTERED = "Filtered@Counter";

	@Override
	public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
		context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL).increment(1);

		// Loading raw table data to perform processing.
		Optional<Put> opt = transform(row, value);
		if (opt.isPresent()) {
			context.getCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_FILTERED).increment(1);
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
		if (isFilter(row, result)) {
			Put put = newPut(row);
			Assert.notNull(put, "Put must not be null");
			for (Cell cell : result.listCells()) {
				byte[] family = extractByteArray(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
				byte[] qualifier = extractByteArray(cell.getQualifierArray(), cell.getQualifierOffset(),
						cell.getQualifierLength());
				byte[] value = extractByteArray(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
				addPut(put, family, qualifier, value, cell.getTimestamp(), cell.getTypeByte());
			}
			return Optional.of(put);
		}
		return Optional.empty();
	}

	/**
	 * Extract byte array without changing the original array.
	 *
	 * @param bys
	 * @param offset
	 * @param len
	 * @return New arrays ahead of time
	 */
	protected byte[] extractByteArray(byte[] bys, int offset, int len) {
		byte[] b1 = new byte[len];
		System.arraycopy(bys, offset, b1, 0, len);
		return b1;
	}

	/**
	 * Does filtering require the current row?
	 * 
	 * @param row
	 * @param result
	 * @return
	 */
	protected abstract boolean isFilter(String row, Result result);

	/**
	 * New create put for data conversion
	 * 
	 * @param row
	 * @return
	 */
	protected abstract Put newPut(String row);

	/**
	 * Add data to put
	 * 
	 * @param put
	 * @param family
	 * @param qualifier
	 * @param value
	 * @param timestamp
	 * @param type
	 */
	protected abstract void addPut(Put put, byte[] family, byte[] qualifier, byte[] value, long timestamp, byte type)
			throws IOException;

}