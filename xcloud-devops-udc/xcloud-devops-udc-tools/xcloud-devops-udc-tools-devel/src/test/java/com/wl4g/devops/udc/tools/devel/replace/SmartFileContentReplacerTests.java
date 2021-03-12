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
package com.wl4g.devops.udc.tools.devel.replace;

import static java.lang.System.out; 
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.io.File;

import com.wl4g.component.common.annotation.Reserved;
import com.wl4g.devops.udc.tools.devel.replace.SmartFileContentReplacer;
import com.wl4g.devops.udc.tools.devel.replace.SmartFileContentReplacer.MatchStrategy;
import com.wl4g.devops.udc.tools.devel.replace.SmartFileContentReplacer.ReplaceStrategy;

/**
 * {@link SmartFileContentReplacerTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月17日
 * @since
 */
@Deprecated
@Reserved
public class SmartFileContentReplacerTests {

	static File file = new File(USER_DIR + "/CompositeJedisCommandsAdapter.java.txt");

	public static void main(String[] args) {
		out.println("Replacing ...");

		MatchStrategy src = new MatchStrategy(1, "	public ", " {");
		ReplaceStrategy dst = new ReplaceStrategy("if (nonNull(jedis)) jedis.", ");", true);
		SmartFileContentReplacer replacer = new SmartFileContentReplacer(file, src, dst);
		replacer.run();

		out.println("Finished replaced!");
	}

}