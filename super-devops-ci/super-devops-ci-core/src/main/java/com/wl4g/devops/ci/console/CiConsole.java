package com.wl4g.devops.ci.console;

import com.wl4g.devops.ci.console.bean.BuildArgument;
import com.wl4g.devops.ci.console.bean.InstanceListArgument;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.scm.Environment;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.processor.ShellContext;
import com.wl4g.devops.shell.utils.ShellConsoleHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-21 15:41:00
 */
@ShellComponent
public class CiConsole {


    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private CiService ciService;

    @ShellMethod(keys = "dev", group = "Ci command", help = "devlop")
    public String devlop(BuildArgument argument, ShellContext context) {
        ShellConsoleHolder.bind(context);

        String appGroupName = argument.getAppGroupName();
        List<String> instances = argument.getInstances();
        String branchName = argument.getBranchName();
        //try add console return
        ShellConsoleHolder.beginQuietly();
        // Used to simulate an asynchronous task, constantly outputting logs
        new Thread(() -> {
            try {
                // Output stream message
                ShellConsoleHolder.writeQuietly("task begin");
                ciService.createTask(appGroupName, branchName, instances);
                ShellConsoleHolder.writeQuietly("task end");
            } catch (Exception e) {
                ShellConsoleHolder.writeQuietly("task fail");
            } finally {
                // Must end, and must be after ShellConsoles.begin()
                ShellConsoleHolder.endQuietly();
            }
        }).start();
        return "create task";
    }


    @ShellMethod(keys = "list", group = "Ci command",
            help = "get list ")
    public String list(InstanceListArgument argument) {
        StringBuffer result = new StringBuffer();

        String appGroupName = argument.getAppGroupName();
        String envName = argument.getEnvName();
        if (StringUtils.isBlank(appGroupName)) {
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
            if (StringUtils.isBlank(envName)) {
                if (null == environments) {
                    return "the project has not env yet,please config it";
                }
                for (Environment environment : environments) {
                    result = getInstance(result, environment.getId(), environment.getName());
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
                result.append("\t" + instances.get(0).getId() + "\t" + instances.get(0).getIp() + ":" + instances.get(0).getHost() + "\t\t" + instances.get(0).getRemark() + "\n");
                for (int i = 1; i < instances.size(); i++) {
                    AppInstance instance = instances.get(i);
                    result.append("\t\t" + instance.getId() + "\t" + instance.getIp() + ":" + instance.getHost() + "\t\t" + instance.getRemark() + "\n");
                }
            }

        }
        return result.toString();
    }


    private StringBuffer getInstance(StringBuffer result, Integer envId, String envName) {
        AppInstance appInstance = new AppInstance();
        appInstance.setEnvId(envId.toString());
        List<AppInstance> instances = appGroupDao.instancelist(appInstance);
        if (null == instances || instances.size() < 1) {
            return result;
        }
        result.append(envName + ":");
        result.append("\t" + instances.get(0).getId() + "\t" + instances.get(0).getIp() + ":" + instances.get(0).getPort() + "\t\t" + instances.get(0).getRemark() + "\n");
        for (int i = 1; i < instances.size(); i++) {
            AppInstance instance = instances.get(i);
            result.append("\t\t" + instance.getId() + "\t" + instance.getIp() + ":" + instance.getPort() + "\t\t" + instance.getRemark() + "\n");
        }
        return result;
    }

}
