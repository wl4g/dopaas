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
package com.wl4g.dopaas.lcdp.tools.hbase.util;

import static com.google.common.base.Charsets.UTF_8;
import static java.lang.System.out;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.io.IOException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.common.io.Resources;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.resource.resolver.ClassPathResourcePatternResolver;

/**
 * {@link HBaseTools}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public abstract class HBaseTools {
    private static final Log log = LogFactory.getLog(HBaseTools.class);

    public static final String DEFAULT_USER = "hbase";
    public static final String DEFUALT_COUNTER_GROUP = HBaseTools.class.getSimpleName().concat(" Counters");
    public static final String DEFUALT_COUNTER_TOTAL = "Total";
    public static final String DEFUALT_COUNTER_PROCESSED = "Progressed";
    public static final String DEFAULT_SCAN_BATCH_SIZE = "1000";
    public static final String DEFAULT_MAP_LIMIT = "8";
    public static final String DEFAULT_FS = "hdfs://"; // hdfs://127.0.0.1:8020
    public static final String DEFAULT_HBASE_MR_TMPDIR = DEFAULT_FS + "/dopaas/tmp";
    public static final String DEFAULT_OUTPUT_DIR = DEFAULT_FS + "/dopaas/output";

    /**
     * Extract byte array without changing the original array.
     *
     * @param bytes
     * @param offset
     * @param len
     * @return New arrays ahead of time
     */
    public static byte[] getCellFieldBytes(byte[] bytes, int offset, int len) {
        byte[] b1 = new byte[len];
        System.arraycopy(bytes, offset, b1, 0, len);
        return b1;
    }

    /**
     * Show banner
     * 
     * @throws IOException
     */
    public static void showBanner() throws IOException {
        out.println(
                Resources.toString(new ClassPathResourcePatternResolver().getResource("classpath:banner.txt").getURL(), UTF_8));
    }

    /**
     * Gets short tableName
     * 
     * @param tableName
     * @return
     */
    public static String getShortTableName(String tableName) {
        if (isBlank(tableName)) {
            return EMPTY;
        }

        int index = tableName.indexOf(".");
        if (index > 0) {
            String shortTableName = tableName.substring(index + 1);
            return shortTableName;
        }

        return tableName;
    }

    /**
     * Is ignore hbase qualifier fields. </br>
     * 
     * <p>
     * Exclude HBase internal built-in fields, for example: '_0'
     * </p>
     * 
     * @return
     */
    public static boolean isIgnoreHbaseQualifier(String qualifier) {
        if (qualifier.startsWith("_") && qualifier.length() == 2) {
            return isNumeric(qualifier.substring(1));
        }
        return false;
    }

    /**
     * Setup scan condition if necessary.
     * 
     * @param conf
     * @param cli
     * @throws IOException
     */
    public static void setScanIfNecessary(Configuration conf, CommandLineFacade cli) throws Exception {
        String startRow = cli.getString("startRow");
        String endRow = cli.getString("endRow");
        String startTime = cli.getString("startTime");
        String endTime = cli.getString("endTime");

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
                    + "      * this is specified.See TableMapReduceUtil.convertScanToString(Scan)\n"
                    + "      * for more details.");
            conf.set(TableInputFormat.SCAN, Base64.encodeBytes(proto.toByteArray()));
        }
    }

}