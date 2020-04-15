package com.wl4g.devops.ci.core;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author vjay
 * @date 2020-04-15 16:13:00
 */
public class JgitTest {

    final private  static String projectDir = "/Users/vjay/.ci-workspace/sources/safecloud-web-sso";
    final private  static String remoteUrl = "http://git.anjiancloud.repo/biz-team/sso-team/safecloud-web-sso.git";
    final private  static String gitPath = projectDir + "/.git";
    final private static UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider("heweijie", "hwj13535248668");

    public static void main(String[] args) throws IOException, GitAPIException {
        //cloneGit();
        checkout();
        //branchs();
        //tags();
    }

    private static void cloneGit() throws GitAPIException {
        File path = new File(projectDir);
        if (!path.exists()) {
            path.mkdirs();
        }
        CloneCommand cmd = Git.cloneRepository().setURI(remoteUrl).setDirectory(path);
        cmd.setCredentialsProvider(usernamePasswordCredentialsProvider);
        cmd.setBranch("v-0.0.1-test");
        cmd.call();
        System.out.println("success");
    }

    private static void checkout() throws IOException, GitAPIException {
        Git git = Git.open(new File(gitPath));
        FetchCommand fetch = git.fetch();
        fetch.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetch.call();
        Ref dev = git.checkout().setName("dev").setCreateBranch(true).call();
        System.out.println("success");
    }

    private static void branchs() throws IOException, GitAPIException {
            Git git = Git.open(new File(gitPath));
            ListBranchCommand listBranchCommand = git.branchList();
            List<Ref> call = listBranchCommand.call();
            for(Ref ref : call){
                System.out.println(ref.getName());
            }
    }

    private static void tags() throws IOException, GitAPIException {
        Git git = Git.open(new File(gitPath));
        ListTagCommand listTagCommand = git.tagList();
        List<Ref> call = listTagCommand.call();
        for(Ref ref : call){
            System.out.println(ref.getName());
        }
    }



}
