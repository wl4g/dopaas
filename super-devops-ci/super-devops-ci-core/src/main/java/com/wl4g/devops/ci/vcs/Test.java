package com.wl4g.devops.ci.vcs;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

import java.io.File;

/**
 * @author vjay
 * @date 2019-11-11 14:36:00
 */
public class Test {


    public static void main(String[] args) throws GitAPIException {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                //session.setConfig("StrictHostKeyChecking","no");
            }

            @Override
            protected JSch createDefaultJSch( FS fs ) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch( fs );
                defaultJSch.removeAllIdentity();
                defaultJSch.addIdentity( "/Users/vjay/.ssh/ci_id_rsa" );
                defaultJSch.setKnownHosts("/Users/vjay/.ssh/known_hosts");
                //defaultJSch.getIdentityRepository().add(Files.readBytes(new File("/")));
                return defaultJSch;
            }


        };
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI( "heweijie@git.anjiancloud.repo:biz-team/portal-team/safecloud-web-portal.git" );
        cloneCommand.setDirectory(new File("/Users/vjay/citest"));
        cloneCommand.setTransportConfigCallback( new TransportConfigCallback() {
            @Override
            public void configure( Transport transport ) {
                SshTransport sshTransport = ( SshTransport )transport;
                sshTransport.setSshSessionFactory( sshSessionFactory );
            }
        } );
        cloneCommand.call();
    }


    public static class MySShSessionFactory extends JschConfigSessionFactory {

        private String sshKeyFilePath;

        @Override
        protected void configure(OpenSshConfig.Host hc, Session session) {
            //TODO Auto-generated method stub
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
        }

        @Override
        protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException {
            JSch jsch = new JSch();
            jsch.removeAllIdentity();
            jsch.addIdentity(sshKeyFilePath);
            jsch.setKnownHosts("C:\\known_hosts");
            return jsch;
        }

        public String getSshKeyFilePath() {
            return sshKeyFilePath;
        }

        public void setSshKeyFilePath(String sshKeyFilePath) {
            this.sshKeyFilePath = sshKeyFilePath;
        }
    }


}
