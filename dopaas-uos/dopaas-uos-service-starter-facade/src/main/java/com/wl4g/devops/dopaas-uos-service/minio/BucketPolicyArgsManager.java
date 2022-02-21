package com.wl4g.devops.uos.minio;

import com.wl4g.components.common.collection.CollectionUtils2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.devops.uos.common.model.ACL;
import com.wl4g.devops.uos.common.model.AccessControlList;
import com.wl4g.devops.uos.common.model.BucketPolicyArgs;
import io.minio.GetBucketPolicyArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.uos.common.model.BucketPolicyArgs.Statement;
import static com.wl4g.devops.uos.minio.MinioEndpoint.fixKey;

/**
 * @author vjay
 * @date 2020-08-14 18:08:00
 */
public class BucketPolicyArgsManager {

    final private SmartLogger log = SmartLoggerFactory.getLogger(getClass());

    final private static String defautlDenyStatementSid = "defautlDenyStatement";
    final private static String defautlReadonlyStatementSid = "defautlReadonlyStatement";
    final private static String defautlReadWriteStatementSid = "defautlReadWriteStatement";

    final private static String effect_allow = "Allow";
    final private static String effect_deny = "Deny";

    final private static Integer policy_default = 0;
    final private static Integer policy_deny = 1;
    final private static Integer policy_readonly = 2;
    final private static Integer policy_readwrite = 3;

    private MinioClient minioClient;

    public BucketPolicyArgsManager(MinioClient minioClient) {
        this.minioClient = minioClient;
    }


    /**
     * @param bucket
     * @param key
     * @param policy: 0default(private); 1Deny(private deny); 2readonly; 3readwrite;
     */
    public void setObjectPolicy(String bucket, String key, ACL acl) throws Exception {
        key = fixKey(key,'/');
        BucketPolicyArgs bucketPolicyArgs = getPolicy(bucket);

        boolean readable = false;
        boolean writable = false;
        String sid = "";
        String effect = null;

        Statement statement = getStatement(bucketPolicyArgs, acl);

        //remove old policy
        removeResourceByKey(bucketPolicyArgs, bucket, key);

        if (acl == ACL.Default) {
            //already remove old policy, return;
            savePolicy(bucket,bucketPolicyArgs);
            return;
        } else if (acl == ACL.Private) {
            sid = defautlDenyStatementSid;
            effect = effect_deny;
        } else if (acl == ACL.PublicRead) {
            readable = true;
            sid = defautlReadonlyStatementSid;
            effect = effect_allow;
        } else if (acl == ACL.PublicReadWrite) {
            readable = true;
            writable = true;
            sid = defautlReadWriteStatementSid;
            effect = effect_allow;
        }

        if (Objects.isNull(statement)) {
            statement = buildStatement(sid, effect, readable, writable);
            List<Statement> statements = bucketPolicyArgs.getStatement();
            if(Objects.isNull(statements)){
                bucketPolicyArgs.setStatement(new ArrayList<>());
            }
            bucketPolicyArgs.getStatement().add(statement);
        }

        //arn:aws:s3:::awsexamplebucket1/*
        statement.getResource().add(getResource(bucket, key));

        savePolicy(bucket,bucketPolicyArgs);

    }

    private void savePolicy(String bucket, BucketPolicyArgs bucketPolicyArgs) throws Exception{
        SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                .bucket(bucket)
                .config(JacksonUtils.toJSONString(bucketPolicyArgs))
                .build();

        minioClient.setBucketPolicy(setBucketPolicyArgs);
    }

    public AccessControlList getObjectPolicy(String bucket, String key) throws Exception {
        key = fixKey(key,'/');
        BucketPolicyArgs bucketPolicyArgs = getPolicy(bucket);

        //ObjectPolicy objectPolicy = new ObjectPolicy();

        AccessControlList accessControlList = new AccessControlList();

        Statement denyStatement = getStatement(bucketPolicyArgs, ACL.Private);
        Statement readonlyStatement = getStatement(bucketPolicyArgs, ACL.PublicRead);
        Statement readwriteStatement = getStatement(bucketPolicyArgs, ACL.PublicReadWrite);

        //deny
        if (Objects.nonNull(denyStatement)) {
            for (String res : denyStatement.getResource()) {
                String resource = getResource(bucket, key);
                if (StringUtils.equals(res, resource)) {
                    accessControlList.setAcl(ACL.Private);
                    accessControlList.setRealAcl(ACL.Private);
                    return accessControlList;
                }
            }
        }

        //readonly
        if (Objects.nonNull(readonlyStatement)) {
            for (String res : readonlyStatement.getResource()) {
                String resource = getResource(bucket, key);
                if (StringUtils.equals(res, resource)) {
                    accessControlList.setAcl(ACL.PublicRead);
                    accessControlList.setRealAcl(ACL.PublicRead);
                    return accessControlList;
                }
            }
        }

        //readwrite
        if (Objects.nonNull(readwriteStatement)) {
            for (String res : readwriteStatement.getResource()) {
                String resource = getResource(bucket, key);
                if (StringUtils.equals(res, resource)) {
                    accessControlList.setAcl(ACL.PublicReadWrite);
                    accessControlList.setRealAcl(ACL.PublicReadWrite);
                    return accessControlList;
                }
            }
        }

        //not found in resource, try find from parents
        accessControlList.setAcl(ACL.Default);
        if (isRealPolicyInStatment(denyStatement, bucket, key)) {
            accessControlList.setRealAcl(ACL.Private);
        } else if (isRealPolicyInStatment(readonlyStatement, bucket, key)) {
            accessControlList.setRealAcl(ACL.PublicRead);
        } else if (isRealPolicyInStatment(readwriteStatement, bucket, key)) {
            accessControlList.setRealAcl(ACL.PublicReadWrite);
        } else {
            accessControlList.setRealAcl(ACL.Default);
        }

        accessControlList.setConfig(JacksonUtils.toJSONString(bucketPolicyArgs));
        return accessControlList;
    }


