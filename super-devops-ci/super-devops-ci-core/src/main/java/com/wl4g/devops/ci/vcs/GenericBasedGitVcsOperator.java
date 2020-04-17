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
package com.wl4g.devops.ci.vcs;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.bean.ci.Vcs.VcsAuthType;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.wl4g.devops.tool.common.codec.Encodes.toBytes;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Generic version control service operator program based on GIT protocol
 * family.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-08
 * @since
 */
public abstract class GenericBasedGitVcsOperator extends AbstractVcsOperator {

    // --- Based Git commands. ---

    @SuppressWarnings("unchecked")
    @Override
    public Git clone(Vcs credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
        super.clone(credentials, remoteUrl, projecDir, branchName);

        File path = new File(projecDir);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            // Authenticate credentials.
            CloneCommand cmd = setupCredentials(credentials, Git.cloneRepository().setURI(remoteUrl).setDirectory(path));
            if (!isBlank(branchName)) {
                cmd.setBranch(branchName);
            }
            Git git = cmd.call();
            if (log.isInfoEnabled()) {
                log.info("Cloning from '" + remoteUrl + "' to " + git.getRepository());
            }
            return git;
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Faild to clone from '%s'", remoteUrl), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PullResult checkoutAndPull(Vcs credentials, String projecDir, String branchName, VcsAction action) {
        super.checkoutAndPull(credentials, projecDir, branchName, action);
        String projectURL = projecDir + "/.git";
        try (Git git = Git.open(new File(projectURL))) {
            //fetch
            setupCredentials(credentials, git.fetch().setTagOpt(TagOpt.FETCH_TAGS)).call();
            //tag list
            boolean hasTag = false;
            if (VcsAction.TAG.equals(action)) {
                List<Ref> tags = git.tagList().call();
                for (Ref ref : tags) {
                    if (StringUtils.equals(branchName, getBranchName(ref))) {
                        hasTag = true;
                        break;
                    }
                }
            }
            // branch list
            List<Ref> refs = git.branchList().call();
            boolean hasBranch = false;// is branch exist
            for (Ref ref : refs) {
                String branchNameHad = getBranchName(ref);
                if (StringUtils.equals(branchName, branchNameHad)) {
                    hasBranch = true;
                    break;
                }
            }
            if (hasTag && hasBranch) {
                throw new RuntimeException("has same name with tag and branch");
            } else if (hasTag) {
                git.checkout().setName(branchName).call();
                return null; //needn't pull
            } else if (hasBranch) {
                git.checkout().setName(branchName).setForceRefUpdate(true).call();
            } else { // Not exist to checkout & create local branch
                git.checkout().setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName)
                        .setForceRefUpdate(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
            }
            // Pull latest source.
            PullResult pullRes = setupCredentials(credentials, git.pull()).call();
            if (log.isInfoEnabled()) {
                log.info("Checkout & pull successful for branchName:{}, projecDir:{}", branchName, projecDir);
            }
            return pullRes;
        } catch (Exception e) {
            String errmsg = String.format("Failed to checkout & pull for branchName: %s, projecDir: %s", branchName, projecDir);
            log.error(errmsg, e);
            throw new IllegalStateException(errmsg, e);
        }
    }

    @Override
    public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
        super.delLocalBranch(projecDir, branchName, force);

        String gitPath = projecDir + "/.git";
        try (Git git = Git.open(new File(gitPath))) {
            return git.branchDelete().setForce(force).setBranchNames(branchName).call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean hasLocalRepository(String projecDir) {
        super.hasLocalRepository(projecDir);
        File file = new File(projecDir + "/.git");
        return file.exists();
    }

    @Override
    public String getLatestCommitted(String projecDir) throws Exception {
        super.getLatestCommitted(projecDir);

        try (Git git = Git.open(new File(projecDir))) {
            Iterable<RevCommit> iterb = git.log().setMaxCount(1).call(); // Latest-commit
            Iterator<RevCommit> it = iterb.iterator();
            if (it.hasNext()) {
                // Get latest version committed.
                String commitSign = it.next().getName();
                if (log.isInfoEnabled()) {
                    log.info("Latest committed sign:{}, path:{}", commitSign, projecDir);
                }
                return commitSign;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Ref rollback(Vcs credentials, String projecDir, String sign) {
        super.rollback(credentials, projecDir, sign);

        String metaDir = projecDir + "/.git";
        try (Git git = Git.open(new File(metaDir))) {
            setupCredentials(credentials, git.fetch()).call();
            Ref ref = git.checkout().setName(sign).call();

            String msg = "Rollback branch completed, sign:" + sign + ", localPath:" + projecDir;
            if (log.isInfoEnabled()) {
                log.info(msg);
            }
            return ref;
        } catch (Exception e) {
            String errmsg = String.format("Failed to rollback, sign:%s, localPath:%s", sign, projecDir);
            log.error(errmsg, e);
            throw new IllegalStateException(e);
        }

    }

    /**
     * Get (local) branch name.
     *
     * @param ref
     * @return
     */
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

    // --- Authentication credentials. ---

    /**
     * Setup GIT commands authenticate credentials.
     *
     * @param credentials
     * @param command
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T extends TransportCommand> T setupCredentials(Vcs credentials, T command) {
        try {
            switch (VcsAuthType.of(credentials.getAuthType())) {
                case AUTH_PASSWD:
                    return (T) command.setCredentialsProvider(
                            new UsernamePasswordCredentialsProvider(credentials.getUsername(), credentials.getPassword()));
                case AUTH_SSH:
                    return (T) command.setTransportConfigCallback(newTransportConfigCallback(toBytes(credentials.getSshKey())));
                default:
                    throw new Error("It shouldn't be do here");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * New transport callback for ssh-key authenticate credentials.
     *
     * @param identity
     * @return
     * @throws Exception
     * @see {@link TransportConfigCallback}
     */
    private TransportConfigCallback newTransportConfigCallback(byte[] identity) throws Exception {
        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
                    @Override
                    protected void configure(OpenSshConfig.Host hc, Session session) {
                        session.setConfig("StrictHostKeyChecking", "no");
                        // session.setPort(2022);
                    }

                    @Override
                    protected JSch createDefaultJSch(FS fs) throws JSchException {
                        JSch jsch = super.createDefaultJSch(fs);
                        jsch.removeAllIdentity();
                        // jsch.addIdentity("/Users/vjay/.ssh/id_rsa");
                        jsch.getIdentityRepository().add(identity);
                        return jsch;
                    }
                });
            }
        };
    }

    @Override
    protected HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", credentials.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return entity;
    }

}