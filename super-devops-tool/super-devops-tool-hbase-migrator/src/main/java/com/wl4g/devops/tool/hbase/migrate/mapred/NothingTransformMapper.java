package com.wl4g.devops.tool.hbase.migrate.mapred;

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