    private BucketPolicyArgs getPolicy(String bucket) throws Exception {
        GetBucketPolicyArgs getBucketPolicyArgs = GetBucketPolicyArgs.builder().bucket(bucket).build();
        String bucketPolicy = null;
        try {
            bucketPolicy = minioClient.getBucketPolicy(getBucketPolicyArgs);
        }catch (Exception e){
            log.error("Get Policy fail.", e);
        }
        if (StringUtils.isBlank(bucketPolicy)) {
            return buildPolicy();
        } else {
            return JacksonUtils.parseJSON(bucketPolicy, BucketPolicyArgs.class);
        }
    }

    private BucketPolicyArgs buildPolicy() {
        BucketPolicyArgs bucketPolicyArgs = new BucketPolicyArgs();
        bucketPolicyArgs.setVersion("2012-10-17");
        return bucketPolicyArgs;
    }


    private Statement getStatement(BucketPolicyArgs bucketPolicyArgs, ACL acl) {

        for (Statement statement : bucketPolicyArgs.getStatement()) {
            if (acl == ACL.Private) {
                if (StringUtils.equals(statement.getSid(), defautlDenyStatementSid)) {
                    return statement;
                }
            } else if (acl == ACL.PublicRead) {
                if (StringUtils.equals(statement.getSid(), defautlReadonlyStatementSid)) {
                    return statement;
                }
            } else if (acl == ACL.PublicReadWrite) {
                if (StringUtils.equals(statement.getSid(), defautlReadWriteStatementSid)) {
                    return statement;
                }
            }
        }
        return null;
    }


    private Statement buildStatement(String sid, String effect, boolean readable, boolean writable) {
        Statement statement = new Statement();
        statement.setSid(sid);
        statement.setEffect(effect);
        BucketPolicyArgs.Principal principal = new BucketPolicyArgs.Principal();
        List<String> aws = new ArrayList<>();
        aws.add("*");
        principal.setAws(aws);
        statement.setPrincipal(principal);
        List<String> action = statement.getAction();
        // when private, action
        //if (readable) {
            action.add("s3:GetObject");
        //}
        if (writable) {
            action.add("s3:PutObject");
        }
        statement.setAction(action);
        return statement;
    }

    private void removeResourceByKey(BucketPolicyArgs bucketPolicyArgs, String bucket, String key) {
        Statement denyStatement = getStatement(bucketPolicyArgs, ACL.Private);
        Statement readonlyStatement = getStatement(bucketPolicyArgs, ACL.PublicRead);
        Statement readwriteStatement = getStatement(bucketPolicyArgs, ACL.PublicReadWrite);


        List<String> needDel = new ArrayList<>();
        //deny
        if (Objects.nonNull(denyStatement)) {
            for (String res : denyStatement.getResource()) {
                String resource = getResource(bucket, key);
                if (StringUtils.equals(res, resource)) {
                    needDel.add(res);
                }
            }
            denyStatement.getResource().removeAll(needDel);
            needDel.clear();
            if(CollectionUtils2.isEmpty(denyStatement.getResource())){
                bucketPolicyArgs.getStatement().remove(denyStatement);
            }
        }


        //readonly
        if (Objects.nonNull(readonlyStatement)) {
            for (String res : readonlyStatement.getResource()) {
                String resource = getResource(bucket, key);
                if (StringUtils.equals(res, resource)) {
                    needDel.add(res);
                }
            }
            readonlyStatement.getResource().removeAll(needDel);
            needDel.clear();
            if(CollectionUtils2.isEmpty(readonlyStatement.getResource())){
                bucketPolicyArgs.getStatement().remove(readonlyStatement);
            }
        }

        //readwrite
        if (Objects.nonNull(readwriteStatement)) {
            for (String res : readwriteStatement.getResource()) {
                String resource = getResource(bucket, key);
                if (StringUtils.equals(res, resource)) {
                    needDel.add(res);
                }
            }
            readwriteStatement.getResource().removeAll(needDel);
            if(CollectionUtils2.isEmpty(readwriteStatement.getResource())){
                bucketPolicyArgs.getStatement().remove(readwriteStatement);
            }
        }
    }

    private boolean isRealPolicyInStatment(Statement statement, String bucket, String key) {
        if (Objects.isNull(statement)) {
            return false;
        }
        String str = bucket + "/" + key;
        for (String res : statement.getResource()) {
            String path = res.substring(13);//arn:aws:s3:::mybucket/*
            if (path.endsWith("*")) {
                path = path.substring(0, path.length() - 1);
                if (str.startsWith(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getResource(String bucket, String key) {
        if (StringUtils.isBlank(key)) {
            return "arn:aws:s3:::" + bucket + "/*";
        } else if (isDir(key)) {
            return "arn:aws:s3:::" + bucket + "/" + key + "*";
        } else {
            return "arn:aws:s3:::" + bucket + "/" + key;
        }
    }


    private boolean isDir(String key) {
        return StringUtils.isBlank(key) || key.endsWith("/");
    }


}
