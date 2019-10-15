package com.wl4g.devops.tool.hbase.migrate.mapred;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Prefix HBASE HFile prefix transform.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class ExamplePrefixTransformMapper extends AbstractTransformHfileMapper {

	final static List<String> ROW_PREFIX = unmodifiableList(new ArrayList<String>() {
		private static final long serialVersionUID = 8767166856581107226L;
		{
			add("11111112");
			add("11111114");
			add("11111115");
			add("11111117");
			add("11111118");
			add("11111119");
			add("11111120");
			add("11111121");
			add("11111122");
			add("11111123");
			add("11111124");
			add("11111131");
			add("11111132");
			add("11111135");
			add("11111136");
			add("11111137");
			add("11111138");
			add("11111139");
			add("11111140");
			add("11111141");
			add("11111142");
			add("11111143");
			add("11111144");
			add("11111145");
			add("11111146");
			add("11111147");
			add("11111148");
			add("11111149");
			add("11111150");
			add("11111151");
			add("11111152");
			add("11111153");
			add("11111154");
			add("11111155");
			add("11111156");
			add("11111157");
			add("11111158");
			add("11111159");
			add("11111160");
			add("11111161");
			add("11111171");
			add("11111172");
			add("11111173");
			add("11111174");
			add("11111175");
			add("11111176");
			add("11111177");
			add("11111178");
			add("11111179");
			add("11111180");
			add("11111181");
			add("11111182");
		}
	});

	/**
	 * e.g. </br>
	 * Only rows containing prefixes are matched:
	 * 
	 * <pre>
	 * RowKey(11111112,ELE_P,134,01,20180919110851085)  √(matched)
	 * RowKey(21111112,ELE_P,121,03,20190918121152085)  ×(unmatched)
	 * </pre>
	 */
	@Override
	protected boolean isFilter(String row, Result result) {
		return ROW_PREFIX.contains(row.substring(0, 8));
	}

	/**
	 * e.g. </br>
	 * 
	 * <pre>
	 * RowKey(11111112,ELE_P,134,01,20180919110851085) => RowKey(31111112,ELE_P,134,01,20180919110851085)
	 * </pre>
	 */
	@Override
	protected Put newPut(String row) {
		return new Put(Bytes.toBytes("3" + row.substring(1, row.length())));
	}

	@Override
	protected void addPut(Put put, byte[] family, byte[] qualifier, byte[] value, long timestamp, byte type) throws IOException {
		Cell newCell = CellUtil.createCell(put.getRow(), family, qualifier, timestamp, type, value);
		put.add(newCell);
	}

}
