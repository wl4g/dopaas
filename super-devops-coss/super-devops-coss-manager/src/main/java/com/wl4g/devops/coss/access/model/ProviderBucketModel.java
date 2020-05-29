package com.wl4g.devops.coss.access.model;

/**
 * @author vjay
 * @date 2020-05-29 14:31:00
 */
public class ProviderBucketModel {

    private String provider;

    private String bucketName;

    public ProviderBucketModel() {
    }

    public ProviderBucketModel(String provider, String bucketName) {
        this.provider = provider;
        this.bucketName = bucketName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
