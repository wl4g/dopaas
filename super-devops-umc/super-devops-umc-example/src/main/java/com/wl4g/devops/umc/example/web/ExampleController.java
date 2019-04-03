package com.wl4g.devops.umc.example.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example/")
public class ExampleController {
	final private static Logger log = LoggerFactory.getLogger(ExampleController.class);

	@RequestMapping("call")
	public String start() {
		log.info("Request calling... ");
		return "ok";
	}

}
