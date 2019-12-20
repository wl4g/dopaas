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
package com.wl4g.devops.tool.hbase.migrator.mapred;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Nothing HBASE HFile prefix transform.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class NothingTransformMapper extends AbstractTransformHfileMapper {

	@Override
	protected boolean isFilter(String row, Result result) {
		return true;
	}

	@Override
	protected Put newPut(String row) {
		return new Put(Bytes.toBytes(row));
	}

	@Override
	protected void addPut(Put put, byte[] family, byte[] qualifier, byte[] value, long timestamp, byte type) throws IOException {
		Cell newCell = CellUtil.createCell(put.getRow(), family, qualifier, timestamp, type, value);
		put.add(newCell);
	}

}