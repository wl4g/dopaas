package com.zrk.demo.web;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class SysErrorController implements ErrorController {


	private static final String ERROR_PATH = "error";

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	public String handleError() {
		return "404";
	}

	

}
