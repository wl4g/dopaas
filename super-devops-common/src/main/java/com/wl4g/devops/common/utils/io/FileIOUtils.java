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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.util.Assert;

import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

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
	 * Default read safety loop count.
	 */
	final public static long DEFAULT_SAFE_READ_COUNT = 100_0000L;

	/**
	 * Default read/write buffer size.
	 */
	final public static int DEFAULT_RW_BUF_SIZE = 4096;

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

	public static void writeFile(File file, String data, boolean append) {
		writeFile(file, data, Charset.forName("UTF-8"), append);
	}

	/**
	 * Write string to file.
	 * 
	 * @param file
	 * @param data
	 * @param append
	 */
	public static void writeFile(File file, String data, Charset charset, boolean append) {
		notNull(file, "Write file must not be null");
		hasText(data, "Write data must not be empty");
		notNull(charset, "Write charset must not be null");

		File parent = file.getParentFile();
		if (!parent.exists() || !parent.isDirectory()) {
			state(parent.mkdirs(), String.format("Failed to creating parent directory for [%s]", parent.getAbsolutePath()));
		}
		if (!file.exists()) {
			try {
				state(file.createNewFile(), String.format("Failed to creating file for [%s]", file.getAbsolutePath()));
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		try (Writer w = new FileWriterWithEncoding(file, charset, append)) {
			w.write(data);
			w.flush();
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
	 * @param limit
	 * @return
	 */
	@Deprecated
	public static List<String> readLines(String filename, int startLine, int limit) {
		List<String> lines = new ArrayList<>();
		try (FileInputStream in = new FileInputStream(filename); Scanner sc = new Scanner(in);) {
			int index = 0;
			int count = 0;
			while (count < limit && sc.hasNextLine()) {
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
	 * Seek reading file to batch string buffer. Note: Each element of the
	 * returned list string does not correspond to a line of the physical file
	 * content. The result you want to read corresponds to a line of the
	 * physical file
	 * 
	 * @param filename
	 *            the system-dependent filename
	 * @param startPos
	 *            seek page start position.
	 * @param aboutLimit
	 *            seek page size, Note: that it will contain line breaks.
	 * @param stopper
	 *            Seek reader stopper, When {@link Function#apply()} returns
	 *            true, the read ends.
	 * @return
	 */
	public static ReadResult seekReadLines(String filename, long startPos, int aboutLimit, Function<String, Boolean> stopper) {
		Assert.hasText(filename, "Read seek filename must not be empty.");
		Assert.isTrue(startPos >= 0, "Read start position must be greater than or equal to 0");
		Assert.isTrue(aboutLimit > 0, "Read about limit must be greater than to 0");

		List<String> lines = new ArrayList<>();
		try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
			raf.seek(startPos);
			long c = 0, lastPos = -1, endPos = (startPos + aboutLimit);
			while (raf.getFilePointer() > lastPos && (lastPos = raf.getFilePointer()) < endPos && ++c < DEFAULT_SAFE_READ_COUNT) {
				String line = raf.readLine();
				if (nonNull(line)) {
					lines.add(line);
					if (!stopper.apply(line)) {
						break;
					}
				}
			}
			return new ReadResult(startPos, raf.getFilePointer(), raf.length(), lines);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Seek reading file to batch string buffer. Note: Each element of the
	 * returned list string does not correspond to a line of the physical file
	 * content. The result you want to read corresponds to a line of the
	 * physical file
	 * 
	 * @param filename
	 *            the system-dependent filename
	 * @param startPos
	 *            seek page start position.
	 * @param limitPos
	 *            seek page size, Note: that it will contain line breaks.
	 * @return
	 */
	public static String seekReadString(String filename, long startPos, int limitPos) {
		final StringBuffer lineBuf = new StringBuffer();
		doSeekReadFile(filename, startPos, limitPos, DEFAULT_RW_BUF_SIZE, (data, totalLen) -> lineBuf.append(new String(data)));
		return lineBuf.toString();
	}

	/**
	 * DO seek reading and limit bytes in file.
	 * 
	 * @param filename
	 *            the system-dependent filename
	 * @param startPos
	 *            seek page start position.
	 * @param limitPos
	 *            seek page size, Note: that it will contain line breaks.
	 * @param bufSize
	 *            Buffer size per batch read
	 * @param processor
	 *            Each read processing program
	 */
	public static void doSeekReadFile(String filename, long startPos, int limitPos, int bufSize, SeekProcessor processor) {
		Assert.hasText(filename, "Read seek filename must not be empty.");
		Assert.isTrue(startPos >= 0, "Read start position must be greater than or equal to 0");
		Assert.isTrue(limitPos > 0, "Read limit position must be greater than to 0");

		try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
			raf.seek(startPos);
			byte[] data = new byte[bufSize];
			int len = 0, totalLen = 0;
			while ((len = raf.read(data)) != -1) {
				totalLen += len;
				if (totalLen >= limitPos) { // Read enough data?
					// e.g. File(3000bytes)
					// bufSize=1024, length=1000 => 0,1024,...
					int needLen = limitPos;
					if (limitPos > bufSize) {
						// e.g. File(3000bytes)
						// bufSize=1024, length=2000 => 0,1024,2048,...
						needLen = bufSize - (totalLen - limitPos);
					}
					byte[] needData = new byte[needLen];
					System.arraycopy(data, 0, needData, 0, needLen);
					processor.process(needData, (totalLen - len + needLen));
					break;
				} else {
					processor.process(data, totalLen);
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

		/**
		 * Processing seek each.
		 * 
		 * @param data
		 *            Every fetch bytes data.
		 * @param totalLen
		 *            total fetch bytes length.
		 */
		void process(byte[] data, int totalLen);
	}

	/**
	 * Read result.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月14日
	 * @since
	 */
	public final static class ReadResult {

		private long startPos;
		private long endPos;
		private long length;
		private List<String> lines;

		public ReadResult(long startPos, long endPos, long length, List<String> lines) {
			this.startPos = startPos;
			this.endPos = endPos;
			this.length = length;
			this.lines = lines;
		}

		public long getStartPos() {
			return startPos;
		}

		public void setStartPos(long startPos) {
			this.startPos = startPos;
		}

		public long getEndPos() {
			return endPos;
		}

		public void setEndPos(long endPos) {
			this.endPos = endPos;
		}

		public List<String> getLines() {
			return lines;
		}

		public void setLines(List<String> lines) {
			this.lines = lines;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		@Override
		public String toString() {
			return "ReadResult [startPos=" + startPos + ", pointer=" + endPos + ", length=" + length + ", lines=" + lines + "]";
		}

	}

	public static void main(String[] args) {
		System.out.println(readLines("C:\\Users\\Administrator\\Desktop\\aaa.txt", 2, 12));
		System.out.println("--------------------");
		System.out.println(seekReadString("C:\\Users\\Administrator\\Desktop\\aaa.txt", 3L, 12));
		System.out.println("--------------------");
		System.out.println(seekReadLines("C:\\Users\\Administrator\\Desktop\\aaa.txt", 13L, 6, line -> {
			return !line.equalsIgnoreCase("EOF"); // End if 'EOF'
		}));
	}

}