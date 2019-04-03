package com.wl4g.devops.common.bean.scm;

/**
 * 对应表：cf_release_history
 * 
 * @author zzh
 * @Description: TODO
 * @date 2018年9月26日
 */
public class Version extends BaseBean {

	private String sign; // 签名字符串
	private String signtype; // 签名类型
	private Integer tag; // 版本标记

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSigntype() {
		return signtype;
	}

	public void setSigntype(String signtype) {
		this.signtype = signtype;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}
}
