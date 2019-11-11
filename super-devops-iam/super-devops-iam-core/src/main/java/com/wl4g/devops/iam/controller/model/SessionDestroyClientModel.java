package com.wl4g.devops.iam.controller.model;

import javax.validation.constraints.NotNull;

import com.wl4g.devops.iam.common.web.model.SessionDestroyModel;

/**
 * Session destroy client parameter model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月11日
 * @since
 */
public class SessionDestroyClientModel extends SessionDestroyModel {
	private static final long serialVersionUID = 2579844578836104919L;

	@NotNull
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}