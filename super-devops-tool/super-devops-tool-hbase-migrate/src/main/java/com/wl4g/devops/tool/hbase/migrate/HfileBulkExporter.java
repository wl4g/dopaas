package com.wl4g.devops.tool.hbase.migrate;

import com.wl4g.devops.tool.common.utils.Assert;
import com.wl4g.devops.tool.hbase.migrate.mapred.ExamplePrefixMigrateMapper;

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
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
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
	final protected static Log log = LogFactory.getLog(HfileBulkExporter.class);

	final public static String DEFAULT_HBASE_FSTMP_DIR = "/tmp/fstmpdir";
	final public static String DEFAULT_SCAN_BATCH_SIZE = "1000";

	/**
	 * e.g. </br>
	 * 
	 * <pre>
	 * yarn jar super-devops-tool-hbase-migrate-master-jar-with-dependencies.jar \
	 * com.wl4g.devops.tool.hbase.migrate.HfileBulkExporter \
	 * -z owner-node2:2181 \
	 * -t safeclound.tb_ammeter \
	 * -o hdfs://emr-header-1/bak/safeclound.tb_ammeter
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("T", "tmpdir", false, "Hbase tmp directory. default:" + DEFAULT_HBASE_FSTMP_DIR);
		options.addRequiredOption("z", "zkaddr", true, "Zookeeper address.");
		options.addRequiredOption("t", "tabname", true, "Hbase table name.");
		options.addRequiredOption("o", "output", true, "Output hdfs path.");
		options.addOption("b", "batchsize", false, "Scan batch size. default: " + DEFAULT_SCAN_BATCH_SIZE);
		options.addOption("s", "startrow", false, "Scan start rowkey.");
		options.addOption("e", "endrow", false, "Scan end rowkey.");
		options.addOption("S", "starttimestamp", false, "Scan start timestamp.");
		options.addOption("E", "endtimestamp", false, "Scan end timestamp.");
		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
			log.info(String.format("Parsed arguments: %s", line.getArgList()));
		} catch (Exception e) {
			log.error(e);
			new HelpFormatter().printHelp("Usage: ", options);
			return;
		}

		// Configuration
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", line.getOptionValue("z"));
		conf.set("hbase.fs.tmp.dir", line.getOptionValue("T", DEFAULT_HBASE_FSTMP_DIR));
		conf.set("hbase.mapreduce.inputtable", line.getOptionValue("t"));
		conf.set("hbase.mapreduce.scan.batchsize", line.getOptionValue("b", DEFAULT_SCAN_BATCH_SIZE));

		// Check directory.
		String output = line.getOptionValue("o");
		FileSystem fs = FileSystem.get(new URI(output), new Configuration(), "root");
		Assert.state(!fs.exists(new Path(output)), String.format("Catalogs do not allow other data. '%s'", output));

		// Set scan condition.(if necessary)
		setScanIfNecessary(conf, line);

		// Job
		Connection conn = ConnectionFactory.createConnection(conf);
		Table table = conn.getTable(TableName.valueOf(line.getOptionValue("t")));
		Job job = Job.getInstance(conf);
		job.setJarByClass(HfileBulkExporter.class);
		job.setMapperClass(ExamplePrefixMigrateMapper.class);
		job.setInputFormatClass(TableInputFormat.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);

		HFileOutputFormat2.configureIncrementalLoad(job, table,
				conn.getRegionLocator(TableName.valueOf(line.getOptionValue("t"))));
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

		Scan scan = new Scan();
		// Row
		if (isNotBlank(startRow)) {
			conf.set(TableInputFormat.SCAN_ROW_START, startRow);
			scan.setStartRow(Bytes.toBytes(startRow));
		}
		if (isNotBlank(endRow)) {
			Assert.hasText(startRow, "Argument for startRow and endRow are used simultaneously");
			conf.set(TableInputFormat.SCAN_ROW_STOP, endRow);
			scan.setStopRow(Bytes.toBytes(endRow));
		}

		// Row TimeStamp
		if (isNotBlank(startTime) && isNotBlank(endTime)) {
			conf.set(TableInputFormat.SCAN_TIMERANGE_START, startTime);
			conf.set(TableInputFormat.SCAN_TIMERANGE_END, endTime);
			try {
				Timestamp stime = new Timestamp(Long.parseLong(startTime));
				Timestamp etime = new Timestamp(Long.parseLong(endTime));
				scan.setTimeRange(stime.getTime(), etime.getTime());
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("Illegal startTime(%s) and endTime(%s)", startTime, endTime), e);
			}
		}
		ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
		conf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray()));
	}

}
