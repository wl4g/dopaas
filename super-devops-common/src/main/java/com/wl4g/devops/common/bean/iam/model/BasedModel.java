package com.wl4g.devops.common.bean.iam.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.StringUtils2;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class BasedModel implements Serializable {
	private static final long serialVersionUID = 151897009229689455L;

	@NotBlank
	private String application;

	public BasedModel() {
		super();
	}

	public BasedModel(String application) {
		super();
		this.setApplication(application);
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		Assert.notNull(application, "'application' must not be null");
		if (!StringUtils2.isEmpty(application) && !"NULL".equalsIgnoreCase(application)) {
			this.application = application;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}
