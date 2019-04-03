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
