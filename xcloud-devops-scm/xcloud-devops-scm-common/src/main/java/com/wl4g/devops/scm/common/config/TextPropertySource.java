package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isInstanceOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.util.function.Function;

import com.wl4g.devops.scm.common.exception.UnresolvedPropertySourceException;

/**
 * Origin plaintext property source.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
public class TextPropertySource<T extends ScmPropertySource<?>> extends AbstractPropertySource<T> {
	private static final long serialVersionUID = -5037062685017411482L;

	/** Release configuration profile.(like spring.profiles) */
	private String profile;

	/** Release configuration plaintext content string. */
	private String content;

	public TextPropertySource() {
		super();
	}

	public TextPropertySource(String profile, String content) {
		hasTextOf(profile, "profile");
		hasTextOf(content, "content");
		this.profile = profile;
		this.content = content;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
		hasTextOf(content, "content");
	}

	@SuppressWarnings("unchecked")
	@Override
	public T resolveCipher(Function<String, Object> resolveFunction) {
		Object result = resolveFunction.apply(content);
		isInstanceOf(String.class, result, "Cannot accept resolved configuration value class type");
		this.resolvedSource = (String) result;
		return (T) this;
	}

	@Override
	public T getResolvedSource() throws UnresolvedPropertySourceException {
		return resolvedSource;
	}

}
