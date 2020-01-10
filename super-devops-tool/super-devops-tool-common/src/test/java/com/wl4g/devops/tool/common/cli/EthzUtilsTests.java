package com.wl4g.devops.tool.common.cli;

import com.wl4g.devops.tool.common.cli.ssh2.EthzUtils;
import com.wl4g.devops.tool.common.cli.ssh2.Ssh2Clients;
import com.wl4g.devops.tool.common.io.ByteStreams2;

public class EthzUtilsTests {

	public static void main(String[] args) throws Exception {
		String command = "sleep 10";
		Ssh2Clients.getInstance(EthzUtils.class).execWaitForCompleteWithSsh2("10.0.0.160", "root", null, command, s -> {
			System.err.println(ByteStreams2.readFullyToString(s.getStderr()));
			System.out.println(ByteStreams2.readFullyToString(s.getStdout()));
			s.close();
			System.err.println("signal:" + s.getExitSignal() + ", state:" + s.getState() + ", status:" + s.getExitStatus());
			return null;
		}, 30_000);

	}

}
