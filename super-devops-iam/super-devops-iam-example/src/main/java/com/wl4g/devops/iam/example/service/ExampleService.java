package com.wl4g.devops.iam.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {
	final private static Logger log = LoggerFactory.getLogger(ExampleService.class);

	public String test1(String name) {
		log.info("Service test1 processing... {}", name);
		return name;
	}

}
