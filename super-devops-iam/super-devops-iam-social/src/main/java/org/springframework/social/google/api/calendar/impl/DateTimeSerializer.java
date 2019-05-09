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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {@link JsonSerializer} for {@link Date}.
 *
 * @author Martin Wink
 */
public class DateTimeSerializer extends JsonSerializer<Date> {
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);

	public DateTimeSerializer() {
		final TimeZone timeZone = TimeZone.getTimeZone("GMT");
		format.setTimeZone(timeZone);
	}

	@Override
	public void serialize(final Date value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		jgen.writeString(format.format(value));
	}
}
