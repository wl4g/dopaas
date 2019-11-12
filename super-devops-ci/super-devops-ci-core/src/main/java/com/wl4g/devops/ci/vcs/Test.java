package com.wl4g.devops.ci.vcs;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author vjay
 * @date 2019-11-11 14:36:00
 */
public class Test {

	public static void main(String[] args) throws Exception {

		/*
		 * KeyPairGenerator keyPairGenerator =
		 * KeyPairGenerator.getInstance("RSA");
		 * keyPairGenerator.initialize(2048, new SecureRandom()); PrivateKey
		 * privateKey = keyPairGenerator.genKeyPair().getPrivate();
		 * 
		 * StringWriter writer = new StringWriter();
		 * 
		 * try (PEMWriter pemWriter = new PEMWriter(writer)) {
		 * pemWriter.writeObject(privateKey); }
		 * 
		 * String privateKeyStr = writer.toString();
		 * System.out.println(privateKeyStr); KeyPair load = KeyPair.load(null,
		 * privateKeyStr.getBytes("US-ASCII"), null);
		 */

		SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {

			@Override
			protected void configure(OpenSshConfig.Host hc, Session session) {
				session.setConfig("StrictHostKeyChecking", "no");
			}

			@Override
			protected JSch createDefaultJSch(FS fs) throws JSchException {
				JSch defaultJSch = super.createDefaultJSch(fs);
				defaultJSch.removeAllIdentity();
				defaultJSch.addIdentity("/Users/vjay/.ssh/id_rsa");
				defaultJSch.setKnownHosts("/Users/vjay/.ssh/known_hosts");
				/*
				 * try { defaultJSch.getIdentityRepository().add(getContent(
				 * "/Users/vjay/.ssh/gitlab-rsa")); } catch (IOException e) {
				 * e.printStackTrace(); }
				 */
				return defaultJSch;
			}

		};
		CloneCommand cloneCommand = Git.cloneRepository();
		cloneCommand.setURI("git@10.0.0.189:root/anzhuotest.git");
		cloneCommand.setDirectory(new File("/Users/vjay/citest"));
		cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(sshSessionFactory);
			}
		});
		cloneCommand.call();
	}

	public static byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			return null;
		}
		FileInputStream fi = new FileInputStream(file);
		byte[] buffer = new byte[(int) fileSize];
		int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset != buffer.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		fi.close();
		return buffer;
	}

}
