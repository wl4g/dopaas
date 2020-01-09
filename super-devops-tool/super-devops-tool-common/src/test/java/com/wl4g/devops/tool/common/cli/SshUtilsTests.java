package com.wl4g.devops.tool.common.cli;

import com.wl4g.devops.tool.common.io.ByteStreams2;

public class SshUtilsTests {

	public static void main(String[] args) throws Exception {
		String command = "sleep 10";
		SshUtils.execWaitForCompleteWithSsh2("10.0.0.160", "root", null, command, s -> {
			System.err.println(ByteStreams2.readFullyToString(s.getStderr()));
			System.out.println(ByteStreams2.readFullyToString(s.getStdout()));
			s.close();
			System.err.println("signal:" + s.getExitSignal() + ", state:" + s.getState() + ", status:" + s.getExitStatus());
			return null;
		}, 30_000);

	}

}
