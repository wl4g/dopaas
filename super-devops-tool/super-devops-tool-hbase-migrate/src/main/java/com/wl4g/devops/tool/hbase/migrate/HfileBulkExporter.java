package com.wl4g.devops.tool.hbase.migrate;

import com.wl4g.devops.tool.common.utils.Assert;
import com.wl4g.devops.tool.hbase.migrate.mapred.ExamplePrefixTransformMapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.net.URI;

/**
 * HASE hfile bulk exporter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class HfileBulkExporter {
	final protected static Log log = LogFactory.getLog(HfileBulkExporter.class);

	final public static String DEFAULT_HBASE_FSTMP_DIR = "/tmp/fstmpdir";
	final public static String DEFAULT_SCAN_BATCH_SIZE = "1000";

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("T", "tmpdir", false, "Hbase tmp directory. default:" + DEFAULT_HBASE_FSTMP_DIR);
		options.addRequiredOption("z", "zkaddr", true, "Zookeeper address.");
		options.addRequiredOption("t", "tabname", true, "Hbase table name.");
		/*options.addRequiredOption("p", "path", true,
				"Data hdfs path to be import. e.g. hdfs://localhost:9000/bak/safeclound.tb_air");*/
		options.addRequiredOption("o", "output", true, "Output hdfs path.");
		options.addOption("b", "batchsize", false, "Scan batch size. default: " + DEFAULT_SCAN_BATCH_SIZE);
		options.addOption("s", "startrow", false, "Scan start rowkey.");
		options.addOption("e", "endrow", false, "Scan end rowkey.");
		options.addOption("S", "starttimestamp", false, "Scan start timestamp.");
		options.addOption("E", "endtimestamp", false, "Scan end timestamp.");
		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
			log.info(String.format("Parsed arguments: {}", line.getArgList()));
		} catch (Exception e) {
			log.error(e);
			new HelpFormatter().printHelp("Usage: ", options);
			return;
		}

		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", line.getOptionValue("z"));
		conf.set("hbase.fs.tmp.dir", line.getOptionValue("T", DEFAULT_HBASE_FSTMP_DIR));
		conf.set("hbase.mapreduce.inputtable", line.getOptionValue("t"));
		conf.set("hbase.mapreduce.scan.batchsize", line.getOptionValue("b", DEFAULT_SCAN_BATCH_SIZE));

		// Check directory.
		String output = line.getOptionValue("o");
		FileSystem fs = FileSystem.get(new URI(output), new Configuration(), "root");
		Assert.state(!fs.exists(new Path(output)), String.format("Catalogs do not allow other data. '%s'", output));

		// Scan condition.
		/*String startRow = line.getOptionValue("s");
		String endRow = line.getOptionValue("e");
		String startTime = line.getOptionValue("S");
		String endTime = line.getOptionValue("E");
		Scan scan = new Scan(); // If necessary
		if (null != startTimestamp || null != endTimestamp) {
			startTimestamp = startTimestamp == null ? 0 : startTimestamp;
			endTimestamp = endTimestamp == null ? System.currentTimeMillis() : endTimestamp;
			conf.set(TableInputFormat.SCAN_TIMERANGE_START, startTimestamp.toString());
			conf.set(TableInputFormat.SCAN_TIMERANGE_END, endTimestamp.toString());
			scan.setTimeRange(startTime, endTime);
		}

		if (isNotBlank(startRow)) {
			conf.set(TableInputFormat.SCAN_ROW_START, startRow);
			scan.setStartRow(Bytes.toBytes(startRow));
		}
		if (isNotBlank(endRow)) {
			conf.set(TableInputFormat.SCAN_ROW_STOP, endRow);
			scan.setStopRow(Bytes.toBytes(endRow));
		}
		System.out.println("filter:" + startTimestamp + "--" + endTimestamp + "--" + startRow + "--" + endRow);
		ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
		conf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray()));*/

		Connection conn = ConnectionFactory.createConnection(conf);
		Table table = conn.getTable(TableName.valueOf(line.getOptionValue("t")));
		Job job = Job.getInstance(conf);
		job.setJarByClass(HfileBulkExporter.class);
		job.setMapperClass(ExamplePrefixTransformMapper.class);
		job.setInputFormatClass(TableInputFormat.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);
		// job.getConfiguration().set("mapred.jar",
		// mapredJar);//预先将程序打包再将jar分发到集群上

		HFileOutputFormat2.configureIncrementalLoad(job, table,
				conn.getRegionLocator(TableName.valueOf(line.getOptionValue("t"))));
		FileOutputFormat.setOutputPath(job, new Path(output));
		if (job.waitForCompletion(true)) {
			log.info("Exported to successfully !");
		}

	}

}
