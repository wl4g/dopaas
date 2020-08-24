package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;

/**
 * Abstract origin property source.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
public abstract class AbstractPropertySource implements ScmPropertySource {
	private static final long serialVersionUID = -5037062685017411482L;

	/** Target file name of the configuration. (like spring.profiles) */
	private String profile;

	/**
	 * Configuration property source format
	 */
	private String sourceType;

	/** Release configuration plaintext content string. */
	private String content;

	/** Resolved configuration decrypted property source. */
	private transient ScmPropertySource resolvedSource;

	public AbstractPropertySource() {
		super();
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		hasTextOf(profile, "profile");
		this.profile = profile;
	}

	public AbstractPropertySource withProfile(String profile) {
		setProfile(profile);
		return this;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		hasTextOf(sourceType, "sourceType");
		this.sourceType = sourceType;
	}

	public AbstractPropertySource withSourceType(String sourceType) {
		setSourceType(sourceType);
		return this;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public AbstractPropertySource withContent(String content) {
		setContent(content);
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	/**
	 * Validation
	 */
	public void validate() {
		hasTextOf(profile, "profile");
		hasTextOf(sourceType, "sourceType");
		// hasTextOf(content, "content");
	}

	@Override
	public boolean isResolved() {
		return nonNull(resolvedSource);
	}

	/**
	 * Save resolved property source.
	 * 
	 * @param resolvedSource
	 * @return
	 */
	protected ScmPropertySource saveResolved(ScmPropertySource resolvedSource) {
		notNullOf(resolvedSource, "resolveSource");
		this.resolvedSource = resolvedSource;
		return resolvedSource;
	}

}
