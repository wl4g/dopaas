/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;
import com.wl4g.devops.common.exception.ci.LockStateException;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.utils.io.FileIOUtils;
import com.wl4g.devops.shell.utils.ShellContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;

/**
 * Abstract based deploy provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
public abstract class BasedMavenPipelineProvider extends AbstractPipelineProvider {
	final protected Logger log = LoggerFactory.getLogger(getClass());


	public BasedMavenPipelineProvider(PipelineInfo info) {
		super(info);
	}

	/**
	 * Execute
	 */
	public abstract void execute() throws Exception;


	/**
	 * Scp + tar + move to basePath
	 */
	public String scpAndTar(String path, String targetHost, String userName, String targetPath, String rsa) throws Exception {
		String result = mkdirs(targetHost, userName, "/home/" + userName + "/tmp", rsa) + "\n";
		// scp
		result += scpToTmp(path, targetHost, userName, rsa) + "\n";
		// tar
		result += tarToTmp(targetHost, userName, path, rsa) + "\n";
		// remove
		result += removeTarPath(targetHost, userName, path, targetPath, rsa);
		// move
		result += moveToTarPath(targetHost, userName, path, targetPath, rsa) + "\n";
		return result;
	}


	/**
	 * Scp To Tmp
	 */
	public String scpToTmp(String path, String targetHost, String userName, String rsa) throws Exception {
		String rsaKey = config.getTranform().getCipherKey();
		AES aes = new AES(rsaKey);
		char[] rsaReal = aes.decrypt(rsa).toCharArray();
		return SSHTool.uploadFile(targetHost, userName, rsaReal, new File(path), "/home/" + userName + "/tmp");
	}

	/**
	 * Unzip in tmp
	 */
	public String tarToTmp(String targetHost, String userName, String path, String rsa) throws Exception {
		String command = "tar -xvf /home/" + userName + "/tmp" + "/" + subPackname(path) + " -C /home/" + userName + "/tmp";
		return exceCommand(targetHost, userName, command, rsa);
	}

	/**
	 * remove tar path
	 */
	public String removeTarPath(String targetHost, String userName, String path, String targetPath, String rsa) throws Exception {
		String s = targetPath + "/" + subPacknameWithOutPostfix(path);
		if (StringUtils.isBlank(s) || s.trim().equals("/")) {
			throw new RuntimeException("bad command");
		}
		String command = "rm -Rf " + targetPath + "/" + subPacknameWithOutPostfix(path);
		return exceCommand(targetHost, userName, command, rsa);
	}

	/**
	 * Move to tar path
	 */
	public String moveToTarPath(String targetHost, String userName, String path, String targetPath, String rsa) throws Exception {
		String command = "mv /home/" + userName + "/tmp" + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
				+ subPacknameWithOutPostfix(path);
		return exceCommand(targetHost, userName, command, rsa);
	}

	/**
	 * Local back up
	 */
	public String backupLocal(String path, String alias, String branchName) throws Exception {
		checkPath(config.getJob().getBackupDir(getPipelineInfo().getTaskHistory().getId()));
		String command = "cp -Rf " + path + " " + config.getJob().getBackupDir(getPipelineInfo().getTaskHistory().getId()) + "/"
				+ subPackname(path);
		return SSHTool.exec(command);
	}

	/**
	 * Get local back up , for rollback
	 */
	public String getBackupLocal(String backFile, String target) throws Exception {
		checkPath(config.getJob().getBackupDir(getPipelineInfo().getTaskHistory().getId()));
		String command = "cp -Rf " + backFile + " " + target;
		return SSHTool.exec(command);
	}

	/**
	 * Building (maven)
	 */
	public String mvnInstall(String path, TaskResult taskResult) throws Exception {
		// Execution mvn
		String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
		return SSHTool.exec(command, inlog -> !ShellContextHolder.isInterruptIfNecessary(), taskResult);
	}

	/**
	 * Building (maven)
	 */
	public String mvnInstall(String path, TaskResult taskResult, String logPath,String buildCommand) throws Exception {
		// Execution mvn
		String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true | tee -a " + logPath;
		return SSHTool.exec(command, inlog -> !ShellContextHolder.isInterruptIfNecessary(), taskResult);
	}

	/**
	 * Rollback
	 */
	public void rollback() throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get date to string user for version
	 */
	public String getDateTimeStr() {
		String str = DateUtils.formatDate(new Date(), DateUtils.YMDHM);
		str = str.substring(2);
		str = "-v" + str;
		return str;
	}

	/**
	 * Get Package Name from path
	 */
	public String subPackname(String path) {
		String[] a = path.split("/");
		return a[a.length - 1];
	}

	/**
	 * Get Packname WithOut Postfix from path
	 */
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

	/**
	 * build
	 *
	 * @param taskHistory
	 * @param taskResult
	 * @throws Exception
	 */
	public void build(TaskHistory taskHistory, TaskResult taskResult, boolean isRollback) throws Exception {
		//get dependencys
		LinkedHashSet<Dependency> dependencys = dependencyService.getDependencys(taskHistory.getProjectId(), null);
		//get task build commands
		List<TaskBuildCommand> taskBuildCommands = taskHisBuildCommandDao.selectByTaskHisId(taskHistory.getId());
		String logPath = config.getJob().getLogBaseDir(taskHistory.getId()) + "/"  + "build.log";
		//write log file -- <[EOF]
		FileIOUtils.writeFile(new File(logPath),LOG_FILE_START);

		log.info("Analysis dependencys={}", dependencys);
		for (Dependency dependency : dependencys) {
			String buildCommand = getBuildCommand(taskBuildCommands, dependency.getProjectId());
			checkLock(taskHistory, dependency.getDependentId(), dependency.getDependentId(), dependency.getBranch(), taskResult,
					true, isRollback,buildCommand);
			// Is Continue ? if fail then return
			if (!taskResult.isSuccess()) {
				return;
			}
		}
		checkLock(taskHistory, taskHistory.getProjectId(), null, taskHistory.getBranchName(), taskResult, false, isRollback,taskHistory.getBuildCommand());

		//write log file -- [EOF]>
		FileIOUtils.writeFile(new File(logPath),LOG_FILE_END);
	}

	private String getBuildCommand(List<TaskBuildCommand> taskBuildCommands,Integer projectId){
		Assert.notEmpty(taskBuildCommands,"taskBuildCommands is empty");
		Assert.notNull(projectId,"projectId is null");
		for(TaskBuildCommand taskBuildCommand : taskBuildCommands){
			if(taskBuildCommand.getProjectId().intValue()==projectId.intValue()){
				return taskBuildCommand.getCommand();
			}
		}
		return null;
	}

	private void checkLock(TaskHistory taskHistory, Integer projectId, Integer dependencyId, String branch, TaskResult taskResult,
			boolean isDependency, boolean isRollback,String buildCommand) throws Exception {
		// ===== redis lock =====
		Lock lock = lockManager.getLock(CI_LOCK + projectId, LOCK_TIME, TimeUnit.MINUTES);
		if (lock.tryLock()) {// needn't wait
			// Do
			try {
				getSourceAndMvnBuild(taskHistory, projectId, dependencyId, branch, taskResult, isDependency, isRollback,buildCommand);
			} finally {
				lock.unlock();
			}
		} else {
			log.info("One Task is running , just waiting and do nothing");
			try {
				if (lock.tryLock(LOCK_TIME, TimeUnit.MINUTES)) {// Wait
					log.info("The task is finish , jemp this project build");
				} else {
					// One Task is running , and Waiting timeout
					throw new LockStateException("One Task is running ,Waiting timeout");
					// TODO
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				lock.unlock();
			}
		}
	}

	private void getSourceAndMvnBuild(TaskHistory taskHistory, Integer projectId, Integer dependencyId, String branch,
			TaskResult taskResult, boolean isDependency, boolean isRollback,String buildCommand) throws Exception {
		log.info("build start projectId={}", projectId);
		Project project = projectDao.selectByPrimaryKey(projectId);
		Assert.notNull(project, "project not exist");

		String path = config.getVcs().getGitlab().getWorkspace() + "/" + project.getProjectName();

		if (isRollback) {
			String sha;
			if (isDependency) {
				TaskSign taskSign = taskSignDao.selectByDependencyIdAndTaskId(dependencyId, taskHistory.getRefId());
				Assert.notNull(taskSign, "not found taskSign");
				sha = taskSign.getShaGit();
			} else {
				sha = taskHistory.getShaGit();
			}

			if (GitUtils.checkGitPath(path)) {
				GitUtils.rollback(config.getVcs().getGitlab().getCredentials(), path, sha);
				taskResult.getStringBuffer().append("project rollback success:").append(project.getProjectName()).append("\n");
			} else {
				GitUtils.clone(config.getVcs().getGitlab().getCredentials(), project.getGitUrl(), path, branch);
				taskResult.getStringBuffer().append("project clone success:").append(project.getProjectName()).append("\n");
				GitUtils.rollback(config.getVcs().getGitlab().getCredentials(), path, sha);
				taskResult.getStringBuffer().append("project rollback success:").append(project.getProjectName()).append("\n");
			}
		} else {
			if (GitUtils.checkGitPath(path)) {// 若果目录存在则:chekcout 分支 并 pull
				GitUtils.checkout(config.getVcs().getGitlab().getCredentials(), path, branch);
				taskResult.getStringBuffer().append("project checkout success:").append(project.getProjectName()).append("\n");
			} else { // 若目录不存在: 则clone 项目并 checkout 对应分支
				GitUtils.clone(config.getVcs().getGitlab().getCredentials(), project.getGitUrl(), path, branch);
				taskResult.getStringBuffer().append("project clone success:").append(project.getProjectName()).append("\n");
			}
		}

		// save dependency git sha -- 保存依赖项目的sha，用于回滚时找回对应的 历史依赖项目
		if (isDependency) {
			TaskSign taskSign = new TaskSign();
			taskSign.setTaskId(taskHistory.getId());
			taskSign.setDependenvyId(dependencyId);
			taskSign.setShaGit(GitUtils.getLatestCommitted(path));
			taskSignDao.insertSelective(taskSign);
		}

		String logPath = config.getJob().getLogBaseDir(taskHistory.getId()) + "/" +"build.log";
		// run install command
		String defaultCommand = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true | tee -a " + logPath;

		String installResult = "";
		if(StringUtils.isBlank(buildCommand)){
			installResult = SSHTool.exec(defaultCommand,inlog -> !ShellContextHolder.isInterruptIfNecessary(),taskResult);
		}else{
			String filePath = config.getJob().getBaseDir()+ "/" + taskHistory.getId() + "/" + "build_"+ project.getId()+".sh | tee -a " + logPath;
			buildCommand = commandReplace(buildCommand,path);
			installResult = SSHTool.execFile(buildCommand,inlog -> !ShellContextHolder.isInterruptIfNecessary(),filePath,taskResult);
		}

		// ===== build end =====
		taskResult.getStringBuffer().append(installResult);

	}



}