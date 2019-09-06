package com.wl4g.devops.tool.hbase.migrate;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * HBASE hfile transform mapper.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class SimpleHfileTransformMapper extends TableMapper<ImmutableBytesWritable, Put> {

	public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
		// this example is just copying the data from the source table...
		context.write(row, resultToPut(row, value));
	}

	private static Put resultToPut(ImmutableBytesWritable key, Result result) throws IOException {
		Put put = new Put(Bytes.toBytes(Bytes.toString(key.get())));
		for (Cell cell : result.listCells()) {
			put.add(cell);
		}
		return put;
	}

	/**
	 * 截取byte数组 不改变原数组
	 * 
	 * @param b
	 *            原数组
	 * @param off
	 *            偏差值（索引）
	 * @param length
	 *            长度
	 * @return 截取后的数组
	 */
	public static byte[] subByte(byte[] b, int off, int length) {
		byte[] b1 = new byte[length];
		System.arraycopy(b, off, b1, 0, length);
		return b1;
	}

}
