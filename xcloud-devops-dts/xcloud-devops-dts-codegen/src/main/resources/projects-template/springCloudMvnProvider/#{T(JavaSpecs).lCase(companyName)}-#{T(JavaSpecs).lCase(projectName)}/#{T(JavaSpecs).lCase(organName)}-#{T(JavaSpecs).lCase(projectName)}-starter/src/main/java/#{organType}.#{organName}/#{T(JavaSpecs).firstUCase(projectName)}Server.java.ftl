// ${watermark}

package com.wl4g;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wl4g.components.data.annotation.AutoConfigureComponentsDataSource;
import com.wl4g.iam.client.annotation.EnableIamClient;

@EnableIamClient
@MapperScan("${organName?uncap_first}.${projectName?uncap_first}.dao.*")
@AutoConfigureComponentsDataSource
@SpringBootApplication
public class ${projectName?cap_first}Server {

	public static void main(String[] args) {
		SpringApplication.run(${projectName?cap_first}Server.class, args);
	}

}