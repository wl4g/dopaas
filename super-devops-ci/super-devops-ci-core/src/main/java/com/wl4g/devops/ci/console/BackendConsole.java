package com.wl4g.devops.ci.console;

import org.springframework.beans.factory.annotation.Autowired;

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
import static com.wl4g.devops.shell.utils.ShellConsoleHolder.*;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

import java.util.List;

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

		try {
			// Open console printer.
			open();

			printfQuietly(String.format("Deployment starting <%s><%s><%s> ...", appGroupName, branchName, instances));

			// Create async task
			ciService.createTask(appGroupName, branchName, instances);

			printfQuietly(String.format("Deployment successfully for <%s><%s><%s> !", appGroupName, branchName, instances));

		} catch (Exception e) {
			printfQuietly(String.format("Deployments failure. cause by: %s", getStackTrace(e)));
		} finally {
			// Close console printer.
			close();
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
						+ "\t\t" + instances.get(0).getRemark() + "\n");
				for (int i = 1; i < instances.size(); i++) {
					AppInstance instance = instances.get(i);
					result.append("\t\t" + instance.getId() + "\t" + instance.getIp() + ":" + instance.getHost() + "\t\t"
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
				+ "\t\t" + instances.get(0).getRemark() + "\n");
		for (int i = 1; i < instances.size(); i++) {
			AppInstance instance = instances.get(i);
			result.append("\t\t" + instance.getId() + "\t" + instance.getIp() + ":" + instance.getPort() + "\t\t"
					+ instance.getRemark() + "\n");
		}
		return result;
	}

}
