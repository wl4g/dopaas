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
package com.wl4g.devops.dts.kit.hbase;

import static com.wl4g.components.common.lang.Assert2.state;
import static com.wl4g.devops.dts.kit.hbase.utils.HbaseMigrateUtils.*;
import static java.lang.String.format;

import com.wl4g.components.common.cli.CommandUtils.Builder;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.devops.dts.kit.hbase.mapred.NoOpTransformMapper;
import com.wl4g.devops.dts.kit.hbase.utils.HbaseMigrateUtils;

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

import static org.apache.commons.lang3.StringUtils.EMPTY;
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

	final public static String DEFAULT_MAPPER_CLASS = NoOpTransformMapper.class.getName();

	/**
	 * e.g. </br>
	 * 
	 * <pre>
	 *  yarn jar super-devops-tool-hbase-migrator-master.jar \
	 *  com.wl4g.devops.tool.hbase.migrator.HfileBulkExporter \
	 *  -s 11111112,ELE_R_P,134,01,20180919110850989 \
	 *  -e 11111112,ELE_R_P,134,01,20180921124050540 \
	 *  -z emr-header-1:2181 \
	 *  -t safeclound.tb_elec_power \
	 *  -o /tmp-devops/safeclound.tb_elec_power
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HbaseMigrateUtils.showBanner();

		Builder builder = new Builder();
		builder.option("T", "tmpdir", DEFAULT_HBASE_MR_TMPDIR, "Hfile export tmp directory.");
		builder.option("z", "zkaddr", null, "Zookeeper address.");
		builder.option("t", "tabname", null, "Hbase table name.");
		builder.option("o", "outputDir", DEFAULT_HFILE_OUTPUT_DIR + "/{tableName}", "Hfile export output hdfs directory.");
		builder.option("b", "batchSize", DEFAULT_SCAN_BATCH_SIZE, "Scan batch size.");
		builder.option("s", "startRow", EMPTY, "Scan start rowkey.");
		builder.option("e", "endRow", EMPTY, "Scan end rowkey.");
		builder.option("S", "startTime", EMPTY, "Scan start timestamp.");
		builder.option("E", "endTime", EMPTY, "Scan end timestamp.");
		builder.option("U", "user", "hbase", "User name used for scan check.");
		builder.option("M", "mapperClass", DEFAULT_MAPPER_CLASS, "Transfrom migration mapper class name.");
		doExporting(builder.build(args));
	}

	/**
	 * Do hfile bulk exporting
	 * 
	 * @param builder
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void doExporting(CommandLine line) throws Exception {
		// Configuration.
		String tabname = line.getOptionValue("tabname");
		String user = line.getOptionValue("user");
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", line.getOptionValue("zkaddr"));
		conf.set("hbase.fs.tmp.dir", line.getOptionValue("T", DEFAULT_HBASE_MR_TMPDIR));
		conf.set(TableInputFormat.INPUT_TABLE, tabname);
		conf.set(TableInputFormat.SCAN_BATCHSIZE, line.getOptionValue("batchSize", DEFAULT_SCAN_BATCH_SIZE));

		// Check directory.
		String outputDir = line.getOptionValue("output", DEFAULT_HFILE_OUTPUT_DIR) + "/" + tabname;
		FileSystem fs = FileSystem.get(new URI(outputDir), new Configuration(), user);
		state(!fs.exists(new Path(outputDir)), format("HDFS temporary directory already has data, path: '%s'", outputDir));

		// Set scan condition.(if necessary)
		setScanIfNecessary(conf, line);

		// Job.
		Connection conn = ConnectionFactory.createConnection(conf);
		TableName tab = TableName.valueOf(tabname);
		Job job = Job.getInstance(conf);
		job.setJobName(HfileBulkExporter.class.getSimpleName() + "@" + tab.getNameAsString());
		job.setJarByClass(HfileBulkExporter.class);
		job.setMapperClass((Class<Mapper>) ClassUtils.getClass(line.getOptionValue("mapperClass", DEFAULT_MAPPER_CLASS)));
		job.setInputFormatClass(TableInputFormat.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);

		HFileOutputFormat2.configureIncrementalLoad(job, conn.getTable(tab), conn.getRegionLocator(tab));
		FileOutputFormat.setOutputPath(job, new Path(outputDir));
		if (job.waitForCompletion(true)) {
			long total = job.getCounters().findCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL).getValue();
			long processed = job.getCounters().findCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED).getValue();
			log.info(String.format("Exported to successfully! with processed:(%d)/total:(%d)", processed, total));
		}

	}

	/**
	 * Setup scan condition if necessary.
	 * 
	 * @param conf
	 * @param line
	 * @throws IOException
	 */
	public static void setScanIfNecessary(Configuration conf, CommandLine line) throws IOException {
		String startRow = line.getOptionValue("startRow");
		String endRow = line.getOptionValue("endRow");
		String startTime = line.getOptionValue("startTime");
		String endTime = line.getOptionValue("endTime");

		boolean enabledScan = false;
		Scan scan = new Scan();
		// Row
		if (isNotBlank(startRow)) {
			conf.set(TableInputFormat.SCAN_ROW_START, startRow);
			scan.setStartRow(Bytes.toBytes(startRow));
			enabledScan = true;
		}
		if (isNotBlank(endRow)) {
			Assert2.hasText(startRow, "Argument for startRow and endRow are used simultaneously");
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