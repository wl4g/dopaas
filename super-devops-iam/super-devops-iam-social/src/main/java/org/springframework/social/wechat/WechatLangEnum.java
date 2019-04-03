package org.springframework.social.wechat;

public enum WechatLangEnum {

	ZH_CN("zh-CN"), EN("en");

	private String value;

	private WechatLangEnum(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

}
