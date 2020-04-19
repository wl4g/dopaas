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
package com.wl4g.devops.components.webide.generate;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.wl4g.devops.components.webide.generate.parse.CodingType;
import com.wl4g.devops.components.webide.generate.parse.GenericClassInfo;

/**
 * {@link CodingCompletionGenerator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年4月19日 v1.0.0
 * @see
 */
public abstract class CodingCompletionGenerator {

	/**
	 * Coding type definition.
	 */
	final protected CodingType coding;

	/**
	 * Input depends libs files.
	 */
	final protected List<File> libs;

	/**
	 * Output depends libs parsed configuration.
	 */
	protected OutputStream output;

	/**
	 * Coding completion result.
	 */
	private GenericClassInfo result;

	public CodingCompletionGenerator(CodingType coding, File libs, File output) {
		this(coding, libs, openOutputStream(output));
	}

	public CodingCompletionGenerator(CodingType coding, File libs, OutputStream output) {
		notNullOf(coding, "coding");
		notNullOf(libs, "libs");
		notNullOf(output, "output");
		this.coding = coding;
		List<File> _libs = null;
		if (libs.isDirectory()) {
			_libs = asList(libs.listFiles()).stream().filter(f -> coding.matchs(f.getAbsolutePath())).collect(toList());
		} else if (libs.isFile()) {
			if (coding.matchs(libs.getAbsolutePath())) {
				_libs = asList(libs);
			}
		} else {
			throw new IllegalStateException(format("Loading depend libs file: '%s' not is directory or file.", libs));
		}
		this.libs = _libs;
		notNull(libs, IllegalStateException.class, "Not found availabled library files, %s", libs);
		this.output = output;
	}

	/**
	 * Gets coding libs depends completion result.
	 * 
	 * @return
	 */
	public final synchronized GenericClassInfo getCompletionResult() {
		if (isNull(result)) {
			try {
				loadLibParsingAll();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return result;
	}

	/**
	 * Loading libs paring.
	 * 
	 * @throws IOException
	 */
	private final void loadLibParsingAll() throws IOException {
		result = new GenericClassInfo();
		for (File lib : libs) {
			result.getClassInfo().add(coding.getParser().parse(lib));
		}
	}

	private static OutputStream openOutputStream(File file) {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

}
