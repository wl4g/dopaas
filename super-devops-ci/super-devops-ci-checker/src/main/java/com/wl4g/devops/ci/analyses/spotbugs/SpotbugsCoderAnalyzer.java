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

import org.springframework.stereotype.Component;

import com.wl4g.devops.ci.analyses.CodeAnalyzer;
import com.wl4g.devops.ci.analyses.ProjectModel;
import com.wl4g.devops.common.task.GenericTaskRunner;
import com.wl4g.devops.common.task.RunnerProperties;

/**
 * Spotbugs code scanner. </br>
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
@Component
public class SpotbugsCoderAnalyzer extends GenericTaskRunner<RunnerProperties> implements CodeAnalyzer {

	@Override
	public void analysis(ProjectModel model) {

	}

}
