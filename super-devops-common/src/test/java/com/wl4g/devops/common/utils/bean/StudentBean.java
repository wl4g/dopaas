package com.wl4g.devops.common.utils.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentBean {

	private List<String> listTitle = new ArrayList<String>();
	{
		listTitle.add("Spring");
		listTitle.add("Java");
	}

	private String name;
	private String addr;
	private String sex;
	private int age;
	private Date birthdate;
	private Date createTime;
	private String createUser;
	private Date modifyTime;
	private String modifyUser;
	private String isValidate;
	private String comments;
	private NameBean nameBean;

	public List<String> getListTitle() {
		return listTitle;
	}

	public void setListTitle(List<String> listTitle) {
		this.listTitle = listTitle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getIsValidate() {
		return isValidate;
	}

	public void setIsValidate(String isValidate) {
		this.isValidate = isValidate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public NameBean getNameBean() {
		return nameBean;
	}

	public void setNameBean(NameBean nameBean) {
		this.nameBean = nameBean;
	}

}
