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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wl4g.devops.CossServer;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.common.model.AccessControlList;
import com.wl4g.devops.coss.common.model.ObjectListing;
import com.wl4g.devops.coss.common.model.ObjectSummary;
import com.wl4g.devops.coss.common.model.ObjectValue;
import com.wl4g.devops.coss.common.model.bucket.Bucket;
import com.wl4g.devops.coss.common.model.bucket.BucketList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CossServer.class, properties = {})
@FixMethodOrder(MethodSorters.JVM)
public class HdfsCossEndpointTests {

	private CossEndpoint endpoint;

	@Autowired
	public void createHdfsEndpoint(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
		System.out.println("createHdfsEndpoint...");
		endpoint = endpointAdapter.forOperator(CossProvider.Hdfs);
	}

	@Test
	public void hdfsEndpointCreateBucketTest() throws Exception {
		System.out.println("createBucket...");
		endpoint.createBucket("sm-clound");
	}

	@Test
	public void hdfsEndpointGetBucketAclTest() throws Exception {
		System.out.println("getBucketAcl...");
		AccessControlList acl = endpoint.getBucketAcl("sm-clound");
		System.out.println(acl);
	}

	@Test
	public void hdfsEndpointListBucketsTest() throws Exception {
		System.out.println("listBuckets...");
		BucketList<Bucket> buckets = endpoint.listBuckets(null, null, null);
		System.out.println(buckets);
	}

	@Test
	public void hdfsEndpointPutObjectTest() throws Exception {
		System.out.println("putObject...");
		endpoint.putObject("sm-clound", "hdfs-coss-sample.txt", new FileInputStream(createSampleFile()));
	}

	@Test
	public void hdfsEndpointListObjectsTest() throws Exception {
		System.out.println("listObjects...");
		ObjectListing<ObjectSummary> objects = endpoint.listObjects("sm-clound");
		System.out.println(objects);
	}

	@Test
	public void hdfsEndpointGetObjectTest() throws Exception {
		System.out.println("getObject...");
		ObjectValue object = endpoint.getObject("sm-clound", "hdfs-coss-sample.txt");
		System.out.println(object);
	}

	private File createSampleFile() throws IOException {
		File file = File.createTempFile("hdfs-coss-sample", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		for (int i = 0; i < 100; i++) {
			writer.write("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz\n");
			writer.write("01234567890012345678900123456789001234567890\n");
		}
		writer.close();

		return file;
	}

}