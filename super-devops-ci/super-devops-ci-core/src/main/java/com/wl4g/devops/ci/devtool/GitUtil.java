package com.wl4g.devops.ci.devtool;

import com.wl4g.devops.shell.utils.ShellConsoleHolder;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-06 09:54:00
 */
public class GitUtil {
    public static final Logger log = LoggerFactory.getLogger(GitUtil.class);


    @Autowired
    private DevConfig baseConfig;


    /**
     * clone
     */
    public static void clone(String remoteUrl, String localPath) throws IOException {
        File path = new File(localPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            Git git = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(path)
                    .setCredentialsProvider(DevConfig.getCp())
                    .call();
            log.info("Cloning from " + remoteUrl + " to " + git.getRepository());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * clone
     */
    public static void clone(String remoteUrl, String localPath, String branchName) throws IOException {
        File path = new File(localPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            Git git = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(path)
                    .setCredentialsProvider(DevConfig.getCp())
                    .setBranch(branchName)
                    .call();
            log.info("Cloning from " + remoteUrl + " to " + git.getRepository());
            ShellConsoleHolder.writeQuietly("Cloning from " + remoteUrl + " to " + git.getRepository());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * checkout and pull
     */
    public static void checkout(String localPath, String branchName) {
        String projectURL = localPath + "/.git";
        Git git = null;
        try {
            git = Git.open(new File(projectURL));
            List<Ref> refs = git.branchList().call();
            boolean exist = false;//is branch exist
            for (Ref ref : refs) {
                String branchNameHad = ref.getName().substring(11);
                if (StringUtils.equals(branchName, branchNameHad)) {
                    exist = true;
                }
            }
            if (exist) {//if exist --checkout

                git.checkout().setName(branchName).call();
            } else {//if not exist --checkout and create local branch
                git.checkout()
                        .setCreateBranch(true)
                        .setName(branchName)
                        .setStartPoint("origin/" + branchName)
                        .setForceRefUpdate(true)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                        .call();
            }
            //pull -- get newest code
            git.pull().setCredentialsProvider(DevConfig.getCp()).call();
            log.info("checkout branch success;branchName=" + branchName + " localPath=" + localPath);
            ShellConsoleHolder.writeQuietly("checkout branch success;branchName=" + branchName + " localPath=" + localPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkout branch fail;branchName=" + branchName + " localPath=" + localPath);
            ShellConsoleHolder.writeQuietly("checkout branch fail;branchName=" + branchName + " localPath=" + localPath);
            throw new RuntimeException(e);
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    /**
     * del branch
     */
    public static void delbranch(String localPath, String branchName) {
        String projectURL = localPath + "/.git";
        Git git = null;
        try {
            git = Git.open(new File(projectURL));
            git.branchDelete().setForce(true).setBranchNames(branchName).call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    /**
     * get local branch list
     */
    public static void branchlist(String localPath) {
        String projectURL = localPath + "/.git";
        Git git = null;
        try {
            git = Git.open(new File(projectURL));
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                System.out.println(ref.getName().substring(11));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }


    private static String getBranchName(String ref) {
        //ref.substring(11);
        return null;
    }


    public static void main(String[] args) {
        //git远程url地址
        //String url = "http://code.anjiancloud.owner/devops-team/safecloud-devops.git";
        //String localPath = "/Users/vjay/gittest/safecloud-devops";

        //String url = "https://github.com/wl4g/super-devops.git";
        String url = "http://code.anjiancloud.owner:8443/biz-team/android-team/portal-for-android.git";
        String localPath = "/Users/vjay/gittest/super-devops";
        //
        String branchName = "master";
        try {
            //GitUtil.clone(url,localPath);
            GitUtil.checkout(localPath, branchName);
            //GitUtil.delbranch(localPath,branchName);
            GitUtil.branchlist(localPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
