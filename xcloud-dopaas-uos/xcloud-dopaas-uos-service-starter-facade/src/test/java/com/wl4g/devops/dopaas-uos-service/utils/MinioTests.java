package com.wl4g.devops.uos.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.wl4g.components.common.io.ByteStreamUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-08-14 10:37:00
 */
public class MinioTests {

    private static MinioClient buildMinioClient() {

        return MinioClient.builder()
                .endpoint("http://10.0.0.160:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    public static void main(String[] args) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InvalidBucketNameException, InsufficientDataException, InternalException, BucketPolicyTooLargeException, InvalidExpiresRangeException {
        //setObjectTagsTest();
        //getObjectTags();

        //putObjectTest();
        //statObjectTest();

        //setBucketPolicyTest();
        //getBucketPolicyTest();

        s3SelectTest();

    }

    public static void setObjectTagsTest() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        Map<String, String> tags = new HashMap<>();
        tags.put("mytags1", "1");
        tags.put("mytags2", "2");
        tags.put("mytags3", "3");
        SetObjectTagsArgs setObjectTagsArgs = SetObjectTagsArgs.builder()
                .bucket("mybucket1")
                .object("devops_dev-0310.sql")
                .tags(tags)
                .build();

        buildMinioClient().setObjectTags(setObjectTagsArgs);
    }

