package com.wl4g.devops.erm.dns.model;

/**
 * @author vjay
 * @date 2020-07-02 15:19:00
 */
public enum ResolveType {

    A,
    AAAA,
    CNAME,
    TXT,
    NS,
    MX,
    SRV,
    SOA;

    /**
     * Converter string to {@link ResolveType}
     *
     * @param resolveType
     * @return
     */
    public static ResolveType of(String resolveType) {
        ResolveType wh = safeOf(resolveType);
        if (wh == null) {
            throw new IllegalArgumentException(String.format("Illegal resolveType '%s'", resolveType));
        }
        return wh;
    }

    /**
     * Safe converter string to {@link Action}
     *
     * @param resolveType
     * @return
     */
    public static ResolveType safeOf(String resolveType) {
        for (ResolveType t : values()) {
            if (String.valueOf(resolveType).equalsIgnoreCase(t.name())) {
                return t;
            }
        }
        return null;
    }
}
