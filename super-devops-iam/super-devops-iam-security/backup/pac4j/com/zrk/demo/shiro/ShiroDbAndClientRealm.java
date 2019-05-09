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
package com.zrk.demo.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.zrk.demo.entity.Customer;
import com.zrk.demo.repository.CustomerRepository;
import com.zrk.demo.utils.JsonUtils;
import com.zrk.oauthclient.profile.ClientProfile;
import com.zrk.oauthclient.profile.QqProfile;
import com.zrk.oauthclient.profile.SinaWeiboProfile;
import com.zrk.oauthclient.profile.WeiXinProfile;
import com.zrk.oauthclient.shiro.support.UsernamePasswordAndClientRealm;
import com.zrk.oauthclient.shiro.support.UsernamePasswordAndClientToken;


/**
 * ShiroDbAndClientRealm 支持 第三方登录回调拦登录  及 用户名密码登录
 * @author zrk
 * @date 2016年11月21日 下午3:59:21
 */
public class ShiroDbAndClientRealm extends UsernamePasswordAndClientRealm{

	@Autowired
	private CustomerRepository customerRepository;
	
	//用户名密码登录认证
	@Override
	protected AuthenticationInfo internalUsernamePasswordGetAuthenticationInfo( AuthenticationToken authenticationToken) {
		UsernamePasswordAndClientToken token = (UsernamePasswordAndClientToken) authenticationToken;
		String username = token.getUsername();
		Customer customer = customerRepository.findTopByUsernameAndUseable(username, true);
		if (customer!=null) {
			ShiroUser shiroUser = new ShiroUser();
			shiroUser.setId(customer.getId());
			shiroUser.setEmail(customer.getEmail());
			shiroUser.setHeadImg(customer.getHeadImg());
			shiroUser.setNickName(customer.getNickName());
			shiroUser.setTel(customer.getTel());
			return new SimpleAuthenticationInfo(JsonUtils.objectToJson(shiroUser),customer.getPwd(),customer.getTel());
		} else {
			 throw new UnknownAccountException(); //用户不存在
		}
	}

	//第三方登录认证数据再处理
	@Override
	protected AuthenticationInfo internalClientGetAuthenticationInfo( CommonProfile profile, Credentials credentials) {
		ClientProfile clientProfile = (ClientProfile)profile;
		Customer customer = null;
		if(clientProfile instanceof QqProfile){
			customer = customerRepository.findTopByQqOpenidAndUseable(clientProfile.getOpenid(), true);
		}else if(clientProfile instanceof WeiXinProfile){
			customer = customerRepository.findTopByWeixinOpenidAndUseable(clientProfile.getOpenid(), true);
		}else if(clientProfile instanceof SinaWeiboProfile){
			customer = customerRepository.findTopBySinaOpenidAndUseable(clientProfile.getOpenid(), true);
		}
		//第一次登陆
		if(customer == null){
			customer = new Customer();
			if(clientProfile instanceof QqProfile)
				customer.setQqOpenid(clientProfile.getOpenid());
			if(clientProfile instanceof WeiXinProfile)
				customer.setWeixinOpenid(clientProfile.getOpenid());
			if(clientProfile instanceof SinaWeiboProfile)
				customer.setSinaOpenid(clientProfile.getOpenid());
			customer.setHeadImg(clientProfile.getIcon());
			customer.setNickName(clientProfile.getNickname());
			customer = customerRepository.save(customer);
		}
		
		ShiroUser shiroUser = new ShiroUser();
		shiroUser.setId(customer.getId());
		shiroUser.setEmail(customer.getEmail());
		shiroUser.setHeadImg(customer.getHeadImg());
		shiroUser.setNickName(customer.getNickName());
		shiroUser.setTel(customer.getTel());
		return new SimpleAuthenticationInfo(JsonUtils.objectToJson(shiroUser), credentials,getName());
	}
	

}