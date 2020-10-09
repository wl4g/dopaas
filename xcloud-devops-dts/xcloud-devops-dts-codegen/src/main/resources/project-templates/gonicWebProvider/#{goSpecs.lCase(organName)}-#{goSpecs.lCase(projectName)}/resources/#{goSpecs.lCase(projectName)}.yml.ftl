# ${watermark}

${goSpecs.wrapSingleComment(copyright, '#')}

### -------------------------------------
### Server configuration.
### -------------------------------------
server:
  listen: :16088
  cors:
    allow-origins: '*'
    #allow-origins: 'http://localhost:16088,https://${fatServiceHost}:${entryAppPort},http://${fatServiceHost}:${entryAppPort},https://*.${fatTopDomain},http://*.${fatTopDomain}'
    #allow-origins: 'http://localhost:16088,https://${uatServiceHost}:${entryAppPort},http://${uatServiceHost}:${entryAppPort},https://*.${uatTopDomain},http://*.${uatTopDomain}'
    #allow-origins: 'http://localhost:16088,https://${proServiceHost}:${entryAppPort},http://${proServiceHost}:${entryAppPort},https://*.${proTopDomain},http://*.${proTopDomain}'
    allow-credentials: false

### -------------------------------------
### DataSource configuration.
### -------------------------------------
datasource:
  mysql:
    # 10.0.0.160:3306
    dbconnectstr: gzsm:gzsm@%#jh?@tcp(127.0.0.1:3306)/devops_dev?charset=utf8

### -------------------------------------
### Admin configuration.
### -------------------------------------
admin:
  listen: :16089

### -------------------------------------
### Logger configuration.
### -------------------------------------
logging:
  items:
    main:
      file: /mnt/disk1/log/${projectName?lower_case}/${projectName?lower_case}.log
      level: INFO
      policy:
        retention-days: 30 # Day
        max-backups: 30 # Numbers
        max-size: 512 # MB
    receive:
      file: /mnt/disk1/log/${projectName?lower_case}/${projectName?lower_case}-receive.log
      level: INFO
      policy:
        retention-days: 30 # Day
        max-backups: 30 # Numbers
        max-size: 512 # MB
