package com.wl4g.devops.shell.bean;

import java.io.Serializable;

import com.wl4g.devops.shell.annotation.PropertyDescription;

public class AdditionResult implements Serializable {

	private static final long serialVersionUID = -3398687888016885699L;

	@PropertyDescription("Addition sum")
	private int sum;

	public AdditionResult() {
		super();
	}

	public AdditionResult(int sum) {
		super();
		this.sum = sum;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	@Override
	public String toString() {
		return "AdditionResult [sum=" + sum + "]";
	}

}
