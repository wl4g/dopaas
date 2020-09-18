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
package com.wl4g.devops.dts.codegen.web;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static java.lang.Integer.valueOf;

/**
 * {@link GenerateController}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@RestController
@RequestMapping("/gen/configure")
public class GenerateController {

	final private static SmartLogger log = SmartLoggerFactory.getLogger(GenerateController.class);

	@Autowired
	private GenerateService genConfigurationService;

	@RequestMapping("loadTables")
	public RespBase<?> loadTables(Integer databaseId) {
		RespBase<Object> resp = RespBase.create();
		List<String> strings = genConfigurationService.loadTables(databaseId);
		resp.setData(strings);
		return resp;
	}

	@RequestMapping("loadMetadata")
	public RespBase<?> loadMetadata(Integer databaseId, String tableName) {
		RespBase<Object> resp = RespBase.create();
		GenTable genTable = genConfigurationService.loadMetadata(databaseId, tableName);
		resp.setData(genTable);
		return resp;
	}

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String tableName, Integer projectId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genConfigurationService.page(pm, tableName, projectId));
		return resp;
	}

	@RequestMapping("save")
	public RespBase<?> save(@RequestBody GenTable genTable) {
		RespBase<Object> resp = RespBase.create();
		genConfigurationService.saveGenConfig(genTable);
		return resp;
	}

	@RequestMapping("detail")
	public RespBase<?> detail(Integer tableId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(genConfigurationService.detail(tableId));
		return resp;
	}

	@RequestMapping("del")
	public RespBase<?> del(Integer tableId) {
		RespBase<Object> resp = RespBase.create();
		genConfigurationService.delete(tableId);
		return resp;
	}

	@RequestMapping("generate")
	public RespBase<?> generate(String id) {
		hasTextOf(id, "id");
		RespBase<Object> resp = RespBase.create();
		genConfigurationService.generate(valueOf(id));
		return resp;
	}


	@RequestMapping("downloadTest")
	public void downloadTest(HttpServletResponse response) throws IOException {

		zip("/Users/vjay/.codegen-workspace/jobs/job.root-276481024-1600339253448",response);

		/*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		try{


		}finally {
			try {
				zip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		IOUtils.write(outputStream.toByteArray(), response.getOutputStream());*/
	}

	public static void zip(String sourceFileName, HttpServletResponse response){
		ZipOutputStream out = null;
		BufferedOutputStream bos = null;
		try {
			//将zip以流的形式输出到前台
			response.setHeader("content-type", "application/octet-stream");
			response.setCharacterEncoding("utf-8");
			// 设置浏览器响应头对应的Content-disposition
			//参数中 testZip 为压缩包文件名，尾部的.zip 为文件后缀
			response.setHeader("Content-disposition",
					"attachment;filename=\""+"codegen"+"\".zip");
			//创建zip输出流
			out = new ZipOutputStream(response.getOutputStream());
			//创建缓冲输出流
			bos = new BufferedOutputStream(out);
			File sourceFile = new File(sourceFileName);
			//调用压缩函数
			compress(out, bos, sourceFile, sourceFile.getName());
			out.flush();
			log.info("压缩完成");
		} catch (Exception e) {
			log.error("ZIP压缩异常："+e.getMessage(),e);
		} finally {
			ioClose(bos,out);
		}
	}

	public static void compress(ZipOutputStream out, BufferedOutputStream bos, File sourceFile, String base){
		FileInputStream fos = null;
		BufferedInputStream bis = null;
		try {
			//如果路径为目录（文件夹）
			if (sourceFile.isDirectory()) {
				//取出文件夹中的文件（或子文件夹）
				File[] flist = sourceFile.listFiles();
				if (flist.length == 0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
					out.putNextEntry(new ZipEntry(base + "/"));
				} else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
					for (int i = 0; i < flist.length; i++) {
						compress(out, bos, flist[i], base + "/" + flist[i].getName());
					}
				}
			} else {//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
				out.putNextEntry(new ZipEntry(base));
				fos = new FileInputStream(sourceFile);
				bis = new BufferedInputStream(fos);

				int tag;
				//将源文件写入到zip文件中
				while ((tag = bis.read()) != -1) {
					out.write(tag);
				}

				bis.close();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ioClose(bis,fos);
		}
	}

	public static void ioClose(Closeable... io) {
		for (Closeable temp : io) {
			try {
				if (null != temp)
					temp.close();
			} catch (IOException e) {
				log.error("close error",e);
			}
		}
	}




}