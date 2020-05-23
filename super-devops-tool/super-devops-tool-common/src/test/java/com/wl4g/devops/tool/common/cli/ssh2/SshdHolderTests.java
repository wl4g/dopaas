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
package com.wl4g.devops.tool.common.cli.ssh2;

import static com.google.common.base.Charsets.UTF_8;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.wl4g.devops.tool.common.cli.ssh2.Ssh2Holders.SshExecResponse;
import com.wl4g.devops.tool.common.resource.ResourceUtils2;

/**
 * {@link SshdHolderTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年5月23日 v1.0.0
 * @see
 */
public class SshdHolderTests {

	static String privateKey = ResourceUtils2.getResourceString(SshdHolderTests.class, "id_pub");

	public static void main(String[] args) throws Exception {
		sshdEthzInteractiveCmdTest1();
		// sshdPutTransferTest2();
	}

	public static void sshdEthzInteractiveCmdTest1() throws Exception {
		// Generate sample data.
		File file = new File("/tmp/test_vim_file.txt");
		FileUtils.write(file, "abcdefghijklmnopqrstuvwxyz", UTF_8);
		// Testing
		// String cmd = "vim /tmp/test_vim_file.txt";
		String cmd = "cat /tmp/test_vim_file.txt";
		SshExecResponse resp = Ssh2Holders.getInstance(EthzHolder.class).execWithSsh2("127.0.0.1", "wanglsir",
				privateKey.toCharArray(), cmd, 3_000);

		out.println("stdout=" + resp.getMessage());
		out.println("stderr=" + resp.getErrmsg());
		out.println("exitCode=" + resp.getExitCode());
	}

	public static void sshdPutTransferTest2() throws Exception {
		long begin = currentTimeMillis();
		// Test upload file
		String loaclFile = "/Users/vjay/Downloads/elasticsearch-7.6.0-linux-x86_64.tar";
		Ssh2Holders.getInstance(SshdHolder.class).scpPutFile("10.0.0.160", "root", privateKey.toCharArray(), new File(loaclFile),
				"$HOME/testssh/elasticsearch-7.6.0-linux-x86_64.tar");
		long end = currentTimeMillis();
		out.println("cost:" + (end - begin));// 80801ms 150<cpu<200
	}

}
