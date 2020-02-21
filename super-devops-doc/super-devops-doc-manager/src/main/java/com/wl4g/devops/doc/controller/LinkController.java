package com.wl4g.devops.doc.controller;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.doc.service.ShareService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

@RestController
@RequestMapping("/link")
public class LinkController {

	final protected Logger log = getLogger(getClass());

	@Autowired
	private ShareService shareService;

	@CrossOrigin
	@RequestMapping(value = "/rendering")
	public RespBase<?> rendering(String code, String passwd) {
		log.info("rendering file code={} passwd={}", code, passwd);
		return shareService.rendering(code, passwd);
	}

}
