package com.wl4g.devops.scm.example.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.devops.scm.example.service.ExampleService;

@RestController
@RequestMapping("/")
public class ExampleController {
	final private static Logger log = LoggerFactory.getLogger(ExampleController.class);

	@Autowired
	private ExampleService exampleService;

	@RequestMapping("start")
	public String start() {
		log.info("Request starting... " + exampleService);
		this.exampleService.start();
		return "Started";
	}

	@RequestMapping("stop")
	public String stop() {
		log.info("Request stoping... ");
		this.exampleService.stop();
		return "Stoped";
	}

}
