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
package com.wl4g.devops.coss.hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wl4g.devops.CossServer;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.coss.CossEndpoint;
import com.wl4g.devops.coss.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.model.AccessControlList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CossServer.class, properties = {})
public class HdfsCossEndpointTests {

	@Autowired
	private GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter;

	@Test
	public void hdfsEndpointTest1() throws Exception {
		System.out.println("Starting...");
		CossEndpoint endpoint = endpointAdapter.forOperator(CossProvider.Hdfs);

		System.out.println("createBucket...");
		endpoint.createBucket("sm-clound");

		System.out.println("getBucketAcl...");
		AccessControlList acl = endpoint.getBucketAcl("sm-clound");
		System.out.println(acl);

		System.out.println("putObject...");
		endpoint.putObject("sm-clound", "hdfs-coss-sample.txt", new FileInputStream(createSampleFile()));
		System.out.println("End.");
	}

	private File createSampleFile() throws IOException {
		File file = File.createTempFile("hdfs-coss-sample", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.write("0123456789011234567890\n");
		writer.close();

		return file;
	}

}
