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


import com.wl4g.devops.components.tools.common.cli.ssh2.SSH2Holders;
import com.wl4g.devops.components.tools.common.cli.ssh2.SSH2Holders.SshExecResponse;
import com.wl4g.devops.components.tools.common.cli.ssh2.SshdHolder;
import com.wl4g.devops.components.tools.common.resource.ResourceUtils2;
import org.apache.commons.io.FileUtils;
import org.apache.sshd.client.channel.ClientChannelEvent;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Charsets.UTF_8;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.util.Collections.singleton;
import static java.util.Objects.nonNull;

/**
 * {@link SshdHolderTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年5月23日 v1.0.0
 * @see
 */
public class SshdHolderTests {

	static String PRIVATE_KEY = ResourceUtils2.getResourceString(SshdHolderTests.class, "id_pub");

	static {
		// Generate sample data.
		File file = new File("/tmp/test_vim_file.txt");
		try {
			FileUtils.write(file, "abcdefghijklmnopqrstuvwxyz", UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		// execCatInteractiveCommandTest1();
		execVimCommandTest2();
		// sshdPutTransferTest2();
	}

	public static void execCatCommandTest1() throws Exception {
		String cmd = "cat /tmp/test_vim_file.txt";
		SshExecResponse resp = SSH2Holders.getInstance(SshdHolder.class).execWaitForResponse("127.0.0.1", "wanglsir",
				PRIVATE_KEY.toCharArray(),null, cmd, 3_000);
		out.println("stdout=" + resp.getMessage());
		out.println("stderr=" + resp.getErrmsg());
		out.println("exitCode=" + resp.getExitCode());

	}

	public static void execVimCommandTest2() throws Exception {
		String cmd = "vim /tmp/test_vim_file.txt";
		SSH2Holders.getInstance(SshdHolder.class).doExecCommand("127.0.0.1", "wanglsir", PRIVATE_KEY.toCharArray(),null, cmd,
				chSession -> {
					chSession.waitFor(singleton(ClientChannelEvent.CLOSED), 3_000);

					String msg = null, errmsg = null;
					if (nonNull(chSession.getOut())) {
						msg = chSession.getOut().toString();
						out.println("stdout=" + msg);
					}
					if (nonNull(chSession.getErr())) {
						errmsg = chSession.getErr().toString();
						out.println("stderr=" + errmsg);
					}
					return null;
				});

	}

	public static void putTransferTest2() throws Exception {
		long begin = currentTimeMillis();
		// Test upload file
		String loaclFile = "/Users/vjay/Downloads/elasticsearch-7.6.0-linux-x86_64.tar";
		SSH2Holders.getInstance(SshdHolder.class).scpPutFile("10.0.0.160", "root", PRIVATE_KEY.toCharArray(),null, new File(loaclFile),
				"$HOME/testssh/elasticsearch-7.6.0-linux-x86_64.tar");
		long end = currentTimeMillis();
		out.println("cost:" + (end - begin));// 80801ms 150<cpu<200
	}

}