### Dockerfile for wl4g/${APP_NAME}
### 1. Copy ${APP_NAME}-${APP_VERSION}.tar to current directory.
### 2. Build with: docker build -t com.wl4g/${APP_NAME}:${APP_VERSION} .
### 3. Run with: 
### docker run -p ${APP_PORT}:${APP_PORT} -d -v /mnt/disk1/log/${APP_NAME}:/mnt/disk1/log/${APP_NAME} 
### -v /mnt/disk1/${APP_NAME}:/mnt/disk1/${APP_NAME} --name ${APP_NAME} ${APP_NAME}

FROM openjdk:8-jre-alpine
LABEL maintainer="Wanglsir<983708408@qq.com>"

ARG APP_NAME
ARG APP_VERSION
ARG APP_BIN_NAME
ARG APP_PORT
ENV APP_HOME "/opt/apps/acm/${APP_NAME}-package"

RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
&& echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
&& apk update upgrade \
&& apk add --no-cache procps unzip curl bash tzdata \
&& ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

COPY target/${APP_BIN_NAME}.tar /${APP_BIN_NAME}.tar

RUN mkdir -p ${APP_HOME}
RUN tar -xf /${APP_BIN_NAME}.tar -C ${APP_HOME} \
&& rm -rf /${APP_BIN_NAME}.tar \
&& chmod +x ${APP_HOME}/${APP_BIN_NAME}/bin/startup.sh

EXPOSE ${APP_PORT}

#CMD ["${APP_HOME}/${APP_BIN_NAME}/bin/startup.sh"]