# ${watermark}

### -------------------------------------
### Server configuration.
### -------------------------------------
server:
  listen: :16088
  cors:
    #allow-origins: '*'
    allow-origins: 'http://localhost:16088,https://*.wl4g.com,https://*.wl4g.debug,http://*.wl4g.com,http://*.wl4g.debug'
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
    dbconnectstr: root:root@tcp(127.0.0.1:3306)/webconsole?charset=utf8
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
      file: /mnt/disk1/log/webconsole/webconsole.log
      level: INFO
      policy:
        retention-days: 30 # Day
        max-backups: 30 # Numbers
        max-size: 512 # MB
    receive:
      file: /mnt/disk1/log/webconsole/webconsole-ws.log
      level: INFO
      policy:
        retention-days: 30 # Day
        max-backups: 30 # Numbers
        max-size: 512 # MB