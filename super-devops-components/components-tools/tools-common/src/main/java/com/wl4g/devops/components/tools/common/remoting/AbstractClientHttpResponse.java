package com.wl4g.devops.components.tools.common.remoting;

import java.io.IOException;

/**
 * Abstract base for {@link ClientHttpResponse}.
 *
 */
public abstract class AbstractClientHttpResponse implements ClientHttpResponse {

	@Override
	public HttpStatus getStatusCode() throws IOException {
		return HttpStatus.valueOf(getRawStatusCode());
	}

}
