package com.wl4g.devops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.wl4g.devops.support.config.internal.logback.LogbackLoggingSystem;

@EnableDiscoveryClient
@MapperScan("com.wl4g.devops.*.dao")
@SpringBootApplication
public class DevOpsDoc {

	static {
		System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LogbackLoggingSystem.class.getName());
	}

	public static void main(String[] args) {
		SpringApplication.run(DevOpsDoc.class, args);
	}

}
