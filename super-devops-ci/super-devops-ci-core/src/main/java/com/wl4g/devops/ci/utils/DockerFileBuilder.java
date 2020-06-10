package com.wl4g.devops.ci.utils;

import com.wl4g.devops.tool.common.io.FileIOUtils;

import java.io.File;

/**
 * @author vjay
 * @date 2020-06-10 14:16:00
 */
public class DockerFileBuilder {


    public static void makeDockerFile(File target){
        StringBuilder stringBuilder = new StringBuilder();
        appendHead(stringBuilder);
        appendARG(stringBuilder);
        appendRUN(stringBuilder);
        FileIOUtils.writeFile(target,stringBuilder.toString(),false);
    }

    private static void appendHead(StringBuilder stringBuilder){
        stringBuilder.append("FROM openjdk:8-jre-alpine\n");
        stringBuilder.append("LABEL maintainer=\"Wanglsir<983708408@qq.com>\"\n");
        //stringBuilder.append("WORKDIR /\n");

    }

    private static void appendARG(StringBuilder stringBuilder){
        stringBuilder.append("ARG APP_BIN_NAME=\"iam-server-master-bin.tar\"\n");
        stringBuilder.append("ARG RUN_COM=\"echo helloworld\"\n");

    }

    private static void appendRUN(StringBuilder stringBuilder){
        stringBuilder.append("COPY ${APP_BIN_NAME} /${APP_BIN_NAME}\n");

        //stringBuilder.append("RUN echo \"params APP_BIN_NAME = ${APP_BIN_NAME}  ----  ${RUN_COM}\"\n");
        stringBuilder.append("RUN echo \"http://mirrors.aliyun.com/alpine/v3.8/main\" > /etc/apk/repositories \\\n")
                .append("&& echo \"http://mirrors.aliyun.com/alpine/v3.8/community\" >> /etc/apk/repositories \\\n")
                .append("&& apk update upgrade \\\n")
                .append("&& apk add --no-cache bash \\\n")
                //.append("&& apk add --no-cache procps unzip curl bash tzdata font-adobe-100dpi ttf-dejavu fontconfig \\\n")//remove some unnecessary
                .append("&& ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \\\n")
                .append("&& echo \"Asia/Shanghai\" > /etc/timezone \\\n")
                .append("&& tar -xvf /${APP_BIN_NAME} \\\n")
                .append("&& rm -rf ${APP_BIN_NAME} \\\n")  //clean app tar
                .append("&& rm -rf /var/lib/apt/lists/*\n"); // clean apt cache


        stringBuilder.append("CMD [\"/bin/sh\", \"-c\", \"${RUN_COM}\"]\n");
    }


}
