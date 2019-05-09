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
package org.springframework.social.google.api.plus.moments;

import static org.springframework.social.google.api.plus.moments.MomentTypes.LISTEN_ACTIVITY;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Activity representing listening to a song, audio book, or other type of audio
 * recording
 *
 * @see <a href=
 *      "https://developers.google.com/+/api/moment-types/listen-activity">Listen
 *      Activity</a>
 * @author Gabriel Axel
 *
 */
@JsonTypeName(LISTEN_ACTIVITY)
public class ListenActivity extends Moment {

	protected ListenActivity() {
	}

	public ListenActivity(final String targetUrl) {
		super(targetUrl);
	}
}
