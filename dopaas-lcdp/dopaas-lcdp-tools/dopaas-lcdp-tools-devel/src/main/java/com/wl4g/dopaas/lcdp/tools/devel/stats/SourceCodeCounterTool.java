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
package com.wl4g.dopaas.lcdp.tools.devel.stats;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.io.FileIOUtils.*;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.SystemUtils2.*;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.lang.Boolean.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;

import com.google.common.io.Resources;
import com.wl4g.infra.common.cli.CommandUtils.Builder;
import com.wl4g.infra.common.function.ProcessFunction;
import com.wl4g.infra.common.resource.resolver.ClassPathResourcePatternResolver;

/**
 * Source code counter tools.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月3日
 * @since
 */
public class SourceCodeCounterTool {

	final public static String DEFAULT_EXCLUDES_FILE_PATHS = replace(
			"/node_modules/,/target/,/bin/,/run/,/tmp/,/temp/,/log/,/logs/,/data/,/dist/,/assets/,.classpath,.project,.iml,.git",
			"/", File.separator);
	final public static String DEFAULT_INCLUDES_FILE_EXTS = ".java,.js,.sh,.py,.go,.css,.html,.htm,.c,.h,.cpp";

	public static void main(String[] args) throws Exception {
		showBanner();

		// Builder lines
		Builder builder = new Builder();
		builder.option("V", "verbose", TRUE.toString(), "Show print running verbose details.");
		builder.option("O", "output", EMPTY, "Output append write to file path.");
		builder.option("r", "rootDir", null, "Start scan root directory path.");
		builder.option("i", "fileIncludes", DEFAULT_INCLUDES_FILE_EXTS, "Includes file path parts. eg: .java,.sh");
		builder.option("e", "fileExcludes", DEFAULT_EXCLUDES_FILE_PATHS, "Excludes file path parts. eg: /target/,/node_modules/");
		CommandLine line = builder.build(args);

		boolean verbose = Boolean.parseBoolean(line.getOptionValue("verbose", TRUE.toString()));
		String output = line.getOptionValue("output");
		String rootDir = line.getOptionValue("rootDir");
		List<String> includes = asList(split(line.getOptionValue("fileIncludes", DEFAULT_INCLUDES_FILE_EXTS), ","));
		List<String> excludes = asList(split(line.getOptionValue("fileExcludes", DEFAULT_EXCLUDES_FILE_PATHS), ","));
		SourceCodeCounter counter = new SourceCodeCounter(verbose, output, includes, excludes);
		out.println("  Startup scanning analysis ......");
		counter.process(rootDir);
		out.println("  Scanned Analysis Result:");
		out.println(format("     Root directory: %s", rootDir));
		out.println(format("     Scanned file ext includes: %s", includes));
		out.println(format("     Scanned file path excludes: %s", excludes));
		out.println(format("     Scanned files total: %s", new DecimalFormat(",###.##").format(counter.getFileTotalCount())));
		out.println(format("     Source codes lines: %s", new DecimalFormat(",###.##").format(counter.getLineTotalCount())));
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
		final private File output; // output path
		final private List<String> fileIncludes;
		final private List<String> fileExcludes;

		final private AtomicInteger fileTotalCount = new AtomicInteger(0);
		final private AtomicInteger lineTotalCount = new AtomicInteger(0);
		final private List<String> sourceFiles = new ArrayList<>(128);

		public SourceCodeCounter(boolean verbose, String output, List<String> fileIncludes, List<String> fileExcludes) {
			notEmptyOf(fileIncludes, "fileIncludes");
			notEmptyOf(fileExcludes, "fileExcludes");
			this.verbose = verbose;
			if (!isBlank(output)) {
				this.output = new File(output);
			} else {
				this.output = null;
			}
			this.fileIncludes = fileIncludes;
			this.fileExcludes = fileExcludes;
		}

		public int getFileTotalCount() {
			return fileTotalCount.get();
		}

		public int getLineTotalCount() {
			return lineTotalCount.get();
		}

		@Override
		public Integer process(String rootDir) throws Exception {
			hasTextOf(rootDir, "rootDir");
			addCodeFiles(rootDir);

			sourceFiles.forEach(file -> doStatisticsCodeNumbers(new File(file)));

			return getLineTotalCount();
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
				String line = null;
				while (nonNull(line = br.readLine())) {
					++count;
					if (nonNull(output)) {
						writeBLineFile(output, line);
					}
				}
				if (nonNull(output)) {
					writeFile(output, LINE_SEPARATOR, true);
				}
				lineTotalCount.addAndGet(count);
				if (verbose) {
					out.println(format("Source code scanned lines: %-6s - %snd file: %s", count, fileTotalCount, file));
				}
			} catch (Exception e) {
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
						boolean matchInclude = false, matchExclude = false;
						if (_path.contains(".")) {
							String ext = _path.substring(_path.lastIndexOf("."));
							for (String include : fileIncludes) {
								if (ext.contains(include)) {
									matchInclude = true;
									break;
								}
							}
						}
						for (String exclude : fileExcludes) {
							if (_path.contains(exclude)) {
								matchExclude = true;
								break;
							}
						}
						if (matchInclude && !matchExclude) {
							sourceFiles.add(f.getPath());
						}
					}
				}
			}
		}

	}

}