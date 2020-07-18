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
package com.wl4g.devops.components.tools.common.io;

import com.wl4g.devops.components.tools.common.function.ProcessFunction;
import com.wl4g.devops.components.tools.common.log.SmartLogger;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * @author vjay
 * @date 2020-03-24 16:24:00
 */
public abstract class FileLockUtils {

	final private static SmartLogger log = getLogger(FileLockUtils.class);

	/**
	 * Try lock
	 * 
	 * @param file
	 * @param processor
	 * @param <R>
	 * @return
	 * @throws Exception
	 */
	public static <R> R doTryLock(File file, ProcessFunction<FileLock, R> processor) throws Exception {
		// 给该文件加锁
		RandomAccessFile randomAccessFile = null;
		FileChannel fileChannel = null;
		FileLock fileLock = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			fileChannel = randomAccessFile.getChannel();
			fileLock = fileChannel.tryLock();
			return processor.process(fileLock);
		} catch (OverlappingFileLockException ofe) {
		} finally {
			try {
				if (fileLock != null) {
					fileLock.release();
				}
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}
			try {
				if (fileChannel != null) {
					fileChannel.close();
				}
			} catch (IOException e2) {
				log.error(e2.getMessage(), e2);
			}
			try {
				if (randomAccessFile != null) {
					randomAccessFile.close();
				}
			} catch (IOException e3) {
				log.error(e3.getMessage(), e3);
			}
		}
		return null;
	}

}