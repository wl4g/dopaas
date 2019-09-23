package com.wl4g.devops.tool.hbase.migrate.mapred;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import com.wl4g.devops.tool.common.utils.Assert;

import java.io.IOException;
import java.util.Optional;

/**
 * Abstract HBASE HFile transform mapping processing.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月5日
 * @since
 */
public abstract class AbstractMigrateHfileMapper extends TableMapper<ImmutableBytesWritable, Put> {

	@Override
	public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
		// Loading raw table data to perform processing.
		Optional<Put> opt = transform(row, value);
		if (opt.isPresent()) {
			context.write(row, opt.get());
		}
	}

	/**
	 * Transform process writable data result.
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
