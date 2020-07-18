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
package com.wl4g.devops.coss.aliyun;

import java.io.IOException;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * The examples about how to enable checkpoint in downloading.
 *
 */
public class OssDownloadTests {

	private static String endpoint = "oss-cn-shenzhen.aliyuncs.com";
	private static String accessKeyId = "LTAI4Fk9pjU7ezN2yVeiffYm";
	private static String accessKeySecret = System.getenv("aliyun.secret");

	private static String bucketName = "sm-clound";
	private static String key = "OssDownloadTests.txt"; // Note: Must exist

	private static String downloadFile = "/tmp/OssDownloadTests.txt";

	public static void main(String[] args) throws IOException {

		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

		try {
			DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
			// Sets the local file to download to
			downloadFileRequest.setDownloadFile(downloadFile);
			// Sets the concurrent task thread count 5. By default it's 1.
			downloadFileRequest.setTaskNum(5);
			// Sets the part size, by default it's 100K.
			downloadFileRequest.setPartSize(1024 * 1024 * 1);
			// Enable checkpoint. By default it's false.
			downloadFileRequest.setEnableCheckpoint(true);

			DownloadFileResult downloadResult = ossClient.downloadFile(downloadFileRequest);

			ObjectMetadata objectMetadata = downloadResult.getObjectMetadata();
			System.out.println(objectMetadata.getETag());
			System.out.println(objectMetadata.getLastModified());
			System.out.println(objectMetadata.getUserMetadata().get("meta"));

		} catch (OSSException oe) {
			System.out.println("Caught an OSSException, which means your request made it to OSS, "
					+ "but was rejected with an error response for some reason.");
			System.out.println("Error Message: " + oe.getErrorMessage());
			System.out.println("Error Code:       " + oe.getErrorCode());
			System.out.println("Request ID:      " + oe.getRequestId());
			System.out.println("Host ID:           " + oe.getHostId());
		} catch (ClientException ce) {
			System.out.println("Caught an ClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with OSS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ce.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
	}

}