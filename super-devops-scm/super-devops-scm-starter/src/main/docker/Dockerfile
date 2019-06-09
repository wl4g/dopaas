# Dockerfile for super-devops-scm

# Build with:
# docker build -t scm:master .

FROM openjdk:8-jre-alpine3.8

RUN \
    echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories && \
    echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories && \
    apk update upgrade && \
    apk add --no-cache procps curl bash tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    mkdir -p /apollo-admin-server

ADD . /target/scm-master-bin.tar

ENV SCM_SERVICE_NAME="scm"
ENV JAVA_OPTS=""

EXPOSE 14043

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /scm.jar" ]
