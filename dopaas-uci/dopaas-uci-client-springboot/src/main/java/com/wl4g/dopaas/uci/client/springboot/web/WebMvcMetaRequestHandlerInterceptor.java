/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.client.springboot.web;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.component.common.codec.Encodes.urlDecode;
import static com.wl4g.component.common.lang.Assert2.isTrue;
import static com.wl4g.component.common.lang.ClassUtils2.getDefaultClassLoader;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.dopaas.common.constant.UciConstants.DEFAULT_META_NAME;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.common.bean.uci.model.BuildMetaInfo;
import com.wl4g.dopaas.common.bean.uci.model.BuildMetaInfo.SourceInfo;
import com.wl4g.dopaas.uci.client.springboot.config.UciClientProperties;

/**
 * {@link WebMvcMetaRequestHandlerInterceptor}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-20
 * @sine v1.0
 * @see
 */
public class WebMvcMetaRequestHandlerInterceptor implements HandlerInterceptor {
    protected final SmartLogger log = getLogger(getClass());

    @Autowired
    private UciClientProperties config;

    private final File metaFile;
    private BuildMetaInfo metaInfo;

    public WebMvcMetaRequestHandlerInterceptor() {
        this.metaFile = determineMetaFile(getDefaultClassLoader().getResource("").getPath());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        attachBuildMeta(request, response, handler);
        return true;
    }

    private void attachBuildMeta(HttpServletRequest request, HttpServletResponse response, Object handler) {
        BuildMetaInfo meta = loadBuildMetaInfo();
        if (nonNull(meta)) {
            SourceInfo source = meta.getSourceInfo();
            String value = format("%s:%s:%s", source.getCommitId(), encodeBase64String(source.getComment().getBytes(UTF_8)));
            log.debug("Attaching UCI meta to response header: {} = {}", config.getMetaHeaderName(), value);
            response.setHeader(config.getMetaHeaderName(), value);
        }
    }

    private BuildMetaInfo loadBuildMetaInfo() {
        if (isNull(metaInfo)) {
            synchronized (this) {
                if (isNull(metaInfo)) {
                    try {
                        if (nonNull(metaFile) && metaFile.exists()) {
                            String metaContent = readFileToString(metaFile, UTF_8);
                            this.metaInfo = parseJSON(metaContent, BuildMetaInfo.class);
                            log.info("Reading build meta info: {}", metaContent);
                        }
                    } catch (Exception e) {
                        log.warn("Unable read UCI build meta file.", e);
                    }
                    return metaInfo;
                }
            }
        }
        return metaInfo;
    }

    /**
     * Determine meta file.
     * 
     * @param classpath
     * @return
     */
    File determineMetaFile(final String classpath) {
        String appHomePath = "";
        String path = urlDecode(trimToEmpty(classpath).replaceAll("\\\\", "/")); // Solving-chinese-problems
        path = endsWith(path, "/") ? path : path.concat("/");

        // Maven project Server environment:
        if (path.contains("/BOOT-INF/classes")) {
            // e.g:/opt/apps/acm/portal-master-bin/portal-master-bin.jar!/BOOT-INF/classes!/
            int index = path.indexOf(DEFAULT_SPRING_BOOT_INF_CLASSES);
            isTrue(index > 0, "Unkown spring boot jar class path. %s", path);
            String springbootJarPath = path.substring(0, index);
            appHomePath = springbootJarPath.substring(0, springbootJarPath.lastIndexOf("/"));
        }
        // Maven project assemble environment:
        // e.g:/opt/apps/acm/portal-package/portal-master-bin/lib/
        else if (endsWithAny(path, "/lib/", "/libs/", "/ext-lib/", "/ext-libs/")) {
            appHomePath = path.substring(0, path.lastIndexOf("/"));
        }
        // Maven(Gradle|Ant) project local IDE environment:
        // e.g:/home/myuser/safecloud-web-portal/portal-web/target/classes/
        // e.g:/home/myuser/safecloud-web-portal/portal-web/target/test-classes/
        // e.g:/home/myuser/safecloud-web-portal/portal-web/bin/
        // e.g:/home/myuser/safecloud-web-portal/portal-web/build/
        else if (endsWithAny(path, "/target/classes/", "/target/test-classes/", "/bin/", "/build/")) {
            return null;
        }

        // Metafiles must exist to be useful.
        File metaFile = nonNull(appHomePath) ? new File(appHomePath, DEFAULT_META_NAME) : config.getDefaultMetaFile();
        if (!metaFile.exists()) {
            log.warn("Don't exist build metafile: {}", metaFile);
            return null;
        }
        return metaFile;
    }

    private static final String DEFAULT_SPRING_BOOT_INF_CLASSES = "!/BOOT-INF/classes!";

}