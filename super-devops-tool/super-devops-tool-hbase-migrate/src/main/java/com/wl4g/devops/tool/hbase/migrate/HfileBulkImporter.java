package com.wl4g.devops.tool.hbase.migrate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;

/**
 * HASE hfile bulk importer.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月6日
 * @since
 */
public class HfileBulkImporter {
	final protected static Log log = LogFactory.getLog(HfileBulkImporter.class);

	/**
	 * e.g.</br>
	 * 
	 * <pre>
	 * yarn jar super-devops-tool-hbase-migrate-master-jar-with-dependencies.jar \
	 * com.wl4g.devops.tool.hbase.migrate.HfileBulkImporter \
	 * -z emr-header-1:2181 \
	 * -t safeclound.tb_ammeter \
	 * -p hdfs://emr-header-1/bak/tb_ammeter
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addRequiredOption("z", "zkaddr", true, "Zookeeper address.");
		options.addRequiredOption("t", "tabname", true, "Hbase table name.");
		options.addRequiredOption("p", "path", true,
				"Data hdfs path to be import. e.g. hdfs://localhost:9000/bak/safeclound.tb_air");
		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
			log.info(String.format("Parsed arguments: {}", line.getArgList()));
		} catch (Exception e) {
			log.error(e);
			new HelpFormatter().printHelp("Usage: ", options);
			return;
		}

		Configuration cfg = new Configuration();
		cfg.set("hbase.zookeeper.quorum", line.getOptionValue("z"));
		Connection conn = ConnectionFactory.createConnection(cfg);
		Admin admin = conn.getAdmin();
		Table table = conn.getTable(TableName.valueOf(line.getOptionValue("t")));
		LoadIncrementalHFiles load = new LoadIncrementalHFiles(cfg);
		load.doBulkLoad(new Path(line.getOptionValue("p")), admin, table,
				conn.getRegionLocator(TableName.valueOf(line.getOptionValue("t"))));
	}

}
