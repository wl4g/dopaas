package com.wl4g.devops.coss.access.model;

import com.wl4g.devops.coss.model.ObjectKey;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author vjay
 * @date 2020-03-29 11:09:00
 */
public class ObjectMetadataModel {

    private ObjectKey path;

    private long contentLength;

    private String contentType;

    private String contentMd5;

    private String contentEncoding;

    private String cacheControl;

    private String contentDisposition;

    private String etag;

    private String versionId;

    private Long mtime;

    private Long atime;

    private Long etime;

    private String acl;

    private Map<String, String> userMetadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public ObjectKey getPath() {
        return path;
    }

    public void setPath(ObjectKey path) {
        this.path = path;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentMd5() {
        return contentMd5;
    }

    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Long getMtime() {
        return mtime;
    }

    public void setMtime(Long mtime) {
        this.mtime = mtime;
    }

    public Long getAtime() {
        return atime;
    }

    public void setAtime(Long atime) {
        this.atime = atime;
    }

    public Long getEtime() {
        return etime;
    }

    public void setEtime(Long etime) {
        this.etime = etime;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(Map<String, String> userMetadata) {
        this.userMetadata = userMetadata;
    }
}
