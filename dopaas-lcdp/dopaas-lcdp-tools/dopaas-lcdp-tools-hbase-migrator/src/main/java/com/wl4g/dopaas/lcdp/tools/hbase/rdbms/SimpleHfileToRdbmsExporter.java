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
package com.wl4g.dopaas.lcdp.tools.hbase.rdbms;

import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_HBASE_MR_TMPDIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_OUTPUT_DIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_SCAN_BATCH_SIZE;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_GROUP;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_PROCESSED;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_TOTAL;
import static com.wl4g.infra.common.lang.Assert2.state;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.net.URI;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wl4g.dopaas.lcdp.tools.hbase.rdbms.handler.RdbmsHandler;
import com.wl4g.dopaas.lcdp.tools.hbase.rdbms.mapred.SimpleHfileToRdbmsMapper;
import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;
import com.wl4g.infra.common.cli.CommandLineTool.Builder;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

/**
 * Simple HBase HFile to RDBMS exporter. </br>
 * 
 * <p>
 * Note: It is a very good migration tool in the development environment, but it
 * is not recommended for the production environment. Generally, the demand data
 * flow of the production environment migration is RMDB(MySQL/Oracle/...) =>
 * HBase/HDFS/Hive. In this scenario, it is recommended to use professional
 * migration tools, such as Sqoop or StreamSets.
 * </p>
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class SimpleHfileToRdbmsExporter {
    final static Log log = LogFactory.getLog(SimpleHfileToRdbmsExporter.class);

    final public static String DEFAULT_MAPPER_CLASS = SimpleHfileToRdbmsMapper.class.getName();
    final public static int DEFAULT_RMDB_MAXCONNECTIONS = 100;

    public static RdbmsHandler currentMigrator;
    public static boolean verbose;

    /**
     * e.g. </br>
     * 
     * <pre>
     * java -cp dopaas-lcdp-tools-hbase-migrator-master.jar \
     * com.wl4g.dopaas.lcdp.tools.hbase.SimpleHfileToRmdbExporter \
     * -z emr-header-1:2181 \
     * -t safeclound.tb_elec_power \
     * -j 'jdbc:mysql://localhost:3306/my_tsdb?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
     * -u root \
     * -p '123456' \
     * -c 100 \
     * -s 11111112,ELE_R_P,134,01,20180919110850989 \
     * -e 11111112,ELE_R_P,134,01,20180921124050540
     * </pre>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        HBaseTools.showBanner();

        CommandLineFacade line = new Builder()
                .option("V", "verbose", "false", "Set to true to show messages about what the migrator(MR) is doing.")
                .option("T", "tmpdir", DEFAULT_HBASE_MR_TMPDIR, "Hfile export tmp directory.")
                .option("z", "zkaddr", null, "Zookeeper address.")
                .option("t", "tabname", null, "Hbase table name.")
                .option("o", "outputDir", DEFAULT_OUTPUT_DIR + "/{tableName}", "Hfile export output hdfs directory.")
                .option("b", "batchSize", DEFAULT_SCAN_BATCH_SIZE, "Scan batch size.")
                .option("b", "mapLimit", "0", "map limit.")
                .option("s", "startRow", EMPTY, "Scan start rowkey.")
                .option("e", "endRow", EMPTY, "Scan end rowkey.")
                .option("S", "startTime", EMPTY, "Scan start timestamp.")
                .option("E", "endTime", EMPTY, "Scan end timestamp.")
                .option("U", "user", "hbase", "User name used for scan check.")
                .option("M", "mapperClass", DEFAULT_MAPPER_CLASS, "Transfrom migration mapper class name.")
                .option("j", "jdbcUrl", null, "Hbase to rmdb database jdbc url")
                .option("u", "username", null, "Hbase to rmdb database jdbc username")
                .option("p", "password", null, "Hbase to rmdb database jdbc password")
                .option("c", "maxConnections", valueOf(DEFAULT_RMDB_MAXCONNECTIONS),
                        "Hbase to rmdb database jdbc maxConnections.")
                .build(args);

        // Gets rmdb provider instance.
        currentMigrator = RdbmsHandler.getInstance(line);
        // Verbose
        verbose = Boolean.parseBoolean(line.getString("verbose"));

        // DO exporting
        doExporting(line);
    }

    /**
     * Do hfile bulk exporting
     * 
     * @param builder
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void doExporting(CommandLineFacade line) throws Exception {
        // Gets arguments.
        String tabname = line.getString("tabname");
        String user = line.getString("user");
        String tmpdir = line.getString("T");
        String outputdir = line.getString("output") + "/" + tabname;
        String zkaddr = line.getString("zkaddr");
        String batchSize = line.getString("batchSize");
        String mapLimit = line.getString("mapLimit");
        Class<TableMapper> mapperClass = (Class<TableMapper>) ClassUtils.getClass(line.getString("mapperClass"));

        // Configuration.
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", zkaddr);
        conf.set("hbase.fs.tmp.dir", tmpdir);
        conf.set(TableInputFormat.INPUT_TABLE, tabname);
        conf.set(TableInputFormat.SCAN_BATCHSIZE, batchSize);
        conf.set("mapreduce.job.running.map.limit", mapLimit);
        // conf.set(FileSystem.FS_DEFAULT_NAME_KEY, DEFAULT_FS);

        // Check TMP directory.
        FileSystem fs1 = FileSystem.get(new URI(tmpdir), conf, user);
        state(fs1.mkdirs(new Path(tmpdir)), format("Failed to mkdirs HDFS temporary directory. '%s'", tmpdir));

        // Check output directory.
        FileSystem fs2 = FileSystem.get(new URI(outputdir), conf, user);
        if (fs2.exists(new Path(outputdir))) {
            fs2.delete(new Path(outputdir), true);
        }

        // Sets scan filters.
        HBaseTools.setScanIfNecessary(conf, line);

        // Job.
        TableName tab = TableName.valueOf(tabname);
        Job job = Job.getInstance(conf);
        job.setJobName(SimpleHfileToRdbmsExporter.class.getSimpleName() + "@" + tab.getNameAsString());
        job.setJarByClass(SimpleHfileToRdbmsExporter.class);
        job.setMapperClass(mapperClass);
        job.setInputFormatClass(TableInputFormat.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Put.class);

        Connection conn = ConnectionFactory.createConnection(conf);
        HFileOutputFormat2.configureIncrementalLoad(job, conn.getTable(tab), conn.getRegionLocator(tab));
        FileOutputFormat.setOutputPath(job, new Path(outputdir));
        if (job.waitForCompletion(true)) {
            long total = job.getCounters().findCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL).getValue();
            long processed = job.getCounters().findCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED).getValue();
            log.info(String.format("Exported to successfully! with processed:(%d)/total:(%d)", processed, total));
        }

    }

}