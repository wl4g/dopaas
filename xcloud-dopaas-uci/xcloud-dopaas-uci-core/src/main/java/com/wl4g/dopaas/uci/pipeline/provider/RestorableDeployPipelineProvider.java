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
package com.wl4g.dopaas.uci.pipeline.provider;

import com.wl4g.component.common.io.CompressUtils;
import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.support.cli.command.DestroableCommand;
import com.wl4g.component.support.cli.command.LocalDestroableCommand;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;
import com.wl4g.dopaas.common.bean.uci.PipeStepApi;
import com.wl4g.dopaas.common.bean.uci.Project;
import com.wl4g.dopaas.common.bean.uci.model.ActionControl;
import com.wl4g.dopaas.common.exception.ci.NotFoundBackupAssetsFileException;
import com.wl4g.dopaas.uci.core.context.PipelineContext;
import com.wl4g.dopaas.uci.pipeline.provider.container.DockerNativePipelineProvider;
import com.wl4g.dopaas.uci.pipeline.provider.model.MetaModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.component.common.codec.FingerprintUtils.getMd5Fingerprint;
import static com.wl4g.dopaas.uci.pipeline.provider.PipelineProvider.PipelineKind.DOCKER_NATIVE;
import static com.wl4g.dopaas.uci.utils.PipelineUtils.ensureDirectory;
import static java.lang.String.format;

/**
 * Recoverable deployment pipeline provider based on physical backup (local
 * disk/cloud service).
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月12日
 * @since
 */
public abstract class RestorableDeployPipelineProvider extends GenericDependenciesPipelineProvider {

	public RestorableDeployPipelineProvider(PipelineContext context) {
		super(context);
	}

	@Override
	protected void postBuiltModulesDependencies() throws Exception {
		// Source code fingerprint.
		// TODO 应获取可打包的主项目project对象
		setSourceFingerprint(getVcsOperator(getContext().getProject()).getLatestCommitted(getContext().getProjectSourceDir()));

		// Assets file fingerprint.
		String assetsFilename = config.getAssetsFullFilename(getContext().getPipeline().getAssetsDir(),
				getContext().getAppCluster().getName());
		File assetsFile = new File(getContext().getProjectSourceDir() + assetsFilename);
		if (assetsFile.exists()) {
			setAssetsFingerprint(getMd5Fingerprint(assetsFile));
		}

		//build Meta
		buildMeta();

		// build api document
		buildApi();

		// Handling backup
		handleDiskBackupAssets();

		// Handing Build Image
		buildImage();

		// skip deploy
		ActionControl actionControl = getContext().getActionControl();
		if (Objects.nonNull(actionControl) && !actionControl.isDeploy()) {
			return;
		}

		// Deploying to remote instances.
		startExecutionDeploying();
	}

	private void buildMeta() {
		String assetsFilePath = getContext().getProjectSourceDir() + config.getAssetsFullFilename(getContext().getPipeline().getAssetsDir(),
				getContext().getAppCluster().getName());

		String tempMetaFilePath = config.getJobBaseDir(getContext().getPipelineHistory().getId()).getAbsolutePath() + "/.uci-meta.json";

		// Step1 : build Meta model
		MetaModel model = new MetaModel();

		MetaModel.BuildInfo buildInfo = model.getBuildInfo();
		buildInfo.setMd5(getAssetsFingerprint());
		buildInfo.setServiceName(getContext().getAppCluster().getName());
		buildInfo.setTotalBytes(FileUtils.sizeOf(new File(assetsFilePath)));


		MetaModel.SourceInfo sourceInfo = model.getSourceInfo();
		sourceInfo.setCommitId(getSourceFingerprint());
		sourceInfo.setBranchOrTag(getContext().getPipeline().getPipeStepBuilding().getRef());
		sourceInfo.setProjectUrl(getContext().getProject().getHttpUrl());
		sourceInfo.setTimestamp(System.currentTimeMillis());
		sourceInfo.setComment(getContext().getPipelineHistory().getRemark());

		MetaModel.PcmInfo pcmInfo = model.getPcmInfo();
		pcmInfo.setPcmIssuesId(getContext().getPipelineHistory().getTrackId());
		pcmInfo.setPcmIssuesSubject(getContext().getPipelineHistory().getPipeName());
		pcmInfo.setPcmProjectName(getContext().getPipelineHistory().getPipeName());

		MetaModel.DeployInfo deployInfo = model.getDeployInfo();
		List<String> hosts = new ArrayList<>();
		for(AppInstance instance : getContext().getInstances()){
			hosts.add(instance.getHostname());
		}
		deployInfo.setHosts(hosts);

		String content = JacksonUtils.toJSONString(model);
		FileIOUtils.writeFile(new File(tempMetaFilePath), content,false);

		try {
			// 写第一个文件
			CompressUtils.appendToTar(assetsFilePath, tempMetaFilePath,".uci-meta.json");
		}catch (Exception e){
			log.error("append file to tar error", e);
		}

	}

