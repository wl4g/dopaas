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
package com.wl4g.devops.components.tools.common.cli.ssh2;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.wl4g.devops.components.tools.common.cli.ssh2.SSH2Holders;
import com.wl4g.devops.components.tools.common.cli.ssh2.SshjHolder;
import com.wl4g.devops.components.tools.common.resource.ResourceUtils2;

import static com.wl4g.devops.components.tools.common.io.ByteStreamUtils.readFullyToString;
import static java.util.Objects.nonNull;

/**
 * {@link SshjHolderTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年5月23日 v1.0.0
 * @see
 */
public class SshjHolderTests {

	static String PRIVATE_KEY = ResourceUtils2.getResourceString(SshjHolderTests.class, "id_pub");
	static String HOME = "$HOME";

	public static void main(String[] args) throws Exception {
		// sshjExecTest();
		sshjScpTest(); // 68869ms cpu <30
		// sshjConnectTest();
	}

	public static void sshjExecTest() throws Exception {
		// Test execute command
		SSH2Holders.SshExecResponse sshExecResponse = SSH2Holders.getInstance(SshjHolder.class).execWaitForResponse("10.0.0.160", "root",
				PRIVATE_KEY.toCharArray(),null, "mvn -version", 60000);
		System.out.println("success=" + sshExecResponse.getMessage());
		System.out.println("fail=" + sshExecResponse.getErrmsg());
		System.out.println("exitCode=" + sshExecResponse.getExitCode());
	}

	public static void sshjScpTest() throws Exception {
		long t1 = System.currentTimeMillis();
		// Test upload file
		String loaclFile = "/Users/vjay/Downloads/elasticsearch-7.6.0-linux-x86_64.tar";
		SSH2Holders.getInstance(SshjHolder.class).scpPutFile("10.0.0.160", "root", PRIVATE_KEY.toCharArray(),null, new File(loaclFile),
				"$HOME/testssh/elasticsearch-7.6.0-linux-x86_64.tar");
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
	}

	public static void sshjConnectTest() throws IOException, InterruptedException {
		SSHClient ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.connect("10.0.0.160");
		KeyProvider keyProvider = ssh.loadKeys(new String(PRIVATE_KEY), null, null);
		ssh.authPublickey("root", keyProvider);
		Session session = ssh.startSession();
		// TODO
		// session.allocateDefaultPTY();
		Session.Shell shell = session.startShell();
		String command = "mvn -version\n";

		OutputStream outputStream = shell.getOutputStream();
		outputStream.write(command.getBytes());
		outputStream.flush();
		outputStream.close();

		InputStream inputStream = shell.getInputStream();
		InputStream errorStream = shell.getErrorStream();

		Thread.sleep(1000);
		shell.close();
		if (nonNull(inputStream)) {
			String message = readFullyToString(inputStream);
			System.out.println(message);
		}
		if (nonNull(errorStream)) {
			String errmsg = readFullyToString(errorStream);
			System.out.println(errmsg);
		}

		session.close();
		ssh.close();

	}

}