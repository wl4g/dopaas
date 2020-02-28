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
package com.wl4g.devops.coss.fs.hdfs;

import java.io.IOException;

import com.wl4g.devops.coss.fs.CossFileSystem;
import com.wl4g.devops.coss.fs.FileStatus;
import com.wl4g.devops.coss.fs.FsPermission;
import com.wl4g.devops.coss.fs.Path;

public class HdfsCossFileSystem extends CossFileSystem {

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<FileStatus> getFileStatusIter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileStatus getFileStatus(Path f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPermission(Path p, FsPermission permission) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOwner(Path p, String username, String groupname) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimes(Path p, long mtime, long atime) throws IOException {
		// TODO Auto-generated method stub

	}

}
