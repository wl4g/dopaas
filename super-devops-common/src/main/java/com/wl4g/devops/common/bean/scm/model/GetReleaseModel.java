package com.wl4g.devops.common.bean.scm.model;

import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class GetReleaseModel extends BaseModel {
	final private static long serialVersionUID = -4016863811283064989L;

	private ReleaseInstance instance = new ReleaseInstance();

	public GetReleaseModel() {
		super();
	}

	public GetReleaseModel(String application, String profile, ReleaseMeta releaseMeta, ReleaseInstance instance) {
		super(application, profile, releaseMeta);
		this.setInstance(instance);
	}

	public GetReleaseModel(String application, String profile, ReleaseInstance instance) {
		super(application, profile, null);
		this.setInstance(instance);
	}

	public ReleaseInstance getInstance() {
		return instance;
	}

	public void setInstance(ReleaseInstance instance) {
		if (instance != null) {
			this.instance = instance;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	@Override
	public void validation(boolean validVersion, boolean validReleaseId) {
		super.validation(validVersion, validReleaseId);
		Assert.notNull(getInstance(), "`releaseInstance` is not allowed to be null.");
		getInstance().validation();
	}

}
