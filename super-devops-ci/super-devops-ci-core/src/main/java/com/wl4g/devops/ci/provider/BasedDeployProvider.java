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
package com.wl4g.devops.ci.provider;

import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.utils.context.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Based executable provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
public abstract class BasedDeployProvider {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Deployments properties configuration.
	 */
	final protected DeployProperties config;

	/**
	 * branch name
	 */
	final private String branch;

	/**
	 * branch path
	 */
	final private String path;

	/**
	 * project alias
	 */
	final private String alias;

	/**
	 * tarName,for example : demo.tar / demo.jar
	 */
	final private String tarName;

	/**
	 * instances
	 */
	final private List<AppInstance> instances;

	/**
	 * taskDetails
	 */
	final private List<TaskDetail> taskDetails;

	/**
	 * service
	 */
	final private DependencyService dependencyService;

	final private Project project;

	/**
	 * now
	 */
	final private Date now = new Date();

	public BasedDeployProvider(Project project, String path, String branch, String alias, List<AppInstance> instances,
			List<TaskDetail> taskDetails) {
		this.config = SpringContextHolder.getBean(DeployProperties.class);
		this.path = path;
		this.branch = branch;
		this.alias = alias;
		this.instances = instances;
		this.taskDetails = taskDetails;

		String[] a = project.getTarPath().split("/");
		this.tarName = a[a.length - 1];

		this.project = project;
		this.dependencyService = SpringContextHolder.getBean(DependencyService.class);
	}

	public abstract void execute() throws Exception;

	/**
	 * exce command
	 */
	public String doExecute(String targetHost, String userName, String command, String rsa) throws Exception {
		String rsaKey = config.getCipherKey();
		AES aes = new AES(rsaKey);
		char[] rsaReal = aes.decrypt(rsa).toCharArray();

		return SSHTool.execute(targetHost, userName, command, rsaReal);
	}

	/**
	 * check git path exist
	 */
	public boolean checkGitPahtExist() throws Exception {
		File file = new File(path + "/.git");
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * clone
	 */
	public void clone(String path, String url, String branch) throws Exception {
		GitUtils.clone(config.getCredentials(), url, path, branch);
	}

	/**
	 * checkOut
	 */
	public void checkOut(String path, String branch) throws Exception {
		GitUtils.checkout(config.getCredentials(), path, branch);
	}

	/**
	 * build (maven)
	 */
	public String build(String path) throws Exception {
		String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
		// String command = "mvn -f /Users/vjay/gittest/jianzutest clean install
		// -Dmaven.test.skip=true";
		return SSHTool.exec(command);
	}

	/**
	 * @param path
	 *            -- /fingerproject/finger-auth/target/fingerauth.tar
	 * @param targetHost
	 *            -- webapps@10.100.0.253
	 * @param targetPath
	 *            -- /data/webapps/web-auth/webapps/
	 * @return
	 * @throws Exception
	 */
	public String scp(String path, String targetHost, String targetPath) throws Exception {
		String command = "scp -r " + path + " " + targetHost + ":" + targetPath;
		return SSHTool.exec(command);
	}

	/**
	 * bak local + scp + rename
	 */
	public String scpAndTar(String path, String targetHost, String userName, String targetPath, String rsa) throws Exception {
		String result = mkdirs(targetHost, userName, "/home/" + userName + "/tmp", rsa);
		// scp
		scpToTmp(path, targetHost, userName, rsa);
		// tar
		result += tarToTmp(targetHost, userName, path, rsa);
		// mkdir--real app path
		// result += mkdirs(targetHost, userName, targetPath, rsa);
		// move
		result += moveToTarPath(targetHost, userName, path, targetPath, rsa);
		return result;
	}

	public String relink(String targetHost, String targetPath, String userName, String path, String rsa) throws Exception {
		String command = "ln -snf " + targetPath + "/" + replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr() + " "
				+ project.getLinkAppHome();
		return doExecute(targetHost, userName, command, rsa);
	}

	/**
	 * scpToTmp
	 */
	public void scpToTmp(String path, String targetHost, String userName, String rsa) throws Exception {
		// String command = "scp -r " + path + " " + targetHost + ":/home/" +
		// userName + "/tmp";
		String rsaKey = config.getCipherKey();
		AES aes = new AES(rsaKey);
		char[] rsaReal = aes.decrypt(rsa).toCharArray();
		SSHTool.uploadFile(targetHost, userName, rsaReal, new File(path), "/home/" + userName + "/tmp");
	}

	/**
	 * unzip in tmp
	 */
	public String tarToTmp(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "tar -xvf /home/" + userName + "/tmp" + "/" + subPackname(path) + " -C /home/" + userName + "/tmp";
		return doExecute(targetHost, userName, command, rsa);
	}

	/**
	 * move to tar path
	 */
	public String moveToTarPath(String targetHost, String userName, String path, String targetPath, String rsa) throws Exception {
		String command = "mv /home/" + userName + "/tmp" + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
				+ replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr();
		return doExecute(targetHost, userName, command, rsa);
	}

	/**
	 * local back up
	 */
	public String backupLocal(String path) throws Exception {
		checkPath(config.getBackupPath());
		String command = "cp -Rf " + path + " " + config.getBackupPath() + "/" + subPackname(path) + getDateTimeStr();
		return SSHTool.exec(command);
	}

	/**
	 * mkdir
	 */
	public String mkdirs(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "mkdir -p " + path;
		return doExecute(targetHost, userName, command, rsa);
	}

	/**
	 * rollback
	 */
	public String rollback() throws Exception {
		return null;
	}

	/**
	 * stop
	 */
	public String stop(String targetHost, String userName, String command, String rsa) throws Exception {
		return doExecute(targetHost, userName, command, rsa);
	}

	/**
	 * start
	 */
	public String start(String targetHost, String userName, String command, String rsa) throws Exception {
		return doExecute(targetHost, userName, command, rsa);
	}

	/**
	 * restart
	 */
	public String restart(String targetHost, String userName, String command, String rsa) throws Exception {
		return doExecute(targetHost, userName, command, rsa);
	}

	public String getDateTimeStr() {
		String str = DateUtils.formatDate(now, DateUtils.YMDHM);
		str = str.substring(2, str.length());
		str = "-v" + str;
		return str;
	}

	public String subPackname(String path) {
		String[] a = path.split("/");
		return a[a.length - 1];
	}

	public String subPacknameWithOutPostfix(String path) {
		String a = subPackname(path);
		return a.substring(0, a.lastIndexOf("."));
	}

	public String replaceMaster(String str) {
		return str.replaceAll("master-", "");
	}

	public void checkPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public String getBranch() {
		return branch;
	}

	public String getPath() {
		return path;
	}

	public List<AppInstance> getInstances() {
		return instances;
	}

	public String getAlias() {
		return alias;
	}

	public String getTarName() {
		return tarName;
	}

	public DependencyService getDependencyService() {
		return dependencyService;
	}

	public List<TaskDetail> getTaskDetails() {
		return taskDetails;
	}

	public Project getProject() {
		return project;
	}
}