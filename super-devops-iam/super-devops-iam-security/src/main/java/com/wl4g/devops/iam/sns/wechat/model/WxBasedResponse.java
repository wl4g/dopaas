package com.wl4g.devops.iam.sns.wechat.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;

/**
 * WeChat based response message model
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月19日
 * @since
 */
public abstract class WxBasedResponse implements Serializable {

	private static final long serialVersionUID = -7472798322235907609L;

	@JsonProperty("errcode")
	private Integer errcode = 0;

	@JsonProperty("errmsg")
	private String errmsg = "ok";

	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}
