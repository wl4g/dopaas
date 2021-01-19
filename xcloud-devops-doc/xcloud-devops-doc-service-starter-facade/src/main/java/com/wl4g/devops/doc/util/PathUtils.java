package com.wl4g.devops.doc.util;

/**
 * @author vjay
 * @date 2021-01-19 11:38:00
 */
public class PathUtils {

    public static  String splicePath(String... paths) {

        if (null == paths || paths.length <= 0) {
            return null;
        }

        if (paths.length == 1) {
            return paths[0];
        }

        if(!paths[0].startsWith("/")){
            paths[0] = "/" + paths[0];
        }

        StringBuilder path = new StringBuilder(paths[0]);
        for (int i = 1; i < paths.length; i++) {

            if (!paths[i].startsWith("/")) {
                path.append("/").append(paths[i]);
            } else {
                path.append(paths[i]);
            }
        }
        return path.toString();
    }

}
