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
package com.wl4g.dopaas.udm.service.md;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.udm.config.DocProperties;
import com.wl4g.dopaas.udm.util.PathUtils;

import freemarker.template.Template;

/**
 * {@link FileLocalMdLocator}
 *
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-19
 * @sine v1.0.0
 * @see
 */
public class FileLocalMdLocator implements MdLocator {

	protected final SmartLogger log = getLogger(getClass());

	final private static String MD_PATH = "/md";

	private @Autowired  DocProperties docProperties;

	@Override
	public List<MdResource> locate(String provider) throws Exception {

		List<MdResource> tpls = new ArrayList<>();

		String basePath = docProperties.getBasePath();
		String path = PathUtils.splicePath(basePath, MD_PATH, provider);

		Collection<File> files = FileIOUtils.listFiles(new File(path), null, true);
		for (File file : files) {
			tpls.add(wrapTemplate(path, file));
		}
		;

		return tpls;
	}

	@Override
	public List<MdMenuTree> loadMenuTree(String provider) throws Exception {

		String basePath = docProperties.getBasePath();
		String path = PathUtils.splicePath(basePath, MD_PATH, provider);
		List<MdMenuTree> mdMenuTrees = new ArrayList<>();

		File[] files = new File(path).listFiles();

		getChildren(path, mdMenuTrees, files);
		return mdMenuTrees;
	}

	private void getChildren(String basePath, List<MdMenuTree> mdMenuTrees, File[] files) {
		for (File file : files) {
			MdMenuTree mdMenuTree = new MdMenuTree();

			int i = file.getAbsolutePath().indexOf(basePath);
			String pathname = null;
			if (i >= 0) {
				pathname = file.getAbsolutePath().substring(i + basePath.length());
			}
			mdMenuTree.setName(file.getName());
			mdMenuTree.setPath(pathname);
			mdMenuTree.setDir(file.isDirectory() ? "true" : "false");
			mdMenuTrees.add(mdMenuTree);

			if (file.isDirectory()) {
				// Collection<File> childrenFiles =
				// FileIOUtils.listFilesAndDirs(file, null, null);
				File[] childrenFiles = file.listFiles();
				if (childrenFiles != null && childrenFiles.length > 0) {
					getChildren(basePath, mdMenuTree.getChildren(), childrenFiles);
				}
			}
		}

	}

	/**
	 * Wrapper {@link Template}
	 *
	 * @param res
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	private static MdResource wrapTemplate(String basePath, File file) throws Exception {
		int i = file.getAbsolutePath().indexOf(basePath);
		String pathname = null;
		if (i >= 0) {
			pathname = file.getAbsolutePath().substring(i + basePath.length());
		}
		return new MdResource(pathname, FileIOUtils.readFileToByteArray(file));
	}

}