/**
 * Copyright 2011-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.google.api.calendar.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.social.google.api.calendar.UriQueryBuilder;
import org.springframework.social.google.api.query.impl.QueryBuilderImpl;

/**
 * {@link UriQueryBuilder} implementation.
 *
 * @author Martin Wink
 */
public abstract class UriQueryBuilderImpl<Q extends UriQueryBuilder<?, T>, T> extends QueryBuilderImpl<Q, T>
		implements UriQueryBuilder<Q, T> {

	public UriQueryBuilderImpl(final String urlTemplate) {
		super(urlTemplate);
	}

	/**
	 * Used to create a URI from the given URL template.
	 *
	 * @return The URI
	 */
	public URI buildUri() {
		try {
			return new URI(super.build());
		} catch (final URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
