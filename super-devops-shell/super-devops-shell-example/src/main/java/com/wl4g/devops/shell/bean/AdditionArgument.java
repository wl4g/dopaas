package com.wl4g.devops.shell.bean;

import java.io.Serializable;

import com.wl4g.devops.shell.annotation.ShellOption;

public class AdditionArgument implements Serializable {

	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "a", lopt = "add1", help = "加数")
	private int add1;

	@ShellOption(opt = "b", lopt = "add2", help = "被加数（默认：1）", defaultValue = "1")
	private int add2;

	public AdditionArgument() {
		super();
	}

	public AdditionArgument(int add1, int add2) {
		super();
		this.add1 = add1;
		this.add2 = add2;
	}

	public int getAdd1() {
		return add1;
	}

	public void setAdd1(int add1) {
		this.add1 = add1;
	}

	public int getAdd2() {
		return add2;
	}

	public void setAdd2(int add2) {
		this.add2 = add2;
	}

	@Override
	public String toString() {
		return "AdditionArgument [add1=" + add1 + ", add2=" + add2 + "]";
	}

}