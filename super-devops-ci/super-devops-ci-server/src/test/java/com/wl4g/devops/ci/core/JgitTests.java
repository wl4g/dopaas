package com.wl4g.devops.ci.core;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * @author vjay
 * @date 2020-04-15 16:13:00
 */
public class JgitTests {

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
        cmd.setBranch("tag1");
        cmd.call();
    }

    @Test
    public void checkoutBranch() throws IOException, GitAPIException {
        String branchName = "branch1";

        Git git = Git.open(new File(gitPath));
        FetchCommand fetchCommand = git.fetch().setTagOpt(TagOpt.FETCH_TAGS);
        fetchCommand.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetchCommand.call();


        List<Ref> refs = git.branchList().call();
        boolean exist = false;// is branch exist
        for (Ref ref : refs) {
            String branchNameHad = getBranchName(ref);
            if (StringUtils.equals(branchName, branchNameHad)) {
                exist = true;
            }
        }
        if (exist) { // Exist to checkout
            git.checkout().setName(branchName).call();
        } else { // Not exist to checkout & create local branch
            git.checkout().setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName)
                    .setForceRefUpdate(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
        }

        PullCommand pullCommand2 = git.pull().setTagOpt(TagOpt.FETCH_TAGS);
        pullCommand2.setCredentialsProvider(usernamePasswordCredentialsProvider);
        pullCommand2.call();


        System.out.println("success");
    }

    @Test
    public void checkoutTag() throws IOException, GitAPIException {
        String branchName = "tag2";
        Git git = Git.open(new File(gitPath));
        FetchCommand fetchCommand = git.fetch().setTagOpt(TagOpt.FETCH_TAGS);
        fetchCommand.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetchCommand.call();

        /*FetchCommand fetch = git.fetch();
        fetch.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetch.call();*/
        git.checkout().setName(branchName).call();
        System.out.println("success");
    }

    @Test
    public void checkoutTest() throws IOException, GitAPIException {//use this,  meger checkout branch and tag
        String branchName = "branch1";

        Git git = Git.open(new File(gitPath));
        FetchCommand fetchCommand = git.fetch().setTagOpt(TagOpt.FETCH_TAGS);
        fetchCommand.setCredentialsProvider(usernamePasswordCredentialsProvider);
        fetchCommand.call();

        // check is tag
        List<Ref> tags = git.tagList().call();
        for(Ref ref : tags){
            if(StringUtils.equals(branchName,getBranchName(ref))){
                git.checkout().setName(branchName).call();
                return;
            }
        }
        //else
        List<Ref> refs = git.branchList().call();
        boolean exist = false;// is branch exist
        for (Ref ref : refs) {
            String branchNameHad = getBranchName(ref);
            if (StringUtils.equals(branchName, branchNameHad)) {
                exist = true;
                break;
            }
        }
        if (exist) { // Exist to checkout
            git.checkout().setName(branchName).setForceRefUpdate(true).call();
        } else { // Not exist to checkout & create local branch
            git.checkout().setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName)
                    .setForceRefUpdate(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
        }

        PullCommand pullCommand = git.pull().setTagOpt(TagOpt.FETCH_TAGS);
        pullCommand.setCredentialsProvider(usernamePasswordCredentialsProvider);
        pullCommand.call();

    }


    @Test
    public void branchListTest() throws IOException, GitAPIException {
            Git git = Git.open(new File(gitPath));
            ListBranchCommand listBranchCommand = git.branchList();
            List<Ref> call = listBranchCommand.call();
            for(Ref ref : call){
                System.out.println(ref.getName());
            }
    }

    @Test
    public void tagListTest() throws IOException, GitAPIException {
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

    protected static String getBranchName(Ref ref) {
        String name = ref.getName();
        if ("HEAD".equals(trimToEmpty(name))) {
            ObjectId objectId = ref.getObjectId();
            name = objectId.getName();
        } else {
            int index = name.lastIndexOf("/");
            name = name.substring(index + 1);
        }
        return name;
    }

    @Test
    public void lsRemote() throws IOException, GitAPIException {
        Git git = Git.open(new File(gitPath));
        LsRemoteCommand lsRemoteCommand = git.lsRemote();
        lsRemoteCommand.setCredentialsProvider(usernamePasswordCredentialsProvider);
        Collection<Ref> refs = lsRemoteCommand.call();
        for (Ref ref : refs) {
            System.out.println("Ref: " + ref);
        }

        // heads only
        refs = lsRemoteCommand.setHeads(true).call();
        for (Ref ref : refs) {
            System.out.println("Head: " + ref);
        }

        // tags only
        refs = lsRemoteCommand.setTags(true).call();
        for (Ref ref : refs) {
            System.out.println("Remote tag: " + ref);
        }
    }





}
