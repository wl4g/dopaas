/*
 * Copyright 2015 the original author or authors.
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
import com.wl4g.devops.shell.processor.ShellContext;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.CI_LOCK;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOCK_TIME;
import static com.wl4g.devops.shell.utils.ShellContextHolder.*;
import static com.wl4g.devops.shell.utils.ShellContextHolder.printfQuietly;
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
    private JedisCluster jedisCluster;

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
        Lock lock = new SimpleRedisLockManager(jedisCluster).getLock(CI_LOCK, LOCK_TIME, TimeUnit.MINUTES);

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
    public String list(InstanceListArgument argument, ShellContext context) {
        StringBuffer result = new StringBuffer();

        String appGroupName = argument.getAppGroupName();
        String envName = argument.getEnvName();
        if (isBlank(appGroupName)) {
            List<AppGroup> apps = appGroupDao.grouplist();
            result.append("apps:\n");
            for (AppGroup appGroup : apps) {
                result.append(appGroup.getName() + "\n");
            }
        } else {
            AppGroup app = appGroupDao.getAppGroupByName(appGroupName);
            if (null == app) {
                return "AppGroup not exist";
            }

            List<Environment> environments = appGroupDao.environmentlist(app.getId().toString());
            if (isBlank(envName)) {
                if (null == environments) {
                    return "the project has not env yet,please config it";
                }
                for (Environment environment : environments) {
                    result = appandInstance(result, environment.getId(), environment.getName());
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
                result.append(envName + ":");
                result.append("\t" + instances.get(0).getId() + "\t" + instances.get(0).getIp() + ":" + instances.get(0).getHost()
                        + "\t" + instances.get(0).getRemark() + "\n");
                for (int i = 1; i < instances.size(); i++) {
                    AppInstance instance = instances.get(i);
                    result.append("\t" + instance.getId() + "\t" + instance.getIp() + ":" + instance.getHost() + "\t"
                            + instance.getRemark() + "\n");
                }
            }

        }
        return result.toString();
    }

    /**
     * Append instance information
     *
     * @param result
     * @param envId
     * @param envName
     * @return
     */
    private StringBuffer appandInstance(StringBuffer result, Integer envId, String envName) {
        AppInstance appInstance = new AppInstance();
        appInstance.setEnvId(envId.toString());
        List<AppInstance> instances = appGroupDao.instancelist(appInstance);
        if (null == instances || instances.size() < 1) {
            return result;
        }
        result.append(envName + ":");
        result.append("\t" + instances.get(0).getId() + "\t" + instances.get(0).getIp() + ":" + instances.get(0).getPort()
                + "\t" + instances.get(0).getRemark() + "\n");
        for (int i = 1; i < instances.size(); i++) {
            AppInstance instance = instances.get(i);
            result.append("\t" + instance.getId() + "\t" + instance.getIp() + ":" + instance.getPort() + "\t"
                    + instance.getRemark() + "\n");
        }
        return result;
    }

}