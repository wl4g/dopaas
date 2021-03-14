### Hbase operation(migrator) suite tools.


##### HBase table data exporting to HDFS :
```
 yarn jar xcloud-dopaas-udc-tools-hbase-migrator-master \
 com.wl4g.dopaas.udc.tools.hbase.HfileBulkExporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540
```

##### HBase table data importing from HDFS :
```
 yarn jar xcloud-dopaas-udc-tools-hbase-migrator-master \
 com.wl4g.dopaas.udc.tools.hbase.HfileBulkImporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -p /tmp-dopaas/safeclound.tb_elec_power
```

##### HBase table data exporting to RMDB(MySQL/Oracle/Postgresql/...) :
```
 java -cp xcloud-dopaas-udc-tools-hbase-migrator-master \
 com.wl4g.dopaas.udc.tools.hbase.SimpleHfileToRmdbExporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -j 'jdbc:mysql://localhost:3306/my_tsdb?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
 -u root \
 -p '123456' \
 -c 100 \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540
```


