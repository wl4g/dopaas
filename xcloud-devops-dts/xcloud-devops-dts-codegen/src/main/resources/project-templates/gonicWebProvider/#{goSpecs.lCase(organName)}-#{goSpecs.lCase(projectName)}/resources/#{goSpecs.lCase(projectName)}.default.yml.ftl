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
    allow-methods: GET,POST,OPTIONS,PUT,DELETE,UPDATE
    allow-headers: Authorization,Content-Length,X-CSRF-Token,Token,session,X_Requested_With,Accept,Origin,Host,Connection,Accept-Encoding,Accept-Language,DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Pragma
    expose-headers: Content-Length,Access-Control-Allow-Origin,Access-Control-Allow-Headers,Cache-Control,Content-Language,Content-Type,Expires,Last-Modified,Pragma,
    max-age: 172800

### -------------------------------------
### DataSource configuration.
### -------------------------------------
datasource:
  mysql:
    # user:password@tcp(host:port)/database?charset=utf-8
    dbconnectstr: root:root@tcp(127.0.0.1:3306)/${projectName?lower_case}?charset=utf8
    max-open-conns: 80
    max-idle-conns: 10
    conn-max-lifetime-sec: 90

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
    date-format-pattern: 06-01-02 15:04:05 # ISO8601 => 2006-01-02 15:04:05
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
