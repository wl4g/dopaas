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

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.ci.dto.TaskResult;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.utils.context.SpringContexts;
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
    final protected CiCdProperties config;

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
     * TaskHistoryDetails
     */
    final private List<TaskHistoryDetail> taskHistoryDetails;

    /**
     * Service
     */
    final private DependencyService dependencyService;

    /**
     * Task History
     */
    final private TaskHistory taskHistory;

    /**
     * Ref Task History , for rollback
     */
    final private TaskHistory refTaskHistory;

    /**
     * project
     */
    final private Project project;

    /**
     * now
     */
    final private Date now = new Date();

    /**
     * sha
     */
    protected String shaGit;
    /**
     * md5
     */
    protected String shaLocal;

    /**
     * is success , if fail , Stop running
     */
    //protected Boolean isSuccess = new Boolean(true);

    /**
     * result
     */
    //protected StringBuffer result = new StringBuffer();

    protected TaskResult taskResult = new TaskResult();

    public BasedDeployProvider(Project project, String path, String branch, String alias, List<AppInstance> instances, TaskHistory taskHistory, TaskHistory refTaskHistory,
                               List<TaskHistoryDetail> taskHistoryDetails) {
        this.config = SpringContexts.getBean(CiCdProperties.class);
        this.path = path;
        this.branch = branch;
        this.alias = alias;
        this.instances = instances;
        this.taskHistory = taskHistory;
        this.refTaskHistory = refTaskHistory;
        this.taskHistoryDetails = taskHistoryDetails;
        String[] a = project.getTarPath().split("/");
        this.tarName = a[a.length - 1];
        this.project = project;
        this.dependencyService = SpringContexts.getBean(DependencyService.class);
    }

    /**
     * Execute
     */
    public abstract void execute() throws Exception;

    /**
     * Exce command
     */
    public String exceCommand(String targetHost, String userName, String command, String rsa) throws Exception {
        String rsaKey = config.getCipherKey();
        AES aes = new AES(rsaKey);
        char[] rsaReal = aes.decrypt(rsa).toCharArray();
        String result = command + "\n";
        result += SSHTool.execute(targetHost, userName, command, rsaReal);
        return result;
    }


    /**
     *  Scp + tar + move to basePath
     */
    public String scpAndTar(String path, String targetHost, String userName, String targetPath, String rsa) throws Exception {
        String result = mkdirs(targetHost, userName, "/home/" + userName + "/tmp", rsa) + "\n";
        // scp
        result += scpToTmp(path, targetHost, userName, rsa) + "\n";
        // tar
        result += tarToTmp(targetHost, userName, path, rsa) + "\n";
        // mkdir--real app path
        // result += mkdirs(targetHost, userName, targetPath, rsa);
        // move
        result += moveToTarPath(targetHost, userName, path, targetPath, rsa) + "\n";
        return result;
    }

    /**
     * Relink
     */
    public String relink(String targetHost, String targetPath, String userName, String path, String rsa) throws Exception {
        String command = "ln -snf " + targetPath + "/" + replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr() + " "
                + project.getLinkAppHome();
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Scp To Tmp
     */
    public String scpToTmp(String path, String targetHost, String userName, String rsa) throws Exception {
        String rsaKey = config.getCipherKey();
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
     * Move to tar path
     */
    public String moveToTarPath(String targetHost, String userName, String path, String targetPath, String rsa) throws Exception {
        String command = "mv /home/" + userName + "/tmp" + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
                + replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr();
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Local back up
     */
    public String backupLocal(String path, String sign) throws Exception {
        checkPath(config.getBackupPath());
        String command = "cp -Rf " + path + " " + config.getBackupPath() + "/" + subPackname(path) + "#" + sign;
        return SSHTool.exec(command);
    }

    /**
     * Get local back up , for rollback
     */
    public String getBackupLocal(String backFile, String target) throws Exception {
        checkPath(config.getBackupPath());
        String command = "cp -Rf " + backFile + " " + target;
        return SSHTool.exec(command);
    }

    /**
     * Mkdir
     */
    public String mkdirs(String targetHost, String userName, String path, String rsa) throws Exception {
        String command = "mkdir -p " + path;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Rollback
     */
    public void rollback() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Docker build
     */
    public String dockerBuild(String path) throws Exception{
        String command = "mvn -f " + path + "/pom.xml -Pdocker:push dockerfile:build  dockerfile:push -Ddockerfile.username="
                +config.getDockerPushUsername()+" -Ddockerfile.password="+config.getDockerPushPasswd();
        return SSHTool.exec(command);
    }

    /**
     * Docker pull
     */
    public String dockerPull(String targetHost, String userName, String imageName, String rsa) throws Exception{
        String command = "docker pull "+imageName;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Docker stop
     */
    public String dockerStop(String targetHost, String userName, String groupName, String rsa) throws Exception{
        String command = "docker stop "+groupName;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Docker remove container
     */
    public String dockerRemoveContainer(String targetHost, String userName, String groupName, String rsa) throws Exception{
        String command = "docker rm "+groupName;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Docker Run
     */
    public String dockerRun(String targetHost, String userName, String runCommand, String rsa) throws Exception{
        return exceCommand(targetHost, userName, runCommand, rsa);
    }

    /**
     * Get date to string user for version
     */
    public String getDateTimeStr() {
        String str = DateUtils.formatDate(now, DateUtils.YMDHM);
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

    public List<TaskHistoryDetail> getTaskHistoryDetails() {
        return taskHistoryDetails;
    }

    public Project getProject() {
        return project;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public String getShaGit() {
        return shaGit;
    }

    public void setShaGit(String shaGit) {
        this.shaGit = shaGit;
    }

    public String getShaLocal() {
        return shaLocal;
    }

    public void setShaLocal(String shaLocal) {
        this.shaLocal = shaLocal;
    }

    public TaskHistory getTaskHistory() {
        return taskHistory;
    }

    public TaskHistory getRefTaskHistory() {
        return refTaskHistory;
    }
}