package com.wl4g.devops.dts.codegen.database;

/**
 * @author vjay
 * @date 2020-09-07 17:20:00
 */
public abstract class AbstractMetadataPaser implements MetadataPaser {

    //init jdbc template

    @Override
    public void queryColumns() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryTable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryVersion() {
        throw new UnsupportedOperationException();
    }
}
