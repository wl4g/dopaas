package com.wl4g.devops.dts.codegen.utils.javatype;

import com.google.common.io.Resources;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author vjay
 * @date 2020-09-10 10:47:00
 */
public class JavaTypeUtils {

    // Cache of types.
    public final static Properties javaTppeToPackage;

    private final static String BASE_PATH = JavaTypeUtils.class.getName().replace(".", "/")
            .replace(JavaTypeUtils.class.getSimpleName(), "") + "/java-type-to-package.types";

    static {
        try {
            // sql to java
            javaTppeToPackage = new Properties();
            javaTppeToPackage.load(new StringReader(readResource()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reading config resource file content.
     *
     * @param sqlType
     * @param filename
     * @param args
     * @return
     * @throws IOException
     */
    static String readResource() {
        try {
            File sqlFile = ResourceUtils.getFile("classpath:" + BASE_PATH);
            return Resources.toString(sqlFile.toURI().toURL(), UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
