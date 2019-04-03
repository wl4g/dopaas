package com.zrk.demo.shiro;

import java.io.Serializable;
/**
 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
 * @author zrk  
 * @date 2016年5月8日 下午11:59:03
 */
public class ShiroUser implements Serializable {
	private static final long serialVersionUID = 1L;
	public Long id;
	public String tel;
	public String username;
	public String nickName;
	public String headImg;
	private String email;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ShiroUser [id=" + id + ", tel=" + tel + ", nickName=" + nickName + ", headImg=" + headImg + ", email="
				+ email + "]";
	}

}
