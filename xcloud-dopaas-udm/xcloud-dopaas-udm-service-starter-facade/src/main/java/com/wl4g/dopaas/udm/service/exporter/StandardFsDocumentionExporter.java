package com.wl4g.dopaas.udm.service.exporter;

import com.wl4g.dopaas.udm.service.DocumentionExporter;
import com.wl4g.dopaas.udm.service.EnterpriseMdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2021-04-15 09:55:00
 */
@Component
public class StandardFsDocumentionExporter extends AbstractDocumentionExporter implements DocumentionExporter {

	@Autowired
	private EnterpriseMdService enterpriseMdService;

	@Override
	public String export(String md, String template) throws Exception {
		return enterpriseMdService.formatTemplate(md, template);
	}

}
