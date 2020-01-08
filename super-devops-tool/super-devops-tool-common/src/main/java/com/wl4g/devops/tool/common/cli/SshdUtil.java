package com.wl4g.devops.tool.common.cli;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.subsystem.sftp.SftpClient;
import org.apache.sshd.client.subsystem.sftp.impl.DefaultSftpClientFactory;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author vjay
 * @date 2020-01-08 09:59:00
 */
public class SshdUtil {

	private static SshClient client;

	public static void main(String[] args) {
		client = SshClient.setUpDefaultClient();
		client.start();

		// TODO do something
		//executeWithPrivateKey();
		executeWithPassword();
		//upload();

		client.stop();
	}

	private static ClientSession authWithPassword() throws IOException, GeneralSecurityException {
		ClientSession session = client.connect("root", "vjay.pw", 30022).verify(10000).getSession();
		session.addPasswordIdentity("hwj13535248668"); // for password-based authentication
		AuthFuture verify = session.auth().verify(10000);
		if (!verify.isSuccess()) {
			throw new GeneralSecurityException("auth fail");
		}
		return session;
	}

	private static ClientSession authWithPrivateKey() throws IOException, GeneralSecurityException {
		ClientSession session = client.connect("root", "10.0.0.160", 22).verify(10000).getSession();
		Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(session, null, getStrToStream(privateKey), null);
		Iterator<KeyPair> iterator = keyPairs.iterator();
		if (iterator.hasNext()) {
			KeyPair next = iterator.next();
			session.addPublicKeyIdentity(next);// for password-less authentication
		}
		AuthFuture verify = session.auth().verify(10000);
		if (!verify.isSuccess()) {
			throw new GeneralSecurityException("auth fail");
		}
		return session;
	}

	public static void executeWithPrivateKey() {
		ClientSession session = null;
		try {
			session = authWithPrivateKey();

			String s = session.executeRemoteCommand("cd /etc\nls");
			System.out.println(s);


		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void executeWithPassword() {
		ClientSession session = null;
		try {
			session = authWithPassword();

			//String s1 = session.executeRemoteCommand("cd /root/git/vjay\nmvn clean");
            //ChannelExec execChannel = session.createExecChannel("cd /root/git/vjay\nmvn clean");

            ChannelExec ec=session.createExecChannel("cd /root/git/vjay\nmvn install");
            OutputStream out = new FileOutputStream("/Users/vjay/Downloads/test2.txt");

            ec.setErr(out);
            ec.setOut(out);
            out.flush();
            ec.open();
            ec.waitFor(Collections.singleton(ClientChannelEvent.CLOSED), 0);
            out.close();
            ec.close();


        } catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void upload() {
		ClientSession session = null;
		SftpClient sftp = null;
		OutputStream os = null;
		try {
			session = authWithPassword();
			sftp = DefaultSftpClientFactory.INSTANCE.createSftpClient(session);
			os = sftp.write("/root/devops-0107.sql");
			Path src = Paths.get("/Users/vjay/Downloads/devops-0107.sql");
			Files.copy(src, os);

		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (sftp != null) {
				try {
					sftp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static InputStream getStrToStream(String sInputString) {
		if (sInputString != null && !sInputString.trim().equals("")) {
			try {
				ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
				return tInputStringStream;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private static final String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n"
			+ "MIIEpQIBAAKCAQEAwawifYZlHNdmkdMmXdi6wslkfvvAVjGo4cBPtrOFonD0Paex\n"
			+ "tVRckfkj6rCu4IkKOq6HFBBf1peYVHojLFUm4FGC+YatxoLcdExBj8A/oMVsWN8a\n"
			+ "ZWv5RH0lqUPZyuefqIrD+pos0R1hJtEDh5cKKT+Ae7kOP2+pX0QeGu0F/z9jozPo\n"
			+ "PiM5DaoM0xaDqhmn1dnY03X3TAY8/V9Oy1zSRslXoiF2EmfTiaHBlvCeK5WhCiMd\n"
			+ "5Xfn26Eiw3RBePh/eGiXjgv2ILZA7pnoINCa+PXI6VW6mthHQ8GJ7w+2afCGZBun\n"
			+ "hxpGPEKih8YGNHBlGKUPale0pPMqI703iENZrQIDAQABAoIBAQCD2/qvk+0Lsev3\n"
			+ "pNceVgzxycROYIEXLkBZU2Hydk+pxVXFFIN9fa55BDNb+mdWIHeCdIkrM+rMY/Im\n"
			+ "sfF4oZEScOzHjtaJrVcDJ1gL00x+3WtjJqMGInlYFAysLbH+36xoR/IekRGqXmJi\n"
			+ "1zOcAU29v6pukhQNRK0AW5RTqMTIfuQeR7Utpq4QNk8BSfDKJcyVoiTpaJ3Pv4AG\n"
			+ "rPkIgtIqtmDJmp6MbMVtJA8AyxYrAiJbHntVeRddj2NkrScK/J4RG+5U/9Jj5cpB\n"
			+ "VA21C7pt4lvUVuAPQ+esFLiwxBlIfW+sS5jaKCbJaeBL78xOiaLBJNWfrGR4USdu\n"
			+ "k0P8o/sBAoGBAO6YeX2TDyVH/8XsSq9nPs+hu85wMqWohswG9R6oxcurVJLLKjdd\n"
			+ "vqwpFaOUaQHcZr7iN2jGWqxxjFRW5qnwevk8uyXs9joSKV2C7KUdxuP9dtuqJNyl\n"
			+ "+p1qN57+J69WXQNJoMKmiICK43fW89CoNucqJqrmsLzBkQiFmtR83ORxAoGBAM/M\n"
			+ "xBTaVPrJYXxx10TZo+NcN5dv4FfDmK/Uz6v2F4y8xoRxpde8yeo0xj9CM8J2SX8w\n"
			+ "712GLWI03m8RsYg/g2qRueK34lrA0eJ8GGUV2SJOqOQqlv1FJJVDp3658Cm0IyDT\n"
			+ "CvyHJyrGmJljUhTgnwMuuzcp1wAfenWFp6OaAfb9AoGAXZMxOr29V+rH9nD4zZgZ\n"
			+ "e0c8J/e69VuGGmi0I+UfRgSY88V4diRvDohCc1hWYqN1LHH+Nzpr/2u9FKrMZmPp\n"
			+ "ZuyZnYM1Aoty67jYZN2rzmju/7HYKS1zf99TlyiomcyuSAbNZOn5aSiPk8Wa8/+1\n"
			+ "IK5YYfh94lmsLwJvOd0KqRECgYEAxUa22L02dCh/Pm+tWRXt+0lvFXwG1gtBh5xX\n"
			+ "0/97+Aa3yMFEGv6GCq0zkJa/INy/hdrlRDrAFz3t9jAsBReXIbNbcBv27wWjvIrn\n"
			+ "dgA59dILkSHF2oir5HEoMK1BjbYQq3bwNTHyQy/ra6PZJyzgiVryLbqw/NLlpXDP\n"
			+ "6Aer2dkCgYEAwKka1EYm5/N4krwsVvNBWD4Xgt4dtkGkQYkhyXZIqGTLFntIdVig\n"
			+ "jKoQ6kaFTPaSST4kWNoXxNWvBDjarOriPa//St+l5fsEjfhjF1CfCS5aKvKfIwmP\n"
			+ "jZss7kAharoCjXmxdyqPBEjJPHMts7d93olfGDGCvFrZnEfuD+zUcmU=\n" + "-----END RSA PRIVATE KEY-----";

}
