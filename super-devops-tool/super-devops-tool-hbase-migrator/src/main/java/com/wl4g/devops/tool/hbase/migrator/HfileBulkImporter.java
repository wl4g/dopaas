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
package com.wl4g.devops.tool.hbase.migrator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.wl4g.devops.tool.common.cli.CommandUtils.Builder;
import com.wl4g.devops.tool.common.resources.resolver.PatternMatchingResourceResolver;

/**
 * HASE hfile bulk importer.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月6日
 * @since
 */
public class HfileBulkImporter {
	final static Log log = LogFactory.getLog(HfileBulkImporter.class);

	/**
	 * e.g.</br>
	 * 
	 * <pre>
	 * yarn jar super-devops-tool-hbase-migrator-master-jar-with-dependencies.jar \
	 * com.wl4g.devops.tool.hbase.migrate.HfileBulkImporter \
	 * -z emr-header-1:2181 \
	 * -t safeclound.tb_elec_power \
	 * -p /tmp-devops/hfileoutputdir/safeclound.tb_elec_power
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(Resources.toString(new PatternMatchingResourceResolver().getResource("classpath:banner.txt").getURL(),
				Charsets.UTF_8));

		CommandLine line = new Builder().option("z", "zkaddr", true, "Zookeeper address.")
				.option("t", "tabname", true, "Hbase table name.")
				.option("p", "path", true, "Data hdfs path to be import. e.g. hdfs://localhost:9000/bak/safeclound.tb_air")
				.build(args);

		Configuration cfg = HBaseConfiguration.create();
		cfg.set("hbase.zookeeper.quorum", line.getOptionValue("z"));
		Connection conn = ConnectionFactory.createConnection(cfg);
		Admin admin = conn.getAdmin();
		Table table = conn.getTable(TableName.valueOf(line.getOptionValue("t")));
		LoadIncrementalHFiles load = new LoadIncrementalHFiles(cfg);
		load.doBulkLoad(new Path(line.getOptionValue("p")), admin, table,
				conn.getRegionLocator(TableName.valueOf(line.getOptionValue("t"))));
	}

}