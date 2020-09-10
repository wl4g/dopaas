package com.wl4g.devops.dts.codegen.bean;

import com.wl4g.components.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link GenDatabase}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
@Getter
@Setter
public class GenDatabase extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private String name;

	private String type;

	private String host;

	private String port;

	private String database;

	private String username;

	private String password;

	private String url;

}