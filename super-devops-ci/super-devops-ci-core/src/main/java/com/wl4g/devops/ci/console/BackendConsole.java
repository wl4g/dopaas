/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.ci.console;

import com.wl4g.devops.ci.console.args.BuildArgument;
import com.wl4g.devops.ci.console.args.InstanceListArgument;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.CI_LOCK;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOCK_TIME;
import static com.wl4g.devops.shell.utils.ShellContextHolder.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * CI/CD console point
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-21 15:41:00
 * @since
 */
@ShellComponent
public class BackendConsole {

    final public static String GROUP = "Devops CI/CD console commands";

    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private CiService ciService;

    @Autowired
    private SimpleRedisLockManager lockManager;

    /**
     * Execution deployments
     *
     * @param argument
     * @return
     */
    @ShellMethod(keys = "deploy", group = GROUP, help = "Execute application deployment")
    public String deploy(BuildArgument argument) {
        String appGroupName = argument.getAppGroupName();
        List<String> instances = argument.getInstances();
        String branchName = argument.getBranchName();

        // Open console printer.
        open();

        Lock lock = lockManager.getLock(CI_LOCK, LOCK_TIME, TimeUnit.MINUTES);
        try {
            if (lock.tryLock()) {
                // Print to client
                printfQuietly(String.format("Deployment starting <%s><%s><%s> ...", appGroupName, branchName, instances));

                // Create async task
                ciService.createTask(appGroupName, branchName, instances);

                printfQuietly(String.format("Deployment successfully for <%s><%s><%s> !", appGroupName, branchName, instances));
            } else {
                printfQuietly("One Task is running ,Please try again later");
            }

        } catch (Exception e) {
            printfQuietly(e);
        } finally {
            // Close console printer.
            close();
            lock.unlock();
        }

        return "Deployment task finished!";
    }

    /**
     * Got application groups list.
     *
     * @param argument
     * @param context
     * @return
     */
    @ShellMethod(keys = "list", group = GROUP, help = "Get a list of application information")
    public String list(InstanceListArgument argument) {
        StringBuffer result = new StringBuffer();

        String appGroupName = argument.getAppGroupName();
        String envName = argument.getEnvName();
        String r = argument.getAnyInstants();
        Pattern pattern = Pattern.compile(r);
        if (isBlank(appGroupName)) {
            List<AppGroup> apps = appGroupDao.grouplist();
            for (AppGroup appGroup : apps) {
                appendApp(result, appGroup, r);
                result.append("\n");
            }
        } else {
            AppGroup app = appGroupDao.getAppGroupByName(appGroupName);
            if (null == app) {
                return "AppGroup not exist";
            }
            List<Environment> environments = appGroupDao.environmentlist(app.getId().toString());
            if (null == environments || environments.size()<=0) {
                return "no one env";
            }
            if (isBlank(envName)) {
                for (Environment environment : environments) {
                    appendEnv(result, environment, r);
                }
            } else {
                Integer envId = null;
                for (Environment environment : environments) {
                    if (environment.getName().equals(envName)) {
                        envId = environment.getId();
                        break;
                    }
                }
                if (null == envId) {
                    return "env name is wrong";
                }
                AppInstance appInstance = new AppInstance();
                appInstance.setEnvId(envId.toString());
                List<AppInstance> instances = appGroupDao.instancelist(appInstance);
                if (null == instances || instances.size() < 1) {
                    return "none";
                }
                result.append(" ----- <"+envName+"> -----\n");
                result.append("\t[ID]    [HostAndPort]          [description]\n");
                for (int i = 0; i < instances.size() && i < 50; i++) {
                    if (StringUtils.isBlank(r) || StringUtils.isNotBlank(r) && pattern.matcher(instances.get(i).getIp() + ":" + instances.get(i).getPort()).matches()) {
                        appendInstance(result, instances.get(i));
                        if (i == 49) {
                            result.append("\t......");
                        }
                    }
                }
            }
        }
        return result.toString();
    }


    private StringBuffer appendApp(StringBuffer result, AppGroup appGroup, String r) {
        List<Environment> environments = appGroupDao.environmentlist(appGroup.getId().toString());
        if (environments == null||environments.size() <= 0) {
            return result;
        }
        result.append(" <").append(appGroup.getName()).append(">:\n");
        for (int i = 0; i < environments.size(); i++) {
            appendEnv(result, environments.get(i), r);
        }
        return result;
    }

    private StringBuffer appendEnv(StringBuffer result, Environment environment, String r) {
        AppInstance appInstance = new AppInstance();
        appInstance.setEnvId(environment.getId().toString());
        List<AppInstance> instances = appGroupDao.instancelist(appInstance);

        if (null == instances ||instances.size() <= 0) {
            return result;
        }
        result.append(" ----- <"+environment.getName()+"> -----\n");
        result.append("\t[ID]    [HostAndPort]          [description]\n");

        Pattern pattern = Pattern.compile(r);
        for (int i = 0; i < instances.size() && i < 50; i++) {
            if (StringUtils.isBlank(r) || StringUtils.isNotBlank(r) && pattern.matcher(instances.get(i).getIp() + ":" + instances.get(i).getPort()).matches()) {
                appendInstance(result, instances.get(i));
                if (i == 49) {
                    result.append("\t......");
                }
            }
        }
        return result;
    }

    private StringBuffer appendInstance(StringBuffer result, AppInstance instance) {
        result.append("\t").append(formatCell(instance.getId().toString(), 8))
                .append(formatCell((instance.getIp() + ":" + instance.getPort()), 23))
                .append(instance.getRemark()).append("\n");
        return result;
    }


    private String formatCell(String text, int width) {
        if (text != null && text.length() < width) {
            StringBuffer space = new StringBuffer();
            for (int i = 0; i < width - text.length(); i++) {
                space.append(" ");
            }
            text = text + space.toString();
        }
        return text;
    }


}