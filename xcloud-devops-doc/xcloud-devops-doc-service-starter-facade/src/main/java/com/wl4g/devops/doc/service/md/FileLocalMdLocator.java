/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
 */
package com.wl4g.devops.doc.service.md;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.log.SmartLogger;
import freemarker.template.Template;

import java.io.File;
import java.util.*;

import static com.wl4g.component.common.jvm.JvmRuntimeKit.isJVMDebugging;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;

/**
 * {@link FileLocalMdLocator}
 *
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-19
 * @sine v1.0.0
 * @see
 */
public class FileLocalMdLocator implements MdLocator {

    protected final SmartLogger log = getLogger(getClass());

    /**
     * Generate template {@link Template} cache.
     */
    private final Map<String, List<MdResource>> templatesCache = new HashMap<>();


    @Override
    public List<MdResource> locate(String provider) throws Exception {
        List<MdResource> tpls = templatesCache.get(provider);
        if (isJVMDebugging || isNull(tpls)) {
            synchronized (this) {
                tpls = templatesCache.get(provider);
                if (isJVMDebugging || isNull(tpls)) {
                    tpls = new ArrayList<>();

                    String basePath = "";
                    String path = basePath + "/" + provider;

                    Collection<File> files = FileIOUtils.listFiles(new File(path), null, true);
                    for (File file : files) {
                        tpls.add(wrapTemplate(path, file));
                    }
                    templatesCache.put(provider, tpls);
                }
            }
        }
        return tpls;
    }

    @Override
    public boolean cleanAll() {
        templatesCache.clear();
        return true;
    }

    /**
     * Wrapper {@link Template}
     *
     * @param res
     * @param provider
     * @return
     * @throws Exception
     */
    private static MdResource wrapTemplate(String basePath, File file) throws Exception {
        int i = file.getAbsolutePath().indexOf(basePath);
        String pathname = null;
        if (i >= 0) {
            pathname = file.getAbsolutePath().substring(i + basePath.length());
        }
        return new MdResource(pathname, FileIOUtils.readFileToByteArray(file));
    }




}