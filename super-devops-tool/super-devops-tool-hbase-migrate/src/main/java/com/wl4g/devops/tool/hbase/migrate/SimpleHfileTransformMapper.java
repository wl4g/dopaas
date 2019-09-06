package com.wl4g.devops.tool.hbase.migrate;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HBASE hfile transform mapper.
 *
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class SimpleHfileTransformMapper extends TableMapper<ImmutableBytesWritable, Put> {


    private static List<String> list = new ArrayList<>();

    private static long count = 0;

    static {
        list.add("11111112");
        list.add("11111114");
        list.add("11111115");
        list.add("11111117");
        list.add("11111118");
        list.add("11111119");
        list.add("11111120");
        list.add("11111121");
        list.add("11111122");
        list.add("11111123");
        list.add("11111124");
        list.add("11111131");
        list.add("11111132");
        list.add("11111135");
        list.add("11111136");
        list.add("11111137");
        list.add("11111138");
        list.add("11111139");
        list.add("11111140");
        list.add("11111141");
        list.add("11111142");
        list.add("11111143");
        list.add("11111144");
        list.add("11111145");
        list.add("11111146");
        list.add("11111147");
        list.add("11111148");
        list.add("11111149");
        list.add("11111150");
        list.add("11111151");
        list.add("11111152");
        list.add("11111153");
        list.add("11111154");
        list.add("11111155");
        list.add("11111156");
        list.add("11111157");
        list.add("11111158");
        list.add("11111159");
        list.add("11111160");
        list.add("11111161");
        list.add("11111171");
        list.add("11111172");
        list.add("11111173");
        list.add("11111174");
        list.add("11111175");
        list.add("11111176");
        list.add("11111177");
        list.add("11111178");
        list.add("11111179");
        list.add("11111180");
        list.add("11111181");
        list.add("11111182");
    }


    public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
        // this example is just copying the data from the source table...
        Put put = resultToPut(row, value);
        if(null!=put){
            context.write(row, put);
        }


    }

	/*private static Put resultToPut(ImmutableBytesWritable key, Result result) throws IOException {
		Put put = new Put(Bytes.toBytes(Bytes.toString(key.get())));
		for (Cell cell : result.listCells()) {
			put.add(cell);
		}
		return put;
	}*/

    private static Put resultToPut(ImmutableBytesWritable key, Result result) throws IOException {
        count++;
        System.out.println(count);

        String oldRowKey = Bytes.toString(key.get());
        String pre = oldRowKey.substring(0, 8);
        if (list.contains(pre)) {
            String newRowKey = "3" + oldRowKey.substring(1, oldRowKey.length());//TODO 修改开头数字
            Put put = new Put(Bytes.toBytes(newRowKey));
            for (Cell cell : result.listCells()) {
                byte[] family = subByte(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                byte[] value = subByte(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                byte[] qualifier = subByte(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                long timestamp = cell.getTimestamp();
                byte type = cell.getTypeByte();
                byte[] newRowKeyByte = Bytes.toBytes(newRowKey);
                Cell newCell = CellUtil.createCell(newRowKeyByte, family, qualifier, timestamp, type, value);
                put.add(newCell);
            }
            return put;
        }

        return null;
    }

    /**
     * 截取byte数组 不改变原数组
     *
     * @param b      原数组
     * @param off    偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }


}
