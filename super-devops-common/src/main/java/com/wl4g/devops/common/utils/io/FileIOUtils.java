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
package com.wl4g.devops.common.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

/**
 * Enhanced files IO operation implements.</br>
 * <p>
 * <b><a href="https://coderanch.com/t/276838/java/seek-skipBytes">
 * InputStream.skip() VS RandomAccessFile.seek()</a></b></br>
 * </br>
 * <b>Result:</b> It is unfair to compare skipping with Data Input Stream and
 * Random Access File. Random Access File knows its underlying stream... knows
 * it's communicating with the file system, so it can take advantage of the
 * shortcut to "find" the underlying file system API. Skp () of FileInputStream
 * has the same advantages. However, neither DataInputStream nor any other
 * generic input stream knows that their source is a file, so they cannot use
 * any low-level shortcuts. The only way they implement skip () is to read
 * bytes, which is certainly slower than telling the filesystem to "seek" for
 * new file locations.
 * </p>
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public abstract class FileIOUtils extends FileUtils {

	/**
	 * Default buffer size.
	 */
	final public static int DEFAULT_BUF_SIZE = 4096;

	// Writer

	/**
	 * Write string to file.
	 * 
	 * @param file
	 * @param data
	 */
	public static void writeFile(File file, String data) {
		writeFile(file, data, Charset.forName("UTF-8"), true);
	}

	/**
	 * Write string to file.
	 * 
	 * @param file
	 * @param data
	 * @param append
	 */
	public static void writeFile(File file, String data, Charset charset, boolean append) {
		if (Objects.isNull(data) || Objects.isNull(file)) {
			return;
		}

		try (Writer w = new FileWriterWithEncoding(file, charset, append)) {
			File parent = file.getParentFile();
			if (!parent.exists() || !parent.isDirectory()) {
				parent.mkdirs();
			}
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
			w.write(data);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	// Reader

	/**
	 * Reading lines for page.
	 * 
	 * @param filename
	 * @param startLine
	 * @param limitLine
	 * @return
	 */
	public static List<String> readLines(String filename, int startLine, int limitLine) {
		List<String> lines = new ArrayList<>();
		try (FileInputStream in = new FileInputStream(filename); Scanner sc = new Scanner(in);) {
			int index = 0;
			int count = 0;
			while (count < limitLine && sc.hasNextLine()) {
				if (index >= startLine) {
					count++;
					String line = sc.nextLine();
					lines.add(line);
				} else {
					sc.nextLine();
				}
				index++;
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return lines;
	}

	/**
	 * Seek reading file to batch string lines.
	 * 
	 * @param filename
	 *            the system-dependent filename
	 * @param startPos
	 *            seek page start position.
	 * @param length
	 *            seek page size, Note: that it will contain line breaks.
	 * @return
	 */
	public static List<String> readSeekLines(String filename, long startPos, long length) {
		// Reading file to buffer strings.
		List<String> bufStrs = readSeekBufLines(filename, startPos, length);

		// Paging reorganization according to system newline characters.
		List<String> newLines = new ArrayList<>();
		for (String buf : bufStrs) {
			StringBuffer line = new StringBuffer();
			for (int i = 0, len = buf.length(); i < len; i++) {
				char ch = buf.charAt(i);
				boolean isNewline = (ch == '\n');
				if (isNewline || (i >= (len - 1))) {
					if (!isNewline) {
						line.append(ch);
					}
					newLines.add(line.toString());
					line.setLength(0);
				} else {
					line.append(ch);
				}
			}
		}
		return newLines;
	}

	/**
	 * Seek reading file to batch string buffer. Note: Each element of the
	 * returned list string does not correspond to a line of the physical file
	 * content. The result you want to read corresponds to a line of the
	 * physical file, see:
	 * {@link FileIOUtils#readSeekLines(String filename, long startPos, int length)}
	 * 
	 * @param filename
	 *            the system-dependent filename
	 * @param startPos
	 *            seek page start position.
	 * @param length
	 *            seek page size, Note: that it will contain line breaks.
	 * @return
	 */
	public static List<String> readSeekBufLines(String filename, long startPos, long length) {
		List<String> content = new ArrayList<>();
		readSeekFile(filename, startPos, length, DEFAULT_BUF_SIZE, (data, len) -> content.add(new String(data, 0, len)));
		return content;
	}

	/**
	 * Seek reading skip mode reading in file.
	 * 
	 * @param filename
	 *            the system-dependent filename
	 * @param startPos
	 *            seek page start position.
	 * @param length
	 *            seek page size, Note: that it will contain line breaks.
	 * @param bufSize
	 *            Buffer size per batch read
	 * @param processor
	 *            Each read processing program
	 */
	public static void readSeekFile(String filename, long startPos, long length, int bufSize, SeekProcessor processor) {
		try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
			raf.seek(startPos);
			byte[] data = new byte[bufSize];
			int len = 0, totalLen = 0;
			while ((len = raf.read(data)) != -1) {
				totalLen += len;
				if (totalLen >= length) { // Read enough data?
					/**
					 * e.g. File:3000bytes</br>
					 * bufSize=1024, length=1000 => 0,1024,...
					 */
					int needLen = (int) length;
					if (length > bufSize) {
						/**
						 * e.g. File:3000bytes</br>
						 * bufSize=1024, length=2000 => 0,1024,2048,...
						 */
						needLen = bufSize - (totalLen - ((int) length));
					}
					byte[] needData = new byte[needLen];
					System.arraycopy(data, 0, needData, 0, needLen);
					processor.process(needData, needLen);
					break;
				} else {
					processor.process(data, len);
				}
			}
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Seek reading processor.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-10-13
	 * @since
	 */
	public static interface SeekProcessor {
		void process(byte[] data, int len);
	}

	public static void main(String[] args) {
		System.out.println(readSeekLines("C:\\Users\\Administrator\\Desktop\\aaa.txt", 3L, 12L));
	}

}
