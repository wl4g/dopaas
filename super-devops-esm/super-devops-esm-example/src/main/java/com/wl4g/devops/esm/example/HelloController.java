package com.wl4g.devops.esm.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@RequestMapping("/echo")
	public String helloEcho() {
		return "Welcome to esm example!";
	}

}
