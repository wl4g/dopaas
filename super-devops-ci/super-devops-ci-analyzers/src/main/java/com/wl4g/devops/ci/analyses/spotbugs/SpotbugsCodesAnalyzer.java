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
package com.wl4g.devops.ci.analyses.spotbugs;

import static org.apache.commons.lang3.SystemUtils.JAVA_CLASS_PATH;

import java.io.File;

import com.wl4g.devops.ci.analyses.AbstractCodesAnalyzer;
import com.wl4g.devops.ci.analyses.model.AnalysisQueryModel;
import com.wl4g.devops.ci.analyses.model.AnalysisResultModel;
import com.wl4g.devops.ci.analyses.model.SpotbugsAnalysingModel;
import com.wl4g.devops.ci.analyses.spotbugs.engine.SpotbugsAnalyzerEngine;
import com.wl4g.devops.support.cli.DestroableProcessManager.ProcessCallback;

/**
 * SPOTBUGS code scanner analyzer. </br>
 * 
 * <pre>
 * &#64;see https://github.com/spotbugs/spotbugs/blob/b38806a67ce454e271ab8f759787e228dc8cf78c/spotbugs/src/gui/main/edu/umd/cs/findbugs/gui2/NewProjectWizard.java#L211
 * &#64;see {@link edu.umd.cs.findbugs.gui2.NewProjectWizard}
 * &#64;see {@link edu.umd.cs.findbugs.gui2.new ActionListener(){...}#actionPerformed(ActionEvent)}
 * &#64;see {@link edu.umd.cs.findbugs.gui2.AnalyzingDialog#show(Project)}
 * &#64;see {@link edu.umd.cs.findbugs.gui2.AnalyzingDialog#show(Project,AnalysisCallback,boolean)}
 * &#64;see {@link edu.umd.cs.findbugs.gui2.AnalyzingDialog.AnalysisThread}
 * &#64;see {@link edu.umd.cs.findbugs.gui2.AnalyzingDialog#startAnalysis(int)}
 * &#64;see {@link edu.umd.cs.findbugs.gui2.AnalyzingDialog#predictPassCount(int[])}
 * </pre>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public class SpotbugsCodesAnalyzer extends AbstractCodesAnalyzer<SpotbugsAnalysingModel> {

	@Override
	protected void doAnalyze(SpotbugsAnalysingModel model) throws Exception {
		String command = getSpotbugsAnalyzerRunCommand(model);
		try {
			processManager.exec(model.getProjectName(), command, new ProcessCallback() {
				@Override
				public void onStdout(byte[] data) {

				}

				@Override
				public void onStderr(byte[] err) {
					throw new IllegalStateException(new String(err));
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get SPOTBUGS analyzer run commands.</br>
	 * e.g.
	 * 
	 * <pre>
	 * java -Xmx1G -cp .:/opt/apps/acm/ci-analyzer-bin/lib edu.umd.cs.findbugs.FindBugs2
	 * </pre>
	 * 
	 * @param model
	 * @return
	 */
	private String getSpotbugsAnalyzerRunCommand(SpotbugsAnalysingModel model) {
		StringBuffer cmd = new StringBuffer("java ");
		cmd.append(config.getSpotbugs().getJvmArgs());
		cmd.append(" -cp .");
		cmd.append(File.pathSeparator);
		cmd.append(JAVA_CLASS_PATH);
		cmd.append(" ");
		// See:edu.umd.cs.findbugs.FindBugs2
		cmd.append(SpotbugsAnalyzerEngine.class.getName());
		return cmd.toString();
	}

	@Override
	public AnalysisResultModel getBugCollection(AnalysisQueryModel model) {
		// TODO Auto-generated method stub
		return null;
	}

}