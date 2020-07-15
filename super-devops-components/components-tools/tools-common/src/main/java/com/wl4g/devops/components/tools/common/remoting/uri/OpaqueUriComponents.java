package com.wl4g.devops.components.tools.common.remoting.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.devops.components.tools.common.collection.multimap.MultiValueMap;
import com.wl4g.devops.components.tools.common.lang.ObjectUtils;

/**
 * Extension of {@link UriComponents} for opaque URIs.
 *
 * @see <a href="http://tools.ietf.org/html/rfc3986#section-1.2.3">Hierarchical
 *      vs Opaque URIs</a>
 */
@SuppressWarnings("serial")
final class OpaqueUriComponents extends UriComponents {

	private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap<>();

	@Nullable
	private final String ssp;

	OpaqueUriComponents(@Nullable String scheme, @Nullable String schemeSpecificPart, @Nullable String fragment) {
		super(scheme, fragment);
		this.ssp = schemeSpecificPart;
	}

	@Override
	@Nullable
	public String getSchemeSpecificPart() {
		return this.ssp;
	}

	@Override
	@Nullable
	public String getUserInfo() {
		return null;
	}

	@Override
	@Nullable
	public String getHost() {
		return null;
	}

	@Override
	public int getPort() {
		return -1;
	}

	@Override
	@Nullable
	public String getPath() {
		return null;
	}

	@Override
	public List<String> getPathSegments() {
		return Collections.emptyList();
	}

	@Override
	@Nullable
	public String getQuery() {
		return null;
	}

	@Override
	public MultiValueMap<String, String> getQueryParams() {
		return QUERY_PARAMS_NONE;
	}

	@Override
	public UriComponents encode(Charset charset) {
		return this;
	}

	@Override
	protected UriComponents expandInternal(UriTemplateVariables uriVariables) {
		String expandedScheme = expandUriComponent(getScheme(), uriVariables);
		String expandedSsp = expandUriComponent(getSchemeSpecificPart(), uriVariables);
		String expandedFragment = expandUriComponent(getFragment(), uriVariables);
		return new OpaqueUriComponents(expandedScheme, expandedSsp, expandedFragment);
	}

	@Override
	public UriComponents normalize() {
		return this;
	}

	@Override
	public String toUriString() {
		StringBuilder uriBuilder = new StringBuilder();

		if (getScheme() != null) {
			uriBuilder.append(getScheme());
			uriBuilder.append(':');
		}
		if (this.ssp != null) {
			uriBuilder.append(this.ssp);
		}
		if (getFragment() != null) {
			uriBuilder.append('#');
			uriBuilder.append(getFragment());
		}

		return uriBuilder.toString();
	}

	@Override
	public URI toUri() {
		try {
			return new URI(getScheme(), this.ssp, getFragment());
		} catch (URISyntaxException ex) {
			throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
		}
	}

	@Override
	protected void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
		if (getScheme() != null) {
			builder.scheme(getScheme());
		}
		if (getSchemeSpecificPart() != null) {
			builder.schemeSpecificPart(getSchemeSpecificPart());
		}
		if (getFragment() != null) {
			builder.fragment(getFragment());
		}
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OpaqueUriComponents)) {
			return false;
		}
		OpaqueUriComponents otherComp = (OpaqueUriComponents) other;
		return (ObjectUtils.nullSafeEquals(getScheme(), otherComp.getScheme())
				&& ObjectUtils.nullSafeEquals(this.ssp, otherComp.ssp)
				&& ObjectUtils.nullSafeEquals(getFragment(), otherComp.getFragment()));
	}

	@Override
	public int hashCode() {
		int result = ObjectUtils.nullSafeHashCode(getScheme());
		result = 31 * result + ObjectUtils.nullSafeHashCode(this.ssp);
		result = 31 * result + ObjectUtils.nullSafeHashCode(getFragment());
		return result;
	}

}
