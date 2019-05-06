package com.wl4g.devops.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wl4g.devops.shell.annotation.EnabledShellServer;

@EnabledShellServer
@SpringBootApplication
public class DevOpsShellExample {

	public static void main(String[] args) {
		SpringApplication.run(DevOpsShellExample.class, args);
	}

}
