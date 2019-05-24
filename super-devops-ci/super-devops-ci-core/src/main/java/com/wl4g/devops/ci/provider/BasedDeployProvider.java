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
package com.wl4g.devops.ci.provider;

import com.wl4g.devops.ci.config.DevConfig;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.utils.SSHTools;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.utils.codec.AES;
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

	// branch name
	private String branch;
	// branch path
	private String path;
	// branch url
	private String url;
	// project alias
	private String alias;
	// for example:/super-devops-iam-security/target/demo.tar
	private String tarPath;
	// tarName,for example : demo.tar / demo.jar
	private String tarName;
	// instances
	private List<AppInstance> instances;
	// taskDetails
	private List<TaskDetail> taskDetails;
	// projectId
	private Integer projectId;
	// service
	private DependencyService dependencyService;
	// config
	private DevConfig devConfig;
	// now
	private Date now = new Date();

	public BasedDeployProvider(DependencyService dependencyService, DevConfig devConfig, Integer projectId, String path,
			String url, String branch, String alias, String tarPath, List<AppInstance> instances, List<TaskDetail> taskDetails) {
		this.path = path;
		this.url = url;
		this.branch = branch;
		this.alias = alias;
		this.tarPath = tarPath;
		this.instances = instances;
		this.taskDetails = taskDetails;
		String[] a = tarPath.split("/");
		this.tarName = a[a.length - 1];
		this.projectId = projectId;

		// service
		this.dependencyService = dependencyService;
		// devConfig
		this.devConfig = devConfig;
	}

	/**
	 * hook
	 */
	abstract public void exec() throws Exception;

	/**
	 * exce command
	 */
	public String run(String command) throws Exception {
		return SSHTools.runLocal(command);
	}

	/**
	 * exce command
	 */
	public String execute(String targetHost, String userName, String command, String rsa) throws Exception {

		String rsaKey = DevConfig.getRsaKey();
		AES aes = new AES(rsaKey);
		char[] rsaReal = aes.decrypt(rsa).toCharArray();

		return SSHTools.execute(targetHost, userName, command, rsaReal);
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
		GitUtils.clone(url, path, branch);
	}

	/**
	 * checkOut
	 */
	public void checkOut(String path, String branch) throws Exception {
		GitUtils.checkout(path, branch);
	}

	/**
	 * build (maven)
	 */
	public String build(String path) throws Exception {
		String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
		// String command = "mvn -f /Users/vjay/gittest/jianzutest clean install
		// -Dmaven.test.skip=true";
		return run(command);
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
		return run(command);

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

	public String reLink(String targetHost, String targetPath, String userName, String path, String rsa) throws Exception {
		String command = "ln -snf " + targetPath + "/" + replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr() + " "
				+ DevConfig.linkPath + "/" + alias + "-package/" + alias + "-current";
		return execute(targetHost, userName, command, rsa);
	}

	/**
	 * scpToTmp
	 */
	public void scpToTmp(String path, String targetHost, String userName, String rsa) throws Exception {
		// String command = "scp -r " + path + " " + targetHost + ":/home/" +
		// userName + "/tmp";
		String rsaKey = DevConfig.getRsaKey();
		AES aes = new AES(rsaKey);
		char[] rsaReal = aes.decrypt(rsa).toCharArray();
		SSHTools.uploadFile(targetHost, userName, rsaReal, new File(path), "/home/" + userName + "/tmp");
	}

	/**
	 * unzip in tmp
	 */
	public String tarToTmp(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "tar -xvf /home/" + userName + "/tmp" + "/" + subPackname(path) + " -C /home/" + userName + "/tmp";
		return execute(targetHost, userName, command, rsa);
	}

	/**
	 * move to tar path
	 */
	public String moveToTarPath(String targetHost, String userName, String path, String targetPath, String rsa) throws Exception {
		String command = "mv /home/" + userName + "/tmp" + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
				+ replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr();
		return execute(targetHost, userName, command, rsa);
	}

	/**
	 * local back up
	 */
	public String backupLocal(String path) throws Exception {
		checkPath(DevConfig.bakPath);
		String command = "cp -Rf " + path + " " + DevConfig.bakPath + "/" + subPackname(path) + getDateTimeStr();
		return run(command);
	}

	/**
	 * mkdir
	 */
	public String mkdirs(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "mkdir -p " + path;
		return execute(targetHost, userName, command, rsa);
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
		return execute(targetHost, userName, command, rsa);

	}

	/**
	 * start
	 */
	public String start(String targetHost, String userName, String command, String rsa) throws Exception {
		return execute(targetHost, userName, command, rsa);
	}

	/**
	 * restart
	 */
	public String restart(String targetHost, String userName, String command, String rsa) throws Exception {
		return execute(targetHost, userName, command, rsa);
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

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTarName() {
		return tarName;
	}

	public void setTarName(String tarName) {
		this.tarName = tarName;
	}

	public String getTarPath() {
		return tarPath;
	}

	public void setTarPath(String tarPath) {
		this.tarPath = tarPath;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public DependencyService getDependencyService() {
		return dependencyService;
	}

	public void setDependencyService(DependencyService dependencyService) {
		this.dependencyService = dependencyService;
	}

	public DevConfig getDevConfig() {
		return devConfig;
	}

	public void setDevConfig(DevConfig devConfig) {
		this.devConfig = devConfig;
	}

	public List<TaskDetail> getTaskDetails() {
		return taskDetails;
	}

	public void setTaskDetails(List<TaskDetail> taskDetails) {
		this.taskDetails = taskDetails;
	}

}