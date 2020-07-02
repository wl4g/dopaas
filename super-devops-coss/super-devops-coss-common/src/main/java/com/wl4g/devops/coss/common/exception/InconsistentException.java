package com.wl4g.devops.coss.common.exception;

/**
 * <p>
 * This exception indicates the checksum returned from Server side is not same
 * as the one calculated from client side.
 * </p>
 * 
 * 
 * <p>
 * Generally speaking, the caller needs to handle the
 * {@link InconsistentException}, because it means the data uploaded or
 * downloaded is not same as its source. Re-upload or re-download is needed to
 * correct the data.
 * </p>
 * 
 * <p>
 * Operations that could throw this exception include putObject, appendObject,
 * uploadPart, uploadFile, getObject, etc.
 * </p>
 * 
 */
public class InconsistentException extends RuntimeException {

    private static final long serialVersionUID = 2140587868503948665L;

    private Long clientChecksum;
    private Long serverChecksum;
    private String requestId;

    public InconsistentException(Long clientChecksum, Long serverChecksum, String requestId) {
        super();
        this.clientChecksum = clientChecksum;
        this.serverChecksum = serverChecksum;
        this.requestId = requestId;
    }

    public Long getClientChecksum() {
        return clientChecksum;
    }

    public void setClientChecksum(Long clientChecksum) {
        this.clientChecksum = clientChecksum;
    }

    public Long getServerChecksum() {
        return serverChecksum;
    }

    public void setServerChecksum(Long serverChecksum) {
        this.serverChecksum = serverChecksum;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getMessage() {
        return "InconsistentException " + "\n[RequestId]: " + getRequestId() + "\n[ClientChecksum]: "
                + getClientChecksum() + "\n[ServerChecksum]: " + getServerChecksum();
    }

}
