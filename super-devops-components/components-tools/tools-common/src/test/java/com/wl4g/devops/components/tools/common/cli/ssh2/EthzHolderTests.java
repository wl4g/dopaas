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

import com.wl4g.devops.components.tools.common.cli.ssh2.EthzHolder;
import com.wl4g.devops.components.tools.common.cli.ssh2.SSH2Holders;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.resource.ResourceUtils2;

import java.io.File;

/**
 * {@link EthzHolderTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年5月23日 v1.0.0
 * @see
 */
public class EthzHolderTests {

	static String PRIVATE_KEY = ResourceUtils2.getResourceString(EthzHolderTests.class, "id_pub");

	public static void main(String[] args) throws Exception {
		// executeCommand();
		transation();
	}

	public static void executeCommand() throws Exception {
		String command = "sleep 10";
		SSH2Holders.getInstance(EthzHolder.class).execWaitForComplete("10.0.0.160", "root", null,null, command, s -> {
			System.err.println(ByteStreamUtils.readFullyToString(s.getStderr()));
			System.out.println(ByteStreamUtils.readFullyToString(s.getStdout()));
			s.close();
			System.err.println("signal:" + s.getExitSignal() + ", state:" + s.getState() + ", status:" + s.getExitStatus());
			return null;
		}, 30_000);
	}

	public static void transation() throws Exception {
		long t1 = System.currentTimeMillis();
		// Test upload file
		String loaclFile = "/Users/vjay/Downloads/safecloud-0203.sql";
		SSH2Holders.getInstance(EthzHolder.class).scpPutFile("10.0.0.160", "root", PRIVATE_KEY.toCharArray(),null, new File(loaclFile),
				"/root/testssh/");
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
	}

}