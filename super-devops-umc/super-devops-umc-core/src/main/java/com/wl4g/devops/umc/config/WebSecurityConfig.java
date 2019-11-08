/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.umc.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring-boot security config.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年5月28日
 * @since
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements EnvironmentAware {
	public static String DEFAULT_SBACTXPATH = "/sba";

	@Override
	public void setEnvironment(Environment environment) {
		DEFAULT_SBACTXPATH = environment.getProperty("spring.boot.admin.context-path", DEFAULT_SBACTXPATH);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// Ignore css.jq.img and other files.
		web.ignoring().antMatchers(DEFAULT_SBACTXPATH + "/**.html", DEFAULT_SBACTXPATH + "/**.css",
				DEFAULT_SBACTXPATH + "/img/**", DEFAULT_SBACTXPATH + "/**.js", DEFAULT_SBACTXPATH + "/third-party/**");
	}

	/**
	 * https://blog.csdn.net/fjnpysh/article/details/72424935
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable() // The ui currently doesn't support csrf
				.authorizeRequests() // Authorize Request Configuration
				.antMatchers(DEFAULT_SBACTXPATH + "/login", DEFAULT_SBACTXPATH + "/api/**", DEFAULT_SBACTXPATH + "/**/heapdump",
						DEFAULT_SBACTXPATH + "/**/loggers", DEFAULT_SBACTXPATH + "/**/liquibase",
						DEFAULT_SBACTXPATH + "/**/logfile", DEFAULT_SBACTXPATH + "/**/flyway",
						DEFAULT_SBACTXPATH + "/**/auditevents", DEFAULT_SBACTXPATH + "/**/jolokia")
				.permitAll() // 放开"/api/**"：为了给被监控端免登录注册并解决Log与Logger冲突
				.and()
				// Requests for the login page and the static assets are
				// allowed.
				.authorizeRequests().antMatchers(DEFAULT_SBACTXPATH + "/**").hasRole("USER")
				.antMatchers(DEFAULT_SBACTXPATH + "/**").authenticated().and() // Login
																				// Form
																				// configuration
																				// for
																				// all
																				// others
				.formLogin().loginPage(DEFAULT_SBACTXPATH + "/login.html")
				// Page with login form is served as /login.html and does a POST
				// on /login
				.loginProcessingUrl(DEFAULT_SBACTXPATH + "/login").permitAll().defaultSuccessUrl(DEFAULT_SBACTXPATH + "/").and() // The
																																	// UI
																																	// does
																																	// a
																																	// POST
																																	// on
																																	// /logout
																																	// on
																																	// logout
				.logout().logoutUrl(DEFAULT_SBACTXPATH + "/logout").deleteCookies("remove")
				.logoutSuccessUrl(DEFAULT_SBACTXPATH + "/login.html").permitAll().and()
				// Enable so that the clients can authenticate via HTTP basic
				// for registering.
				.httpBasic();
	}

}