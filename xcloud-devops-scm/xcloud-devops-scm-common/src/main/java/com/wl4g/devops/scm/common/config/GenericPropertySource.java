package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;

import java.util.function.Function;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo.ReleaseConfigSource;

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

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Configuration release source.
	 */
	private ReleaseConfigSource release;

	/** Resolved configuration decrypted property source. */
	private transient ScmPropertySource resolvedSource;

	public GenericPropertySource() {
		super();
	}

	public GenericPropertySource(ReleaseConfigSource release, ScmPropertySource resolvedSource) {
		this.release = release;
		this.resolvedSource = resolvedSource;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	/**
	 * Validation
	 */
	public void validate() {
		notNullOf(getRelease(), "release");
		getRelease().validate();
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
