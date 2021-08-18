package com.wl4g.dopaas.lcdp.dds.service.evaluate;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class EvaluatorSpec implements Serializable {
    private static final long serialVersionUID = 4320766245447481229L;
    public static final int DEFAULT_LIMIT_OPERATION_RECORDS = 5000;
    public static final int DEFAULT_METADATA_EXPIRE_MS = 1 * 60 * 60 * 1000;

    private int limitOperationRecords = DEFAULT_LIMIT_OPERATION_RECORDS;
    private long metadataExpireMs = DEFAULT_METADATA_EXPIRE_MS;

}