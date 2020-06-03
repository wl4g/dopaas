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
package com.wl4g.devops.tool.devel.stats;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notEmptyOf;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;

import com.google.common.io.Resources;
import com.wl4g.devops.tool.common.cli.CommandUtils.Builder;
import com.wl4g.devops.tool.common.function.ProcessFunction;
import com.wl4g.devops.tool.common.resource.resolver.ClassPathResourcePatternResolver;

/**
 * Source code counter tools.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月3日
 * @since
 */
public class SourceCodeCounterTool {

	final public static String DEFAULT_INCLUDES_FILE_EXT = ".java,.js,.sh,.py";

	public static void main(String[] args) throws Exception {
		showBanner();

		// Builder lines
		Builder builder = new Builder();
		builder.option("V", "verbose", "false", "Show print running verbose details.");
		builder.option("r", "rootDir", null, "Start scan root directory path.");
		builder.option("i", "fileExtIncludes", DEFAULT_INCLUDES_FILE_EXT, "Includes file ext.");
		CommandLine line = builder.build(args);

		boolean verbose = Boolean.parseBoolean(line.getOptionValue("verbose", "false"));
		String rootDir = line.getOptionValue("rootDir");
		String includes = line.getOptionValue("fileExtIncludes", DEFAULT_INCLUDES_FILE_EXT);
		SourceCodeCounter counter = new SourceCodeCounter(verbose, asList(split(includes, ",")));
		out.println("  Starting scanning analysis ......");
		counter.process(rootDir);
		out.println("  Scanned Analysis Result:");
		out.println(format("     Root directory: %s", rootDir));
		out.println(format("     Scan source file include exts: %s", includes));
		out.println(format("     Scan source files total: %s", new DecimalFormat(",###.##").format(counter.getFileTotalCount())));
		out.println(format("     Source code rows total: %s", new DecimalFormat(",###.##").format(counter.getRowsTotalCount())));
	}

	/**
	 * Show banner
	 * 
	 * @throws IOException
	 */
	private static void showBanner() throws Exception {
		out.println(Resources.toString(new ClassPathResourcePatternResolver().getResource("classpath:codes/banner.txt").getURL(),
				UTF_8));
	}

	/**
	 * {@link SourceCodeCounter}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年6月3日
	 * @since
	 */
	public static class SourceCodeCounter implements ProcessFunction<String, Integer> {

		final private boolean verbose;
		final private List<String> fileExtIncludes;
		final private AtomicInteger fileTotalCount = new AtomicInteger(0);
		final private AtomicInteger rowsTotalCount = new AtomicInteger(0);
		final private List<String> sourceFiles = new ArrayList<>(128);

		public SourceCodeCounter(boolean verbose, List<String> fileExtIncludes) {
			notEmptyOf(fileExtIncludes, "fileExtIncludes");
			this.verbose = verbose;
			this.fileExtIncludes = fileExtIncludes;
		}

		public int getFileTotalCount() {
			return fileTotalCount.get();
		}

		public int getRowsTotalCount() {
			return rowsTotalCount.get();
		}

		@Override
		public Integer process(String rootDir) throws Exception {
			hasTextOf(rootDir, "rootDir");
			addCodeFiles(rootDir);

			sourceFiles.forEach(file -> doStatisticsCodeNumbers(new File(file)));

			return getRowsTotalCount();
		}

		/**
		 * Do statistics codes numbers.
		 * 
		 * @param file
		 */
		private void doStatisticsCodeNumbers(File file) {
			try (FileInputStream fis = new FileInputStream(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));) {
				fileTotalCount.incrementAndGet();
				int count = 0;
				while (br.readLine() != null) {
					++count;
				}
				rowsTotalCount.addAndGet(count);
				if (verbose) {
					out.println(format("Source code scanned lines: %-6s - %snd file: %s", count, fileTotalCount, file));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Adds code files.
		 * 
		 * @param path
		 */
		private void addCodeFiles(String path) {
			File file = new File(path);
			File[] filesArr = file.listFiles();
			if (filesArr == null) {
				return;
			} else {
				for (File f : filesArr) {
					if (f.isDirectory()) {
						addCodeFiles(f.getPath());
					} else {
						String _path = f.getAbsolutePath();
						if (_path.contains(".")) {
							String fileExt = _path.substring(_path.lastIndexOf("."));
							if (fileExtIncludes.contains(fileExt)) {
								sourceFiles.add(f.getPath());
							}
						}
					}
				}
			}
		}

	}

}
