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

import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_HBASE_MR_TMPDIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_MAP_LIMIT;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_OUTPUT_DIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_SCAN_BATCH_SIZE;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_USER;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_GROUP;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_PROCESSED;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_TOTAL;
import static com.wl4g.infra.common.lang.Assert2.state;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.net.URI;
import java.util.Date;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wl4g.dopaas.lcdp.tools.hbase.bulk.mapred.HfileToCsvMapper;
import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;
import com.wl4g.infra.common.cli.CommandLineTool.Builder;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

/**
 * Simple HBase hfile to CSV files exporter. </br>
 * 
 * <p>
 * Note: It is a very good migration tool in the development environment, but it
 * is not recommended for the production environment. Generally, the demand data
 * flow of the production environment migration is RMDB(MySQL/Oracle/...) =>
 * HBase/HDFS/Hive. In this scenario, it is recommended to use professional
 * migration tools, such as sqoop or streamsets.
 * </p>
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class HfileBulkToCsvExporter {
    private static final Log log = LogFactory.getLog(HfileBulkToHdfsExporter.class);
    private static final String DEFAULT_MAPPER_CLASS = HfileToCsvMapper.class.getName();

    /**
     * e.g. </br>
     * 
     * <pre>
     *  yarn jar dopaas-lcdp-tools-hbase-migrator-2.0.0.jar \
     *  com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkToCsvExporter \
     *  -s 11111112,ELE_R_P,134,01,20180919110850989 \
     *  -e 11111112,ELE_R_P,134,01,20180921124050540 \
     *  -z emr-header-1:2181 \
     *  -t safeclound.tb_elec_power \
     *  -o /dopaas/safeclound.tb_elec_power
     * </pre>
     */
    public static void main(String[] args) throws Exception {
        HBaseTools.showBanner();
        CommandLineFacade cli = new Builder().option("T", "tmpdir", DEFAULT_HBASE_MR_TMPDIR, "Hfile export tmp directory.")
                .option("z", "zkaddr", null, "Zookeeper address.")
                .option("t", "tabname", null, "Hbase table name.")
                .option("o", "output", DEFAULT_OUTPUT_DIR + "/{tabname}", "Hfile export output hdfs directory.")
                .option("b", "batchSize", DEFAULT_SCAN_BATCH_SIZE, "Scan batch size.")
                .option("L", "mapLimit", DEFAULT_MAP_LIMIT, "Mapred tasks limit.")
                .option("s", "startRow", EMPTY, "Scan start rowkey.")
                .option("e", "endRow", EMPTY, "Scan end rowkey.")
                .option("S", "startTime", EMPTY, "Scan start timestamp.")
                .option("E", "endTime", EMPTY, "Scan end timestamp.")
                .option("U", "user", DEFAULT_USER, "User name used for scan check.")
                .option("M", "mapperClass", DEFAULT_MAPPER_CLASS, "Transfrom mapper class name.")
                .build(args);
        doExporting(cli);
    }

    /**
     * Do hfile bulk exporting
     * 
     * @param builder
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void doExporting(CommandLineFacade cli) throws Exception {
        // Gets arguments.
        String zkaddr = cli.getString("zkaddr");
        String tabname = cli.getString("tabname");
        String user = cli.getString("user");
        String tmpdir = cli.getString("T");
        String outputdir = cli.getString("output");
        String batchSize = cli.getString("batchSize");
        String mapLimit = cli.getString("mapLimit");
        Class<TableMapper> mapperClass = (Class<TableMapper>) ClassUtils.getClass(cli.getString("mapperClass"));

        // Configuration.
        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, zkaddr);
        conf.set(HConstants.TEMPORARY_FS_DIRECTORY_KEY, tmpdir);
        conf.set(TableInputFormat.INPUT_TABLE, tabname);
        conf.set(TableInputFormat.SCAN_BATCHSIZE, batchSize);
        conf.set(MRJobConfig.JOB_RUNNING_MAP_LIMIT, mapLimit);
        conf.set("mapred.textoutputformat.ignoreseparator", "true");
        // (deprecated)see:https://github.com/apache/hadoop/blob/rel/release-2.7.2/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-core/src/main/java/org/apache/hadoop/mapreduce/util/ConfigUtil.java#L488-L489
        conf.set("mapred.textoutputformat.separator", ",");
        // see:https://github.com/apache/hadoop/blob/rel/release-2.7.2/hadoop-mapreduce-project/hadoop-mapreduce-client/hadoop-mapreduce-client-core/src/main/java/org/apache/hadoop/mapreduce/lib/output/TextOutputFormat.java
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        // conf.set(FileSystem.FS_DEFAULT_NAME_KEY, FileSystem.DEFAULT_FS);

        // Check TMP directory.
        FileSystem fs1 = FileSystem.get(new URI(tmpdir), conf, user);
        state(fs1.mkdirs(new Path(tmpdir)), format("Failed to mkdirs HDFS temporary directory. '%s'", tmpdir));

        // Check output directory.
        FileSystem fs2 = FileSystem.get(new URI(outputdir), conf, user);
        Path parent = new Path(outputdir).getParent();
        if (fs2.exists(parent)) {
            fs2.rename(parent, Path.getPathWithoutSchemeAndAuthority(parent)
                    .suffix("_bak".concat(DateFormatUtils.format(new Date(), "YYYYMMddHHmmss"))));
        }
        // Sets scan filters.
        HBaseTools.setScanIfNecessary(conf, cli);

        // Job configuration.
        TableName tab = TableName.valueOf(tabname);
        Job job = Job.getInstance(conf);
        job.setJobName(HfileBulkToHdfsExporter.class.getSimpleName() + "@" + tab.getNameAsString());
        job.setJarByClass(HfileBulkToHdfsExporter.class);
        job.setMapperClass(mapperClass);
        job.setInputFormatClass(TableInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // job.setOutputFormatClass(cls);
        // job.setOutputKeyClass(Text.class);
        // job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, new Path(outputdir));
        if (job.waitForCompletion(true)) {
            long total = job.getCounters().findCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_TOTAL).getValue();
            long processed = job.getCounters().findCounter(DEFUALT_COUNTER_GROUP, DEFUALT_COUNTER_PROCESSED).getValue();
            log.info(String.format("Exported to successfully! with processed:(%d)/total:(%d)", processed, total));
        }
    }

}