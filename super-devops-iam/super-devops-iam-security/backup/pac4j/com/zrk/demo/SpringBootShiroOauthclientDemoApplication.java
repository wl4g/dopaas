/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zrk.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.zrk.demo.entity.Customer;
import com.zrk.demo.repository.CustomerRepository;

@SpringBootApplication
public class SpringBootShiroOauthclientDemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootShiroOauthclientDemoApplication.class, args);
		CustomerRepository customerRepository = context.getBean(CustomerRepository.class);  
	    // 内存数据库操作  
		Customer customer = new Customer();
		customer.setUsername("admin");
		customer.setPwd("admin");
		customer.setNickName("zrk1000");
		customer.setTel("18888888888");
		customer.setEmail("zrk1000@163.com");
		customer.setHeadImg("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=266158537,114847377&fm=116&gp=0.jpg");
		customer.setUseable(true);
		customerRepository.save(customer);  
		customerRepository.findAll().stream().forEach(System.out::println);
		
	}
}