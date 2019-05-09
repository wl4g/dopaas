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
package com.zrk.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zrk.demo.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
	
	Customer findTopByUsernameAndUseable(String username,boolean useable);
	
	Customer findTopByQqOpenidAndUseable(String openid, boolean useable);
	
	Customer findTopBySinaOpenidAndUseable(String openid, boolean useable);
	
	Customer findTopByWeixinOpenidAndUseable(String openid, boolean useable);

}