    public static void getObjectTags() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        GetObjectTagsArgs getObjectTagsArgs = GetObjectTagsArgs.builder()
                .bucket("mybucket1")
                .object("devops_dev-0310.sql")
                .build();
        Tags objectTags = buildMinioClient().getObjectTags(getObjectTagsArgs);
        System.out.println(objectTags);
    }

    public static void putObjectTest() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        Map<String, String> headers = new HashMap<>();
        headers.put("etag", "123");
        headers.put("Etag", "234");
        headers.put("myheader3", "3");

        Multimap<String, String> headers2 = HashMultimap.create();
        headers2.put("etag", "113");
        headers2.put("Etag", "345");
        headers2.put("my2header3", "3");

        FileInputStream file = new FileInputStream("/Users/vjay/Downloads/3.5.2.xlsx");

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("mybucket1")
                .object("3.5.2.xlsx")
                .stream(file, -1, 5 * 1024 * 1024)
                .headers(headers)
                .userMetadata(headers)
                .headers(headers2)
                .build();
        buildMinioClient().putObject(putObjectArgs);
    }

    public static void statObjectTest() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket("mybucket1")
                .object("3.5.2.xlsx")
                .build();
        ObjectStat objectStat = buildMinioClient().statObject(statObjectArgs);

        String url = buildMinioClient().getObjectUrl("mybucket1", "devops_dev-0310.sql");
        System.out.println(url);
        System.out.println(objectStat);
    }

    public static void setBucketPolicyTest() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        String json1 = "{\n" +
                "    \"Statement\":[\n" +
                "        {\n" +
                "            \"Action\":[\n" +
                "                \"s3:GetBucketLocation\",\n" +
                "                \"s3:ListBucket\"\n" +
                "            ],\n" +
                "            \"Effect\":\"Allow\",\n" +
                "            \"Principal\":\"*\",\n" +
                "            \"Resource\":\"arn:aws:s3:::mybucket1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Action\":\"s3:GetObject\",\n" +
                "            \"Effect\":\"Allow\",\n" +
                "            \"Principal\":\"*\",\n" +
                "            \"Resource\":\"arn:aws:s3:::mybucket1/devops_dev-0310.sql\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"Version\":\"2012-10-17\"\n" +
                "}";

        String json2 = "{\n" +
                "    \"Statement\":[\n" +
                "        {\n" +
                "            \"Action\":[\n" +
                "                \"s3:GetBucketLocation\",\n" +
                "                \"s3:ListBucket\"\n" +
                "            ],\n" +
                "            \"Effect\":\"Allow\",\n" +
                "            \"Principal\":\"*\",\n" +
                "            \"Resource\":\"arn:aws:s3:::mybucket1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Action\":\"s3:GetObject\",\n" +
                "            \"Effect\":\"Allow\",\n" +
                "            \"Principal\":\"*\",\n" +
                "            \"Resource\":\"arn:aws:s3:::mybucket1/*\"\n" +
                "        },\n" +

                "        {\n" +
                "            \"Action\":\"s3:GetObject\",\n" +
                "            \"Effect\":\"Deny\",\n" +
                "            \"Principal\":\"*\",\n" +
                "            \"Resource\":\"arn:aws:s3:::mybucket1/dir/dir2/*\"\n" +
                "        },\n" +

                "        {\n" +
                "            \"Action\":\"s3:GetObject\",\n" +
                "            \"Effect\":\"Allow\",\n" +
                "            \"Principal\":\"*\",\n" +
                "            \"Resource\":\"arn:aws:s3:::mybucket1/dir/dir2/dir3/*\"\n" +
                "        }\n" +

                "    ],\n" +
                "    \"Version\":\"2012-10-17\"\n" +
                "}";


        SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                .bucket("mybucket1")
                .config(json2)

                .build();
        buildMinioClient().setBucketPolicy(setBucketPolicyArgs);
    }

    public static void getBucketPolicyTest() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException, BucketPolicyTooLargeException {
        GetBucketPolicyArgs getBucketPolicyArgs = GetBucketPolicyArgs.builder()
                .bucket("mybucket1")
                .build();
        String bucketPolicy = buildMinioClient().getBucketPolicy(getBucketPolicyArgs);
        System.out.println(bucketPolicy);



        MinioClient client = MinioClient.builder()
                .endpoint("http://10.0.0.160:9000")
                //.credentials("minioadmin", "minioadmin")
                .build();

        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("mybucket1")
                .object("devops_dev-0310.sql")
                .build();
        InputStream object = client.getObject(getObjectArgs);

        /*GetObjectArgs getObjectArgs2 = GetObjectArgs.builder()
                .bucket("mybucket1")
                .object("dir/dir2/devops-0228.sql")
                .build();
        InputStream object2 = client.getObject(getObjectArgs2);
        System.out.println(12);*/

        GetObjectArgs getObjectArgs3 = GetObjectArgs.builder()
                .bucket("mybucket1")
                .object("dir/dir2/dir3/test.htm")
                .build();
        InputStream object3 = client.getObject(getObjectArgs3);



        System.out.println(12);
    }


    public static void s3SelectTest() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {

        /*InputSerialization inputSerialization = new InputSerialization();

        OutputSerialization outputSerialization = new OutputSerialization(' ');
        SelectObjectContentArgs selectObjectContentArgs = SelectObjectContentArgs.builder()
                .inputSerialization(inputSerialization)
                .outputSerialization(outputSerialization)
                .requestProgress(false)
                .bucket("mybucket1")
                .object("minioselect.json")
                .sqlExpression("select * from S3Object[*] limit 10")
                .build();
        SelectResponseStream selectResponseStream = buildMinioClient().selectObjectContent(selectObjectContentArgs);*/


        //@see https://docs.min.io/docs/java-client-api-reference.html#selectObjectContent
        String sqlExpression = "select * from S3Object limit 10";
        //InputSerialization is = new InputSerialization(null, false, null, null, FileHeaderInfo.NONE, null, null, null);
        InputSerialization is = new InputSerialization(CompressionType.NONE,JsonType.DOCUMENT);
        //OutputSerialization os = new OutputSerialization(null, null, null, QuoteFields.ALWAYS, null);
        OutputSerialization os = new OutputSerialization('\n');
        SelectResponseStream stream =
                buildMinioClient().selectObjectContent(
                        SelectObjectContentArgs.builder()
                                .bucket("mybucket1")
                                .object("minioselect.json")
                                .sqlExpression(sqlExpression)
                                .inputSerialization(is)
                                .outputSerialization(os)
                                .requestProgress(false)
                                .build());



        String s = ByteStreamUtils.readFullyToString(stream);
        System.out.println(s);
    }











}
