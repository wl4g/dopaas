package com.wl4g.dopaas.udm.service.exporter;

import com.wl4g.dopaas.udm.service.DocumentionExporter;

/**
 * @author vjay
 * @date 2021-04-15 09:56:00
 */
public abstract class AbstractDocumentionExporter implements DocumentionExporter {

    @Override
    public String export(String md, String template) throws Exception {
        throw new UnsupportedOperationException();
    }
}
