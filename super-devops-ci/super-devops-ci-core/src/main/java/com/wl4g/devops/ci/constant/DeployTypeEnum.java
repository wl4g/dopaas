package com.wl4g.devops.ci.constant;

/**
 * @author vjay
 * @date 2019-09-23 10:58:00
 */
public enum DeployTypeEnum {

    MvnAssembleTar,//normal use type
    DockerNative,//docker
    DockerK8s,//docker
    SpringbootExecutable,//spring boot jar
    NativeJar,
    DjangoStandard,
    ;


    public static DeployTypeEnum of(String s) {
        DeployTypeEnum wh = safeOf(s);
        if (wh == null) {
            throw new IllegalArgumentException(String.format("Illegal action '%s'", s));
        }
        return wh;
    }

    public static DeployTypeEnum safeOf(String s) {
        for (DeployTypeEnum t : values()) {
            if (t.name().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }
}
