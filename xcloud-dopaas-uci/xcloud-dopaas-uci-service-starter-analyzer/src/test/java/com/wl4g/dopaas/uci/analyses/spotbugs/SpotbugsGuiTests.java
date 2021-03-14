/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.analyses.spotbugs;

import java.io.File;
import java.io.InputStream;

import com.wl4g.dopaas.uci.analyses.agent.spotbugs.progress.PrintAnalyzingProgress;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.gui2.BugLoader;

public class SpotbugsGuiTests {

	public static void main(String[] args) throws Exception {
		// subCmdProcessTest();
		// edu.umd.cs.findbugs.LaunchAppropriateUI.main(args);
		// edu.umd.cs.findbugs.FindBugs2.main(args);
		customAnalysisTest(args);
	}

	public static void customAnalysisTest(String[] args) throws Exception {
		Project project = new Project();
		// java.util.List<String> srcDirs = new java.util.ArrayList<>();
		// srcDirs.add("C:\\Program
		// Files\\java-1.8.0-openjdk-1.8.0.191-1.b12.redhat.windows.x86_64\\jre\\lib");
		// srcDirs.add("C:\\Users\\Administrator\\Desktop\\iam-server-master-bin\\libs");
		// project.addSourceDirs(srcDirs);

		File targetDir = new File("C:\\Users\\Administrator\\Desktop\\iam-server-master-bin\\libs");
		for (File f : targetDir.listFiles()) {
			project.addFile(f.getAbsolutePath());
		}

		BugCollection bugs = BugLoader.doAnalysis(project, new PrintAnalyzingProgress(System.out));
		bugs.writeXML("C:\\Users\\Administrator\\Desktop\\test-bugs.xml");
	}

	public static void subCmdProcessTest() throws Exception {
		System.out.println("----pre exec---");
		String[] cmds = new String[] { "java", "-jar", "C:\\Users\\Administrator\\Desktop\\test1.jar" };
		Process ps = Runtime.getRuntime().exec(cmds);
		System.out.println("----post exec---");
		// ps.waitFor();
		// System.out.println("----post wait---");

		// Stopping.
		// new Thread(() -> {
		// try {
		// Thread.sleep(2000L);
		// ps.destroy();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }).start();

		InputStream in = ps.getInputStream();
		int len = 0;
		byte[] buf = new byte[1024];
		while (ps.isAlive() && ((len = in.read(buf)) != -1)) {
			System.out.println(new String(buf, 0, len));
		}
		System.out.println("----done---" + ps.exitValue());
		// System.out.println(com.wl4g.dopaas.common.utils.io.ByteStreams2.unsafeReadFullyToString(in));
	}

}