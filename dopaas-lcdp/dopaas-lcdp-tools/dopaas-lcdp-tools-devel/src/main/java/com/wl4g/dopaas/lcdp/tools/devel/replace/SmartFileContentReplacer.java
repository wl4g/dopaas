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
package com.wl4g.dopaas.lcdp.tools.devel.replace;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.wl4g.infra.common.annotation.Reserved;

/**
 * {@link SmartFileContentReplacer}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月17日
 * @since
 */
@SuppressWarnings("unused")
@Deprecated
@Reserved
public class SmartFileContentReplacer implements Runnable {

	private final File file;
	private final MatchStrategy match;
	private final ReplaceStrategy replace;

	private List<String> srcLines = emptyList();
	private List<String> dstLines = emptyList();

	public SmartFileContentReplacer(File file, MatchStrategy match, ReplaceStrategy replace) {
		notNullOf(file, "file");
		notNullOf(match, "match");
		notNullOf(replace, "replace");
		this.file = file;
		this.match = match;
		this.replace = replace;
	}

	@Override
	public void run() {
		try {
			// Load file content lines.
			loadSourceFileLines();

			// Replace content lines.
			doReplacingLineAll();

			// Output new replaced lines to file.
			outReplacedFileLines();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Load source file content lines.
	 * 
	 * @throws IOException
	 */
	private void loadSourceFileLines() throws IOException {
		this.srcLines = Resources.readLines(file.toURI().toURL(), UTF_8);
	}

	/**
	 * Output replaced new file.
	 * 
	 * @throws IOException
	 */
	private void outReplacedFileLines() throws IOException {
		File outFile = new File(file.getCanonicalPath().concat(".replaced"));
		StringBuffer _lines = new StringBuffer(srcLines.size() * 200);
		dstLines.stream().forEach(l -> _lines.append(l));
		Files.write(dstLines.toString(), outFile, UTF_8);
	}

	/**
	 * Do replacing lines all.
	 */
	private void doReplacingLineAll() {
		int matchStartLen = match.getMatchStartToken().length();
		int matchEndLen = match.getMatchEndToken().length();

		for (int i = match.getRefLine(); i < srcLines.size(); i++) {
			if (i < 85) {
				continue; // for testing
			}

			String ref = srcLines.get(i - match.getRefLine());
			String last = srcLines.get(i - 1);
			String line = srcLines.get(i);

			int lastMatchStart = last.indexOf(match.getMatchStartToken()) + matchStartLen;
			int lastEnd = last.indexOf(match.getMatchEndToken());
			if (lastMatchStart > 0 && lastMatchStart < lastEnd) {
				String extract = last.substring(lastMatchStart, lastEnd);

				System.out.println(extract);
			}

		}

	}

	public static class MatchStrategy {
		final private int refLine;
		final private String matchStartToken;
		final private String matchEndToken;

		public MatchStrategy(int refLine, String matchStartToken, String matchEndToken) {
			super();
			this.refLine = refLine;
			this.matchStartToken = matchStartToken;
			this.matchEndToken = matchEndToken;
		}

		public int getRefLine() {
			return refLine;
		}

		public String getMatchStartToken() {
			return matchStartToken;
		}

		public String getMatchEndToken() {
			return matchEndToken;
		}

	}

	public static class ReplaceStrategy {
		final private String insertStartToken;
		final private String insertEndToken;
		final private boolean override;

		public ReplaceStrategy(String insertStartToken, String insertEndToken, boolean override) {
			super();
			this.insertStartToken = insertStartToken;
			this.insertEndToken = insertEndToken;
			this.override = override;
		}

		public String getInsertStartToken() {
			return insertStartToken;
		}

		public String getInsertEndToken() {
			return insertEndToken;
		}

		public boolean isOverride() {
			return override;
		}

	}

}