	private void buildImage() throws Exception {
		if (getContext().getAppCluster().getDeployType() == 2) {
			DockerNativePipelineProvider p = namingBeanFactory.getPrototypeBean(DOCKER_NATIVE, getContext());
			p.buildImage();
		}
	}

	private void buildApi() {
		// TODO read json
		try {
			Project project = getContext().getProject();
			String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
			String jsonFilePath = projectDir + getContext().getPipeline().getAssetsDir()
					+ "/generated-docs/swagger-swagger2-by-springfox.json";
			File file = new File(jsonFilePath);
			if(!file.exists()){
				return;
			}
			String json = FileIOUtils.readFileToString(file, "UTF-8");
			PipeStepApi pipeStepApi = getContext().getPipeStepApi();
			enterpriseApiService.importApiAndUpdateVersion("SWAGGER2", json, pipeStepApi.getRepositoryId());
		} catch (IOException e) {
			log.error("build api fail", e);
		}
	}

	/**
	 * Roll-back
	 */
	@Override
	public void rollback() throws Exception {
		// Obtain backup assets file.
		String path = config.getJobBackupDir(getContext().getPipelineHistory().getRefId()).getAbsolutePath() + "/"
				+ config.getTarFileNameWithTar(getContext().getAppCluster().getName());
		File backAssetsFile = new File(path);
		// Check backup assets file.
		if (!backAssetsFile.exists()) {
			throw new NotFoundBackupAssetsFileException(String.format("Not found backup assets file: %s", backAssetsFile));
		}

		// Direct using backup disk.
		rollbackBackupAssets();

		// Deploying to remote instances.
		startExecutionDeploying();
	}

	/**
	 * Handling assets backup to disk, The default implements is to copy the
	 * asset files to the local shared disk. </br>
	 * For example, the docker based deployment should be backed up to the
	 * docker server image repository.
	 * 
	 * @throws Exception
	 */
	protected void handleDiskBackupAssets() throws Exception {
		Long taskHisId = getContext().getPipelineHistory().getId();
		String assetsFilename = config.getAssetsFullFilename(getContext().getPipeline().getAssetsDir(),
				getContext().getAppCluster().getName());
		String tarFileName = config.getTarFileNameWithTar(getContext().getAppCluster().getName());
		String targetPath = getContext().getProjectSourceDir() + assetsFilename;
		String backupPath = config.getJobBackupDir(taskHisId).getAbsolutePath() + "/" + tarFileName;

		// Ensure backup directory.
		ensureDirectory(config.getJobBackupDir(taskHisId).getAbsolutePath());

		// Copy assets files to backup dir.
		String backupCpCommand = format("cp -Rf %s %s", targetPath, backupPath);
		log.info(writeBuildLog("Backing up assets file command: %s", backupCpCommand));

		// TODO timeoutMs?
		File jobLogFile = config.getJobLog(taskHisId);
		DestroableCommand cmd = new LocalDestroableCommand(String.valueOf(taskHisId), backupCpCommand, null, 300000L)
				.setStdout(jobLogFile).setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

	/**
	 * Roll-back backup assets files.
	 * 
	 * @throws Exception
	 */
	protected void rollbackBackupAssets() throws Exception {
		Long taskHisRefId = getContext().getPipelineHistory().getRefId();
		Long taskHisId = getContext().getPipelineHistory().getId();
		String tarFileName = config.getTarFileNameWithTar(getContext().getAppCluster().getName());
		String backupPath = config.getJobBackupDir(taskHisRefId).getAbsolutePath() + "/" + tarFileName;
		String newBackupPath = config.getJobBackupDir(taskHisId).getAbsolutePath() + "/" + tarFileName;

		// Copy backup assets to build dir.
		String rollbackCpCmd = format("cp -Rf %s %s", backupPath, newBackupPath);
		log.info(writeBuildLog("", rollbackCpCmd));

		// TODO timeoutMs/jobLogFile?
		File jobLogFile = config.getJobLog(taskHisRefId);
		DestroableCommand cmd = new LocalDestroableCommand(rollbackCpCmd, null, 300000L).setStdout(jobLogFile)
				.setStderr(jobLogFile);
		pm.execWaitForComplete(cmd);
	}

}