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
package com.wl4g.devops.tool.hbase.migrate;

import com.wl4g.devops.tool.common.utils.Assert;
import com.wl4g.devops.tool.common.utils.CommandLines.Builder;
import com.wl4g.devops.tool.hbase.migrate.mapred.NothingTransformMapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;

/**
 * HASE hfile bulk exporter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class HfileBulkExporter {
	final static Log log = LogFactory.getLog(HfileBulkExporter.class);

	final public static String DEFAULT_HBASE_FSTMP_DIR = "/tmp/fstmpdir";
	final public static String DEFAULT_SCAN_BATCH_SIZE = "1000";
	final public static String DEFAULT_MAPPER_CLASS = NothingTransformMapper.class.getName();

	/**
	 * e.g. </br>
	 * 
	 * <pre>
	 * yarn jar super-devops-tool-hbase-migrator-master-jar-with-dependencies.jar \
	 * com.wl4g.devops.tool.hbase.migrate.HfileBulkExporter \
	 * -z emr-header-1:2181 \
	 * -t safeclound.tb_ammeter \
	 * -o hdfs://emr-header-1/bak/safeclound.tb_ammeter
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		CommandLine line = new Builder().option("T", "tmpdir", false, "Hbase tmp directory. default:" + DEFAULT_HBASE_FSTMP_DIR)
				.option("z", "zkaddr", true, "Zookeeper address.").option("t", "tabname", true, "Hbase table name.")
				.option("o", "output", true, "Output hdfs path.")
				.option("b", "batchsize", false, "Scan batch size. default: " + DEFAULT_SCAN_BATCH_SIZE)
				.option("s", "startrow", false, "Scan start rowkey.").option("e", "endrow", false, "Scan end rowkey.")
				.option("S", "starttime", false, "Scan start timestamp.").option("E", "endtime", false, "Scan end timestamp.")
				.option("M", "mapperclass", false, "Transfrom migration mapper class name. default:" + DEFAULT_MAPPER_CLASS)
				.option("E", "endtime", false, "Scan end timestamp.").build(args);

		// Configuration
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", line.getOptionValue("z"));
		conf.set("hbase.fs.tmp.dir", line.getOptionValue("T", DEFAULT_HBASE_FSTMP_DIR));
		conf.set(TableInputFormat.INPUT_TABLE, line.getOptionValue("t"));
		conf.set(TableInputFormat.SCAN_BATCHSIZE, line.getOptionValue("b", DEFAULT_SCAN_BATCH_SIZE));

		// Check directory.
		String output = line.getOptionValue("o");
		FileSystem fs = FileSystem.get(new URI(output), new Configuration(), "root");
		Assert.state(!fs.exists(new Path(output)), String.format("Catalogs do not allow other data. '%s'", output));

		// Set scan condition.(if necessary)
		setScanIfNecessary(conf, line);

		// Job
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tab = TableName.valueOf(line.getOptionValue("t"));
		Job job = Job.getInstance(conf);
		job.setJobName(HfileBulkExporter.class.getSimpleName() + "@" + tab.getNameAsString());
		job.setJarByClass(HfileBulkExporter.class);
		job.setMapperClass((Class<Mapper>) ClassUtils.getClass(line.getOptionValue("M", DEFAULT_MAPPER_CLASS)));
		job.setInputFormatClass(TableInputFormat.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);

		HFileOutputFormat2.configureIncrementalLoad(job, conn.getTable(tab), conn.getRegionLocator(tab));
		FileOutputFormat.setOutputPath(job, new Path(output));
		if (job.waitForCompletion(true)) {
			log.info("Exported to successfully !");
		}

	}

	/**
	 * Setup scan condition if necessary.
	 * 
	 * @param conf
	 * @param line
	 * @throws IOException
	 */
	private static void setScanIfNecessary(Configuration conf, CommandLine line) throws IOException {
		String startRow = line.getOptionValue("s");
		String endRow = line.getOptionValue("e");
		String startTime = line.getOptionValue("S");
		String endTime = line.getOptionValue("E");

		boolean enabledScan = false;
		Scan scan = new Scan();
		// Row
		if (isNotBlank(startRow)) {
			conf.set(TableInputFormat.SCAN_ROW_START, startRow);
			scan.setStartRow(Bytes.toBytes(startRow));
			enabledScan = true;
		}
		if (isNotBlank(endRow)) {
			Assert.hasText(startRow, "Argument for startRow and endRow are used simultaneously");
			conf.set(TableInputFormat.SCAN_ROW_STOP, endRow);
			scan.setStopRow(Bytes.toBytes(endRow));
			enabledScan = true;
		}

		// Row TimeStamp
		if (isNotBlank(startTime) && isNotBlank(endTime)) {
			conf.set(TableInputFormat.SCAN_TIMERANGE_START, startTime);
			conf.set(TableInputFormat.SCAN_TIMERANGE_END, endTime);
			try {
				Timestamp stime = new Timestamp(Long.parseLong(startTime));
				Timestamp etime = new Timestamp(Long.parseLong(endTime));
				scan.setTimeRange(stime.getTime(), etime.getTime());
				enabledScan = true;
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("Illegal startTime(%s) and endTime(%s)", startTime, endTime), e);
			}
		}

		if (enabledScan) {
			ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
			log.info("All other SCAN configuration are ignored if\n"
					+ "		 * this is specified.See TableMapReduceUtil.convertScanToString(Scan)\n"
					+ "		 * for more details.");
			conf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray()));
		}
	}

}
