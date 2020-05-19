### Hbase operation(migrator) suite tools.


##### HBase table data exporting to HDFS :
```
 yarn jar super-devops-tool-hbase-migrator-master.jar \
 com.wl4g.devops.tool.hbase.migrator.HfileBulkExporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540
```

##### HBase table data importing from HDFS :
```
 yarn jar super-devops-tool-hbase-migrator-master.jar \
 com.wl4g.devops.tool.hbase.migrator.HfileBulkImporter \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -p /tmp-devops/safeclound.tb_elec_power
```

##### HBase table data exporting to RMDB(MySQL/Oracle/Postgresql/...) :
```
 yarn jar super-devops-tool-hbase-migrator-master.jar \
 com.wl4g.devops.tool.hbase.migrator.HfileBulkExporter \
 -s 11111112,ELE_R_P,134,01,20180919110850989 \
 -e 11111112,ELE_R_P,134,01,20180921124050540 \
 -z emr-header-1:2181 \
 -t safeclound.tb_elec_power \
 -o /tmp-devops/safeclound.tb_elec_power
```


