# DoPaaS Tools for HBase

## 1. ETL (machine learning, offline analysis)

- 1.1 HBase table exporting to CSV

```bash
yarn jar dopaas-lcdp-tools-hbase-migrator-master.jar \
 com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkToCsvExporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540
```

## 2. Backup and restore

- 2.1 HBase Hfile-bulk exporting to HDFS

```bash
yarn jar dopaas-lcdp-tools-hbase-migrator-master.jar \
 com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkToHdfsExporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540
```

- 2.2 HBase hfile-bulk importing from HDFS

```bash
yarn jar dopaas-lcdp-tools-hbase-migrator-master \
 com.wl4g.dopaas.lcdp.tools.hbase.bulk.HfileBulkFromHdfsImporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -p /tmp-dopaas/safeclound.tb_elec_power
```

## 3. Migration

- 3.1 HBase exporting htable to RDBMS(MySQL/Oracle/PostgreSQL/...)

```bash
java -cp dopaas-lcdp-tools-hbase-migrator-master.jar \
 com.wl4g.dopaas.lcdp.tools.hbase.rdbms.SimpleHfileToRdbmsExporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -j 'jdbc:mysql://localhost:3306/my_tsdb?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
 -u root \
 -p '123456' \
 -c 100 \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540
```

## 4. Spark exporting htable to csv

```bash
spark-submit \
  --conf "spark.driver.extraJavaOptions=-Dlog.level.root=INFO -Dlog.levels=org.apache.spark=INFO" \
  --deploy-mode client \
  --driver-memory 2g \
  --num-executors 4 \
  --executor-cores 4 \
  --executor-memory 2g \
  --jars ossref://my-oss-bucket/sparklib/dopaas-lcdp-tools-hbase-migrator-master-with-dependencies.jar \
  --class com.wl4g.dopaas.lcdp.tools.hbase.spark.SparkHBaseToHdfsExporter ossref://my-oss-bucket/sparklib/dopaas-lcdp-tools-hbase-migrator-master.jar \
  com.wl4g.dopaas.lcdp.tools.hbase.bulk.HBaseSparkToHdfsExporter \
  -s 11111112,ELE_R_P,134,01,20180919110850989 \
  -e 11111112,ELE_R_P,134,01,20180921124050540 \
  -z emr-header-1:2181 \
  -t safeclound.tb_ammeter \
  -o hdfs://emr-cluster/dopaas/output
```
