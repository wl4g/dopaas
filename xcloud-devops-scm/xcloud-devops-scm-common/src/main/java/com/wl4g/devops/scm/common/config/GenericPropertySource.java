package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import javax.validation.constraints.NotNull;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic origin base property source.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
public abstract class GenericPropertySource implements ScmPropertySource {
	private static final long serialVersionUID = -5037062685017411482L;

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Configuration files. (like spring.profiles)
	 */
	@NotNull
	private ConfigProfile profile;

	public GenericPropertySource() {
		super();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	@Override
	public void read(ConfigProfile profile, String sourceContent) {
		log.debug("Resolving release cipher configuration source ...");
		setProfile(profile);
	}

	/**
	 * DO read & resolving property source.
	 * 
	 * @param profile
	 * @param sourceContent
	 */
	protected abstract void doRead(ConfigProfile profile, String sourceContent);

}
