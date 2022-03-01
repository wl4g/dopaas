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
package com.wl4g.dopaas.ucm.client.console;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.infra.common.serialize.JaxbUtils.toXml;
import static java.lang.System.out;

import java.util.function.Function;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.client.internal.RefreshWatcher;
import com.wl4g.dopaas.ucm.client.recorder.ReleaseConfigSourceWrapper;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ReleaseContent;
import com.wl4g.shell.common.annotation.ShellMethod;
import com.wl4g.shell.common.annotation.ShellOption;

/**
 * {@link UcmManagementConsole}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2018-10-20
 * @sine v1.0.0
 * @see
 */
public class UcmManagementConsole {

    protected final SmartLogger log = getLogger(getClass());

    /** {@link RefreshWatcher} */
    protected final RefreshWatcher watcher;

    public UcmManagementConsole(RefreshWatcher watcher) {
        notNullOf(watcher, "watcher");
        this.watcher = watcher;
    }

    /**
     * Exporting current runtime configuration.
     * 
     * @param format
     */
    @ShellMethod(keys = {
            "export" }, group = DEFAULT_UCM_CONSOLE_GROUP, help = "Export current runtime use configuration source.")
    public void export(
            @ShellOption(opt = "f", lopt = "format", help = "Export configuration source format. (json|xml)") String format) {
        log.info("Exporting configuration soruce... format: {}", format);

        // Gets current used configuration.
        ReleaseConfigSourceWrapper release = getRuntimeUseConfiguration();

        switch (format.toUpperCase()) {
        case "JSON":
            printConfigSources(release, s -> toJSONString(s));
            break;
        case "XML":
            printConfigSources(release, s -> toXml(s, "UTF-8", ReleaseContent.class));
            break;
        default:
            printConfigSources(release, s -> s.getSourceContent());
            break;
        }

    }

    /**
     * Gets runtime use configuration source.
     * 
     * @return
     */
    private ReleaseConfigSourceWrapper getRuntimeUseConfiguration() {
        return ((AbstractRefreshWatcher<?>) watcher).getRecorder().currentRelease();
    }

    /**
     * Output release configuration sources.
     * 
     * @param release
     * @param func
     */
    private void printConfigSources(ReleaseConfigSourceWrapper release, Function<ReleaseContent, String> func) {
        for (ReleaseContent source : release.getRelease().getReleases()) {
            out.println("--- ".concat(source.getProfile().getName()).concat(".").concat(source.getProfile().getType()).concat(
                    " ---"));
            out.println(func.apply(source));
            out.println();
        }
    }

    /** UCM console group */
    public static final String DEFAULT_UCM_CONSOLE_GROUP = "UCM Configuration Console";

}