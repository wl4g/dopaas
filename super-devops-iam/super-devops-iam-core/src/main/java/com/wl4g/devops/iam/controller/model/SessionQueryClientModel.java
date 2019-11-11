package com.wl4g.devops.iam.controller.model;

import javax.validation.constraints.NotNull;

import com.wl4g.devops.iam.common.web.model.SessionQueryModel;

/**
 * Session query client parameter model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月11日
 * @since
 */
public class SessionQueryClientModel extends SessionQueryModel {
	private static final long serialVersionUID = 2579844578836104919L;

	@NotNull(message = "Iam server id must not be empty")
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}