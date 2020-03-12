package com.wl4g.devops.coss.aliyun;

import java.io.IOException;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;

/**
 * Examples of uploading with enabling checkpoint file.
 *
 */
public class OssUploadSample {

	private static String endpoint = "oss-cn-shenzhen.aliyuncs.com";
	private static String accessKeyId = "LTAI4Fk9pjU7ezN2yVeiffYm";
	private static String accessKeySecret = System.getenv("aliyun.secret");

	private static String bucketName = "sm-clound";
	private static String key = "OssUploadSample.txt";
	// Note: Must exist
	private static String uploadFile = "/tmp/OssUploadSample.txt";

	public static void main(String[] args) throws IOException {
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

		try {
			UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
			// The local file to upload---it must exist.
			uploadFileRequest.setUploadFile(uploadFile);
			// Sets the concurrent upload task number to 5.
			uploadFileRequest.setTaskNum(5);
			// Sets the part size to 1MB.
			uploadFileRequest.setPartSize(1024 * 1024 * 1);
			// Enables the checkpoint file. By default it's off.
			uploadFileRequest.setEnableCheckpoint(true);

			UploadFileResult uploadResult = ossClient.uploadFile(uploadFileRequest);

			CompleteMultipartUploadResult multipartUploadResult = uploadResult.getMultipartUploadResult();
			System.out.println(multipartUploadResult.getETag());

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
