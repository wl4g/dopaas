### Dockerfile for wl4g/${APP_NAME}
### 1. Copy ${APP_NAME}-${APP_VERSION}.tar to current directory.
### 2. Build with: docker build -t com.wl4g/${APP_NAME}:${APP_VERSION} .
### 3. Run with: 
### docker run -p ${APP_PORT}:${APP_PORT} -d -v /mnt/disk1/log/${APP_NAME}:/mnt/disk1/log/${APP_NAME} 
### -v /mnt/disk1/${APP_NAME}:/mnt/disk1/${APP_NAME} --name ${APP_NAME} ${APP_NAME}

FROM openjdk:8-jre-alpine
LABEL maintainer="Wanglsir<983708408@qq.com>"

ARG APP_BIN_NAME="iam-server-master-bin"
ARG APP_PORT=14040
ARG MAIN_CLASS="com.wl4g.devops.IamServer"
ARG ACTIVE="dev"

### ENV APP_BIN_NAME ${APP_BIN_NAME}

RUN echo "myparams ${APP_BIN_NAME}  ----  ${APP_PORT}  ----   ${MAIN_CLASS} ----  ${ACTIVE} "

RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
&& echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
&& apk update upgrade \
&& apk add --no-cache procps unzip curl bash tzdata font-adobe-100dpi ttf-dejavu fontconfig \
&& ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

COPY ${APP_BIN_NAME}.tar /${APP_BIN_NAME}.tar

RUN tar -xvf /${APP_BIN_NAME}.tar && rm -rf /${APP_BIN_NAME}.tar

EXPOSE ${APP_PORT}

ENV RUN_COM "java -server -Xms256M -Xmx1G  -Dfile.encoding=UTF-8 -cp .:/${APP_BIN_NAME}/conf:/${APP_BIN_NAME}/libs/* ${MAIN_CLASS}  --spring.profiles.active=${ACTIVE} --server.tomcat.basedir=/basedir --logging.file=/log/out.log"

RUN echo "mycommand ${RUN_COM}"

CMD ["/bin/sh", "-c", "${RUN_COM}"]