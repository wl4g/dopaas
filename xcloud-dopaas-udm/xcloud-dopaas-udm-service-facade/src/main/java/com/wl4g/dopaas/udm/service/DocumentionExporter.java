package com.wl4g.dopaas.udm.service;

import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 * @date 2021-04-15 09:47:00
 */
@FeignConsumer(name = "${provider.serviceId.udm-facade:udm-facade}")
public interface DocumentionExporter {

	@RequestMapping(value = "/export", method = POST)
	String export(@RequestParam("md") String md, @RequestParam("template") String template) throws Exception;

}
