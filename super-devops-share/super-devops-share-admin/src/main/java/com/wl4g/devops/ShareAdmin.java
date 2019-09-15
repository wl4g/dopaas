package com.wl4g.devops;

import com.wl4g.devops.iam.client.annotation.EnableIamClient;
import com.wl4g.devops.support.config.internal.logback.LogbackLoggingSystem;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.logging.LoggingSystem;

@EnableIamClient
@MapperScan("com.wl4g.devops.dao.*")
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ShareAdmin {

	static {
		System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LogbackLoggingSystem.class.getName());
	}

	public static void main(String[] args) {
		SpringApplication.run(ShareAdmin.class, args);
	}

}