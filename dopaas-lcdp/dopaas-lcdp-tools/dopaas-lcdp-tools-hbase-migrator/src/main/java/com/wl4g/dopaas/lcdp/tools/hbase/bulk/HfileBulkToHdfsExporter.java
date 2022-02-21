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

import static com.wl4g.component.common.lang.Assert2.state;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_HBASE_MR_TMPDIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_OUTPUT_DIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_MAP_LIMIT;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_SCAN_BATCH_SIZE;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_USER;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_GROUP;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_PROCESSED;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFUALT_COUNTER_TOTAL;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.net.URI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wl4g.component.common.cli.CommandUtils.Builder;
import com.wl4g.dopaas.lcdp.tools.hbase.bulk.mapred.NoOpTransformMapper;
import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;

/**
 * HASE hfile bulk exporter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class HfileBulkToHdfsExporter {
    private static final Log log = LogFactory.getLog(HfileBulkToHdfsExporter.class);
    private static final String DEFAULT_MAPPER_CLASS = NoOpTransformMapper.class.getName();

    /**
     * e.g. </br>
     * 
     * <pre>
     *  yarn jar dopaas-lcdp-tools-hbase-migrator-2.0.0.jar \
     *  com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkToHdfsExporter \
     *  -s 11111112,ELE_R_P,134,01,20180919110850989 \
     *  -e 11111112,ELE_R_P,134,01,20180921124050540 \
     *  -z emr-header-1:2181 \
     *  -t safeclound.tb_elec_power \
     *  -o /dopaas/safeclound.tb_elec_power
     * </pre>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        HBaseTools.showBanner();

        Builder builder = new Builder();
        builder.option("T", "tmpdir", DEFAULT_HBASE_MR_TMPDIR, "Hfile export tmp directory.");
        builder.option("z", "zkaddr", null, "Zookeeper address.");
        builder.option("t", "tabname", null, "Hbase table name.");
        builder.option("o", "output", DEFAULT_OUTPUT_DIR + "/{tableName}", "Hfile export output hdfs directory.");
        builder.option("b", "batchSize", DEFAULT_SCAN_BATCH_SIZE, "Scan batch size.");
        builder.option("L", "mapLimit", DEFAULT_MAP_LIMIT, "Mapred tasks limit.");
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
        // Gets arguments.
        String tabname = line.getOptionValue("tabname");
        String user = line.getOptionValue("user", DEFAULT_USER);
        String tmpdir = line.getOptionValue("T", DEFAULT_HBASE_MR_TMPDIR);
        String outputdir = line.getOptionValue("output", DEFAULT_OUTPUT_DIR) + "/" + tabname;
        String zkaddr = line.getOptionValue("zkaddr");
        String batchSize = line.getOptionValue("batchSize", DEFAULT_SCAN_BATCH_SIZE);
        String mapLimit = line.getOptionValue("mapLimit", DEFAULT_MAP_LIMIT);
        Class<TableMapper> mapperClass = (Class<TableMapper>) ClassUtils
                .getClass(line.getOptionValue("mapperClass", DEFAULT_MAPPER_CLASS));

        // Configuration.
        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, zkaddr);
        conf.set(HConstants.TEMPORARY_FS_DIRECTORY_KEY, tmpdir);
        conf.set(TableInputFormat.INPUT_TABLE, tabname);
        conf.set(TableInputFormat.SCAN_BATCHSIZE, batchSize);
        conf.set(MRJobConfig.JOB_RUNNING_MAP_LIMIT, mapLimit);
        // conf.set(FileSystem.FS_DEFAULT_NAME_KEY, DEFAULT_FS);

        // Check TMP directory.
        FileSystem fs1 = FileSystem.get(new URI(tmpdir), conf, user);
        state(fs1.mkdirs(new Path(tmpdir)), format("Failed to mkdirs HDFS temporary directory. '%s'", tmpdir));

        // Check output directory.
        FileSystem fs2 = FileSystem.get(new URI(outputdir), conf, user);
        state(!fs2.exists(new Path(outputdir)), format("HDFS output directory already has data. '%s'", outputdir));

        // Sets scan filters.
        HBaseTools.setScanIfNecessary(conf, line);

        // Job.
        TableName tab = TableName.valueOf(tabname);
        Job job = Job.getInstance(conf);
        job.setJobName(HfileBulkToHdfsExporter.class.getSimpleName() + "@" + tab.getNameAsString());
        job.setJarByClass(HfileBulkToHdfsExporter.class);
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