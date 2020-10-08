# ${watermark}

### -------------------------------------
### Server configuration.
### -------------------------------------
server:
  listen: :16088
  cors:
    #allow-origins: '*'
    allow-origins: 'http://localhost:16088,https://*.wl4g.com,https://*.wl4g.debug,http://*.wl4g.com,http://*.wl4g.debug'

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