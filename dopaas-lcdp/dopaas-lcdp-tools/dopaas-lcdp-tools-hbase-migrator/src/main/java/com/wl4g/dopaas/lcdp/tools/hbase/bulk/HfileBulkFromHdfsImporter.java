/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.lcdp.tools.hbase.bulk;

import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_MAP_LIMIT;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;

import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;
import com.wl4g.infra.common.cli.CommandLineTool.Builder;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

/**
 * HASE hfile bulk importer.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月6日
 * @since
 */
public class HfileBulkFromHdfsImporter {

    /**
     * e.g.</br>
     * 
     * <pre>
     *  yarn jar dopaas-lcdp-tools-hbase-migrator-2.0.0.jar \
     *  com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkFromHdfsImporter \
     *  -z emr-header-1:2181 \
     *  -t safeclound.tb_elec_power \
     *  -p /dopaas/safeclound.tb_elec_power
     * </pre>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        HBaseTools.showBanner();
        CommandLineFacade line = new Builder().option("z", "zkaddr", null, "Zookeeper address.")
                .option("t", "tabname", null, "Hbase table name.")
                .option("p", "path", null, "Data hdfs path to be import. eg. hdfs://localhost:9000/bak/mydb.tb_example1")
                .option("L", "mapLimit", DEFAULT_MAP_LIMIT, "Mapred tasks limit.")
                .build(args);

        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, line.getString("z"));

        Connection conn = ConnectionFactory.createConnection(conf);
        Admin admin = conn.getAdmin();
        Table table = conn.getTable(TableName.valueOf(line.getString("t")));
        LoadIncrementalHFiles load = new LoadIncrementalHFiles(conf);
        load.doBulkLoad(new Path(line.getString("p")), admin, table,
                conn.getRegionLocator(TableName.valueOf(line.getString("t"))));
    }

}