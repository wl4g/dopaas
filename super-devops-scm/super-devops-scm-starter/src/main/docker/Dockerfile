### Dockerfile for com.wl4g/devops ${APP_NAME}
### 1. Copy ${APP_NAME}-${APP_VERSION}.tar to current directory.
### 2. Build with: docker build -t com.wl4g/${APP_NAME}:${APP_VERSION} .
### 3. Run with: docker run -p ${APP_PORT}:${APP_PORT} -d -v /mnt/disk1/log/${APP_HOME}:/mnt/disk1/log/${APP_HOME} --name ${APP_NAME} ${APP_NAME}

FROM openjdk:8-jre-alpine
LABEL maintainer="wanglsir<983708408@qq.com>"

ARG APP_NAME
ARG APP_VERSION
ENV APP_PORT 14043
ENV APP_HOME "/opt/apps/acm/${APP_NAME}-package"
# DataSource information.
ENV DB_URL ""
ENV DB_USERNAME ""
ENV DB_PASSWORD ""

RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
    && apk update upgrade \
    && apk add --no-cache procps unzip curl bash tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ADD ${APP_NAME}-${APP_VERSION}.tar /${APP_NAME}-${APP_VERSION}.tar

RUN mkdir -p ${APP_HOME}
RUN tar -xf /${APP_NAME}-${APP_VERSION}.tar -C ${APP_HOME} \
    && rm -rf /${APP_NAME}-${APP_VERSION}.tar \
    && chmod +x ${APP_HOME}/bin/startup.sh

EXPOSE $APP_PORT

CMD ["${APP_HOME}/bin/startup.sh"]