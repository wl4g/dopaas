package com.wl4g.devops.ci.subject;

import com.wl4g.devops.ci.devtool.ConnectLinuxCommand;
import com.wl4g.devops.ci.devtool.DevConfig;
import com.wl4g.devops.ci.devtool.GitUtil;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.utils.codec.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
@Component
public abstract class BaseSubject {
    final protected Logger log = LoggerFactory.getLogger(BaseSubject.class);

    //branch name
    protected String branch;
    //branch path
    protected String path;
    //branch url
    protected String url;
    //project alias
    protected String alias;
    // for example:/super-devops-iam-security/target/demo.tar
    protected String tarPath;
    //tarName,for example : demo.tar / demo.jar
    protected String tarName;
    //instances
    protected List<AppInstance> instances;
    //taskDetails
    protected List<TaskDetail> taskDetails;
    //projectId
    protected Integer projectId;


    //service
    protected DependencyService dependencyService;
    //config
    protected DevConfig devConfig;

    //now
    protected Date now = new Date();

    public BaseSubject() {

    }

    /**
     * hook
     */
    abstract public void exec() throws Exception;

    /**
     * exce command
     */
    public String run(String command) throws Exception {
        return ConnectLinuxCommand.runLocal(command);
    }

    /**
     * exce command
     */
    public String execute(String targetHost, String userName, String command, String rsa) throws Exception {

        String rsaKey = devConfig.rsaKey;
        AES aes = new AES(rsaKey);
        char[] rsaReal = aes.decrypt(rsa).toCharArray();

        return ConnectLinuxCommand.execute(targetHost, userName, command, rsaReal);
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
        GitUtil.clone(url, path, branch);
    }

    /**
     * checkOut
     */
    public void checkOut(String path, String branch) throws Exception {
        GitUtil.checkout(path, branch);
    }


    /**
     * build (maven)
     */
    public String build(String path) throws Exception {
        String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
        //String command = "mvn -f /Users/vjay/gittest/jianzutest clean install -Dmaven.test.skip=true";
        return run(command);
    }

    /**
     * @param path       -- /fingerproject/finger-auth/target/fingerauth.tar
     * @param targetHost -- webapps@10.100.0.253
     * @param targetPath -- /data/webapps/web-auth/webapps/
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
        result += scpToTmp(path, targetHost, userName,rsa);
        result += tarToTmp(targetHost, userName, path, rsa);
        result += mkdirs(targetHost, userName, targetPath, rsa);
        result += moveToTarPath(targetHost, userName, path, targetPath, rsa);
        return result;
    }

    public String reLink(String targetHost, String targetPath, String userName, String path, String rsa) throws Exception {
        String command = "ln -snf " + targetPath + "/" + replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr() + " " + DevConfig.linkPath + "/" + alias + "-package/" + alias + "-current";
        return execute(targetHost, userName, command, rsa);
    }


    /**
     * scpToTmp
     */
    public String scpToTmp(String path, String targetHost, String userName,String rsa) throws Exception {
        //String command = "scp -r " + path + " " + targetHost + ":/home/" + userName + "/tmp";
        String rsaKey = devConfig.rsaKey;
        AES aes = new AES(rsaKey);
        char[] rsaReal = aes.decrypt(rsa).toCharArray();
        ConnectLinuxCommand.uploadFile(targetHost,userName,rsaReal,path,"/home/" + userName + "/tmp");
        return "";
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
        String command = "mv /home/" + userName + "/tmp" + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/" + replaceMaster(subPacknameWithOutPostfix(path)) + getDateTimeStr();
        return execute(targetHost, userName, command, rsa);
    }


    /**
     * local back up
     */
    public String bakLocal(String path) throws Exception {
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
        //TODO
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
}
