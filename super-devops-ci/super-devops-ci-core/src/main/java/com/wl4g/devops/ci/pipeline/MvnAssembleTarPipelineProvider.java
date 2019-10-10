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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.pipeline.handler.MvnAssembleTarPipelineHandler;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskSign;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.exception.ci.LockStateException;
import com.wl4g.devops.common.utils.codec.FileCodec;
import org.springframework.util.Assert;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.CI_LOCK;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOCK_TIME;

/**
 * MAVEN assemble tar provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
public class MvnAssembleTarPipelineProvider extends AbstractPipelineProvider {

	public MvnAssembleTarPipelineProvider(PipelineInfo info) {
		super(info);
	}

	/**
	 * Execution build and deploy
	 */
	@Override
	public void execute() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getProject().getId());
		// maven install , include dependency
		build(getTaskHistory() ,taskResult,false);//TODO 修改后要测试
		if (!taskResult.isSuccess()) {
			return;
		}
		// get git sha
		setShaGit(GitUtils.getLatestCommitted(getPath()));
		deploy();
	}

	/**
	 * Roll-back
	 */
	@Override
	public void rollback() throws Exception {
		Dependency dependency = new Dependency();
		dependency.setProjectId(getProject().getId());
		// Old file
		String oldFilePath = config.getBackupPath() + "/" + subPackname(getProject().getTarPath()) + "#"
				+ getTaskHistory().getRefId();
		File oldFile = new File(oldFilePath);
		if (oldFile.exists()) {// Check bakup file isExist , if not -- check out
								// from git
			getBackupLocal(oldFilePath, getPath() + getProject().getTarPath());
			setShaGit(getRefTaskHistory().getShaGit());
		} else {
			//getDependencyService().rollback(getTaskHistory(), getRefTaskHistory(), dependency, getBranch(), taskResult, false);
			build(getTaskHistory() ,taskResult,true);
			setShaGit(GitUtils.getLatestCommitted(getPath()));
		}
		deploy();
	}

	/**
	 * Deploy
	 */
	private void deploy() throws Exception {
		// get local sha
		setShaLocal(FileCodec.getFileMD5(new File(getPath() + getProject().getTarPath())));
		// backup in local
		backupLocal(getPath() + getProject().getTarPath(), getTaskHistory().getId().toString());
		// scp to server
		for (AppInstance instance : getInstances()) {
			// create deploy task
			Runnable task = new MvnAssembleTarPipelineHandler(this, getProject(), getPath(), instance, getProject().getTarPath(),
					getTaskHistoryDetails());
			Thread t = new Thread(task);
			t.start();
			t.join();
		}

		if (log.isInfoEnabled()) {
			log.info("Maven assemble deploy done!");
		}
	}

	//====

	/**
	 * build
	 * @param taskHistory
	 * @param taskResult
	 * @throws Exception
	 */
	public void build(TaskHistory taskHistory, TaskResult taskResult, boolean isRollback) throws Exception{

		LinkedHashSet<Dependency> dependencys = dependencyService.getDependencys(taskHistory.getProjectId(), null);
		Dependency[] dependencys2 = (Dependency[]) dependencys.toArray();

		for (int i = dependencys2.length - 1; i >= 0; i--) {
			Dependency dependency1 = dependencys2[i];
			checkLock(taskHistory,dependency1.getProjectId(),dependency1.getDependentId(),dependency1.getBranch(),taskResult,true,isRollback);
			// Is Continue ? if fail then return
			if (!taskResult.isSuccess()) {
				return;
			}
		}
		checkLock(taskHistory,taskHistory.getProjectId(),null,taskHistory.getBranchName(),taskResult,false,isRollback);

	}


	private void checkLock(TaskHistory taskHistory, Integer projectId,Integer dependencyId, String branch, TaskResult taskResult, boolean isDependency,boolean isRollback)throws Exception{
		// ===== redis lock =====
		Lock lock = lockManager.getLock(CI_LOCK+projectId, LOCK_TIME, TimeUnit.MINUTES);
		if(lock.tryLock()){// needn't wait
			//Do
			try {
				getSourceAndMvnBuild(taskHistory,projectId,dependencyId,branch,taskResult,isDependency,isRollback);
			}finally {
				lock.unlock();
			}
		}else{
			log.info("One Task is running , just waiting and do nothing");
			try {
				if (lock.tryLock(LOCK_TIME, TimeUnit.MINUTES)) {//Wait
					log.info("The task is finish , jemp this project build");
				} else {
					//One Task is running , and Waiting timeout
					throw new LockStateException("One Task is running ,Waiting timeout");
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				lock.unlock();
			}
		}
	}

	private void getSourceAndMvnBuild(TaskHistory taskHistory, Integer projectId, Integer dependencyId, String branch, TaskResult taskResult, boolean isDependency, boolean isRollback) throws Exception{
		log.info("build start projectId={}", projectId);
		Project project = projectDao.selectByPrimaryKey(projectId);
		Assert.notNull(project, "project not exist");


		String path = config.getGitBasePath() + "/" + project.getProjectName();

		if(isRollback){
			String sha;
			if (isDependency) {
				TaskSign taskSign = taskSignDao.selectByDependencyIdAndTaskId(dependencyId, taskHistory.getRefId());
				Assert.notNull(taskSign, "not found taskSign");
				sha = taskSign.getShaGit();
			} else {
				sha = taskHistory.getShaGit();
			}

			if (GitUtils.checkGitPath(path)) {
				GitUtils.rollback(config.getCredentials(), path, sha);
				taskResult.getStringBuffer().append("project rollback success:").append(project.getProjectName()).append("\n");
			} else {
				GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, branch);
				taskResult.getStringBuffer().append("project clone success:").append(project.getProjectName()).append("\n");
				GitUtils.rollback(config.getCredentials(), path, sha);
				taskResult.getStringBuffer().append("project rollback success:").append(project.getProjectName()).append("\n");
			}
		}else{
			if (GitUtils.checkGitPath(path)) {// 若果目录存在则:chekcout 分支 并 pull
				GitUtils.checkout(config.getCredentials(), path, branch);
				taskResult.getStringBuffer().append("project checkout success:").append(project.getProjectName()).append("\n");
			} else { // 若目录不存在: 则clone 项目并 checkout 对应分支
				GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, branch);
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

		// run install command
		String installResult = mvnInstall(path, taskResult);

		// ===== build end =====
		taskResult.getStringBuffer().append(installResult);

	}


}