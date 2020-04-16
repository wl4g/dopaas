package com.wl4g.devops.ci.core;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author vjay
 * @date 2020-04-15 16:13:00
 */
public class JgitTest {

    final private  static String projectDir = "/Users/vjay/.ci-workspace/sources/safecloud-devops-datachecker";
    final private  static String remoteUrl = "http://git.anjiancloud.repo/heweijie/safecloud-devops-datachecker.git";
    final private  static String gitPath = projectDir + "/.git";
    final private static UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider("heweijie", "hwj13535248668");

    @Test
    public void cloneGit() throws GitAPIException {
        File path = new File(projectDir);
        if (!path.exists()) {
            path.mkdirs();
        }
        CloneCommand cmd = Git.cloneRepository().setURI(remoteUrl).setDirectory(path);
        cmd.setCredentialsProvider(usernamePasswordCredentialsProvider);
        cmd.setBranch("branch1");
        cmd.call();
    }

    @Test
    public void checkout() throws IOException, GitAPIException {
        Git git = Git.open(new File(gitPath));
        FetchCommand fetch = git.fetch();
        fetch.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetch.call();
        Ref dev = git.checkout().setName("dev").setCreateBranch(true).call();
        System.out.println("success");
    }

    @Test
    public void branchs() throws IOException, GitAPIException {
            Git git = Git.open(new File(gitPath));
            ListBranchCommand listBranchCommand = git.branchList();
            List<Ref> call = listBranchCommand.call();
            for(Ref ref : call){
                System.out.println(ref.getName());
            }
    }

    @Test
    public void tags() throws IOException, GitAPIException {
        Git git = Git.open(new File(gitPath));
        /*ListTagCommand listTagCommand = git.tagList();
        List<Ref> call = listTagCommand.call();
        for(Ref ref : call){
            System.out.println(ref.getName());
        }*/

        //
        FetchCommand fetchCommand = git.fetch().setTagOpt(TagOpt.FETCH_TAGS);

        fetchCommand.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetchCommand.call();
        List<Ref> tags = git.tagList().call();
        if(tags != null && tags.size() > 0) {
            for(Ref tag : tags) {
                System.out.println(tag.getName());
            }
        }
    }





}
