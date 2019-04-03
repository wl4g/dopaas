package com.zrk.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="u_customer")
public class Customer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	protected Long id;

	@Column(length=15)
	private String tel;				//手机号
	
	@Column(length=50)
	private String email;			//电子邮箱
	
	@Column(length=30)
	private String username;		//用户名
	
	@Column(length=50)
	private String pwd;				//密码
	
	@Column(length=30)
	private String nickName;		//昵称
	
	@Column(length=200)
	private String headImg;			//用户头像
	
	private String sinaOpenid;		//新浪微博openid
	
	private String qqOpenid;		//qq openid
	
	private String weixinOpenid;	//微信 openid
	
	private Boolean useable;		//是否
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
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

	public String getSinaOpenid() {
		return sinaOpenid;
	}

	public void setSinaOpenid(String sinaOpenid) {
		this.sinaOpenid = sinaOpenid;
	}

	public String getQqOpenid() {
		return qqOpenid;
	}

	public void setQqOpenid(String qqOpenid) {
		this.qqOpenid = qqOpenid;
	}

	public String getWeixinOpenid() {
		return weixinOpenid;
	}

	public void setWeixinOpenid(String weixinOpenid) {
		this.weixinOpenid = weixinOpenid;
	}

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

	public Boolean getUseable() {
		return useable;
	}

	public void setUseable(Boolean useable) {
		this.useable = useable;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", tel=" + tel + ", email=" + email + ", username=" + username + ", pwd=" + pwd
				+ ", nickName=" + nickName + ", headImg=" + headImg + ", sinaOpenid=" + sinaOpenid + ", qqOpenid="
				+ qqOpenid + ", weixinOpenid=" + weixinOpenid + ", useable=" + useable + "]";
	}
	
	
	
}
