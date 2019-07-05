# Dockerfile for com.wl4g/devops scm-server
# 1. Copy ${APP_NAME}-${APP_VERSION}.tar to current directory.
# 2. Build with: docker build -t com.wl4g/${APP_NAME}:${APP_VERSION} .
# 3. Run with: docker run -p 8080:8080 -d -v /tmp/logs:/opt/logs --name apollo-configservice apollo-configservice

FROM openjdk:8-jre-alpine
LABEL maintainer="wanglsir<983708408@qq.com>"

ENV APP_NAME "scm"
ENV APP_VERSION "master"
ENV APP_PORT 14043
ENV APP_HOME "/opt/apps/acm/${APP_NAME}-package"
# DataSource Info
ENV DS_URL ""
ENV DS_USERNAME ""
ENV DS_PASSWORD ""

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