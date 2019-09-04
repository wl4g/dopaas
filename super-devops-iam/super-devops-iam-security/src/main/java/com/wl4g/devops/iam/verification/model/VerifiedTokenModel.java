package com.wl4g.devops.iam.verification.model;

/**
 * @author vjay
 * @date 2019-09-04 17:50:00
 */
public class VerifiedTokenModel {

    private boolean verified;

    private String verifiedToken;

    public VerifiedTokenModel(boolean verified, String verifiedToken) {
        this.verified = verified;
        this.verifiedToken = verifiedToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerifiedToken() {
        return verifiedToken;
    }

    public void setVerifiedToken(String verifiedToken) {
        this.verifiedToken = verifiedToken;
    }
}
