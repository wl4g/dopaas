package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;

import java.util.function.Function;

import javax.validation.constraints.NotBlank;

import com.wl4g.components.common.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

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
@Wither
public class GenericPropertySource implements ScmPropertySource {
	private static final long serialVersionUID = -5037062685017411482L;

	/** Target file name of the configuration. (like spring.profiles) */
	private String profile;

	/**
	 * Configuration property source format.
	 */
	private String sourceType;

	/** Release configuration plaintext content string. */
	private String content;

	/** Resolved configuration decrypted property source. */
	private transient ScmPropertySource resolvedSource;

	public GenericPropertySource() {
		super();
	}

	public GenericPropertySource(@NotBlank String profile, @NotBlank String sourceType, @NotBlank String content,
			@Nullable @Deprecated ScmPropertySource ignore0) {
		this.profile = profile;
		this.sourceType = sourceType;
		this.content = content;
		validate();
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
		hasTextOf(content, "content");
	}

	@Override
	public boolean isResolved() {
		return nonNull(resolvedSource);
	}

	@Override
	public ScmPropertySource resolveCipher(Function<String, Object> cipherResolver) {
		return (this.resolvedSource = doResolved(cipherResolver));
	}

	/**
	 * Do resolving property source.
	 * 
	 * @param cipherResolver
	 * @return
	 */
	protected ScmPropertySource doResolved(Function<String, Object> cipherResolver) {
		throw new UnsupportedOperationException();
	}

}
