package com.zrk.oauthclient.profile;

import java.util.Map;

/**
 * 客户端接口类
 * 
 * @author Administrator
 *
 */
public interface ClientProfile {

	public String getId();

	public void setId(final Object id);

	public String getOpenid();

	public String getNickname();

	public Integer getSex();

	public String getIcon();

	public void addAttribute(final String key, Object value);

	public Map<String, Object> getAttributes();

	public Object getAttribute(final String name);

}
