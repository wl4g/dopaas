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
package com.wl4g.dopaas.lcdp.tools.hbase.spark;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_HBASE_MR_TMPDIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_OUTPUT_DIR;
import static com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools.DEFAULT_USER;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkToHdfsExporter;
import com.wl4g.dopaas.lcdp.tools.hbase.util.CsvUtil;
import com.wl4g.dopaas.lcdp.tools.hbase.util.HBaseTools;
import com.wl4g.infra.common.cli.CommandLineTool.Builder;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;

import scala.Tuple2;

/**
 * {@link SparkHBaseToHdfsExporter}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-02-19 v1.0.0
 * @since v1.0.0
 */
public class SparkHBaseToHdfsExporter implements Serializable {
    private static final long serialVersionUID = -5277212061036639902L;
    private static final Log log = LogFactory.getLog(HfileBulkToHdfsExporter.class);

    /**
     * e.g. </br>
     * 
     * <pre>
     *  ./spark-submit \
     *  --conf "spark.driver.extraJavaOptions=-Dlog.level.root=INFO -Dlog.levels=org.apache.spark=INFO" \
     *  --deploy-mode client \
     *  --driver-memory 2g \
     *  --num-executors 4 \
     *  --executor-cores 4 \
     *  --executor-memory 2g \
     *  --jars ossref://my-oss-bucket/sparklib/dopaas-lcdp-tools-hbase-migrator-2.0.0-jar-with-dependencies.jar \
     *  --class com.wl4g.dopaas.lcdp.tools.hbase.spark.SparkHBaseToHdfsExporter ossref://my-oss-bucket/sparklib/dopaas-lcdp-tools-hbase-migrator-2.0.0.jar \
     *  com.wl4g.dopaas.lcdp.tools.hbase.bulk.HBaseSparkToHdfsExporter \
     *  -s 11111112,ELE_R_P,134,01,20180919110850989 \
     *  -e 11111112,ELE_R_P,134,01,20180921124050540 \
     *  -z emr-header-1:2181 \
     *  -t safeclound.tb_ammeter \
     *  -o hdfs://emr-cluster/dopaas/output
     * </pre>
     */
    public static void main(String[] args) throws Exception {
        CommandLineFacade cli = new Builder().option("T", "tmpdir", DEFAULT_HBASE_MR_TMPDIR, "Hfile export tmp directory.")
                .option("z", "zkaddr", null, "Zookeeper address.")
                .option("t", "tabname", null, "Hbase table name.")
                .option("o", "output", DEFAULT_OUTPUT_DIR + "/{tabname}", "Hfile export output hdfs directory.")
                .option("U", "user", DEFAULT_USER, "User name used for scan check.")
                .option("s", "startRow", EMPTY, "Scan start rowkey.")
                .option("e", "endRow", EMPTY, "Scan end rowkey.")
                .option("S", "startTime", EMPTY, "Scan start timestamp.")
                .option("E", "endTime", EMPTY, "Scan end timestamp.")
                .option("R", "repartition", "0", "Repartition number.")
                .build(args);
        String zkaddr = cli.getString("zkaddr");
        String tabname = cli.getString("tabname");
        String outputdir = cli.getString("output") + "/" + tabname;
        String user = cli.getString("user");
        int repartition = Integer.parseInt(cli.getString("repartition"));

        SparkConf conf = new SparkConf().setAppName(SparkHBaseToHdfsExporter.class.getSimpleName())
                .setMaster("local[*]")
                .set("spark.akka.frameSize", "1024")
                // .set("spark.kryoserializer.buffer.mb", "512")
                .set("spark.ui.port", "4042")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.rdd.compress", "true")
                .set("spark.io.compression.codec", "org.apache.spark.io.SnappyCompressionCodec")
                .set("spark.streaming.blockInterval", "1000")
                .set("spark.shuffle.manager", "SORT")
                .set("spark.eventLog.enabled", "true");
        // .set("spark.scheduler.mode", "FAIR")
        // //see:https://stackoverflow.com/questions/68390868/filenotfoundexception-on-spark-scheduler-allocation-file
        // .set("spark.scheduler.allocation.file", "./spark-scheduler.xml");

        JavaSparkContext sc = new JavaSparkContext(conf);
        Configuration hadoopConf = sc.hadoopConfiguration();
        hadoopConf.set(HConstants.ZOOKEEPER_QUORUM, zkaddr);
        hadoopConf.set(HConstants.HBASE_DIR, HConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT);
        hadoopConf.set(TableInputFormat.INPUT_TABLE, tabname);
        // Sets scan filters.
        HBaseTools.setScanIfNecessary(hadoopConf, cli);

        // Check output directory.
        FileSystem fs = FileSystem.get(new URI(outputdir), hadoopConf, user);
        Path parent = new Path(outputdir).getParent();
        if (fs.exists(parent)) {
            fs.rename(parent, Path.getPathWithoutSchemeAndAuthority(parent)
                    .suffix("_bak".concat(DateFormatUtils.format(new Date(), "YYYYMMddHHmmss"))));
        }

        // load transform to RDD
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = sc.newAPIHadoopRDD(hadoopConf, TableInputFormat.class,
                ImmutableBytesWritable.class, Result.class);

        AtomicLong total = new AtomicLong(0l);
        JavaRDD<String> resultRDD = hbaseRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, String>() {
            private static final long serialVersionUID = -8277212221036639902L;

            @Override
            public String call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                String rowkey = new String(tuple._1.get(), UTF_8);
                StringBuilder header = null;
                if (total.getAndIncrement() == 0) {
                    header = new StringBuilder(128);
                    header.append("rowkey,");
                }

                StringBuilder body = new StringBuilder(128);
                body.append(CsvUtil.escapeCsv(rowkey)).append(",");
                Cell[] cells = tuple._2.rawCells();
                for (int i = 0; i < cells.length; i++) {
                    Cell cell = cells[i];
                    String family = new String(CellUtil.cloneFamily(cell), UTF_8);
                    String qualifier = new String(CellUtil.cloneQualifier(cell), UTF_8);
                    String value = new String(CellUtil.cloneValue(cell), UTF_8);
                    if (nonNull(header)) {
                        header.append(CsvUtil.escapeCsv(family)).append(":").append(CsvUtil.escapeCsv(qualifier)).append(",");
                    }
                    body.append(CsvUtil.escapeCsv(value)).append(",");
                }
                if (nonNull(header)) {
                    return header.append("\r\n").append(body).toString();
                }
                return body.toString();
            }
        });
        if (log.isDebugEnabled()) {
            List<String> collect = resultRDD.collect();
            log.debug(format("Output total: %s, %s ...", collect.size(), collect.stream().findFirst().orElse(null)));
        }

        // Write to files.
        if (repartition > 0) {
            resultRDD.repartition(1);
        }
        resultRDD.saveAsTextFile(outputdir);
        sc.close();
    }

}
