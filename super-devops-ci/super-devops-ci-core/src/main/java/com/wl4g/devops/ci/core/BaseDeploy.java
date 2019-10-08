package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.common.utils.codec.AES;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * @author vjay
 * @date 2019-09-24 09:21:00
 */
@Component
public class BaseDeploy {

    protected final Logger log = LoggerFactory.getLogger(BaseDeploy.class);


    @Autowired
    protected CiCdProperties ciCdProperties;


    /**
     * Exce command
     */
    public String exceCommand(String targetHost, String userName, String command, String rsa) throws Exception {
        if (StringUtils.isBlank(command)) {
            return "command is blank";
        }
        String rsaKey = ciCdProperties.getCipherKey();
        AES aes = new AES(rsaKey);
        char[] rsaReal = aes.decrypt(rsa).toCharArray();
        String result = command + "\n";
        result += SSHTool.execute(targetHost, userName, command, rsaReal);
        return result;
    }

    /**
     * Scp + tar + move to basePath
     */
    public String scpAndTar(String path, String targetHost, String userName, String targetPath, String rsa) throws Exception {
        String result = mkdirs(targetHost, userName, "/home/" + userName + "/tmp", rsa) + "\n";
        // scp
        result += scpToTmp(path, targetHost, userName, rsa) + "\n";
        // tar
        result += tarToTmp(targetHost, userName, path, rsa) + "\n";
        // mkdir--real app path
        // result += mkdirs(targetHost, userName, targetPath, rsa);

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
        String rsaKey = ciCdProperties.getCipherKey();
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
    public String backupLocal(String path, String sign) throws Exception {
        checkPath(ciCdProperties.getBackupPath());
        String command = "cp -Rf " + path + " " + ciCdProperties.getBackupPath() + "/" + subPackname(path) + "#" + sign;
        return SSHTool.exec(command);
    }

    /**
     * Get local back up , for rollback
     */
    public String getBackupLocal(String backFile, String target) throws Exception {
        checkPath(ciCdProperties.getBackupPath());
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
    public String dockerBuild(String path) throws Exception {
        String command = "mvn -f " + path + "/pom.xml -Pdocker:push dockerfile:build  dockerfile:push -Ddockerfile.username="
                + ciCdProperties.getDockerPushUsername() + " -Ddockerfile.password=" + ciCdProperties.getDockerPushPasswd();
        return SSHTool.exec(command);
    }

    /**
     * Docker pull
     */
    public String dockerPull(String targetHost, String userName, String imageName, String rsa) throws Exception {
        String command = "docker pull " + imageName;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Docker stop
     */
    public String dockerStop(String targetHost, String userName, String groupName, String rsa) throws Exception {
        String command = "docker stop " + groupName;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Docker remove container
     */
    public String dockerRemoveContainer(String targetHost, String userName, String groupName, String rsa) throws Exception {
        String command = "docker rm " + groupName;
        return exceCommand(targetHost, userName, command, rsa);
    }

    /**
     * Docker Run
     */
    public String dockerRun(String targetHost, String userName, String runCommand, String rsa) throws Exception {
        return exceCommand(targetHost, userName, runCommand, rsa);
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



}
