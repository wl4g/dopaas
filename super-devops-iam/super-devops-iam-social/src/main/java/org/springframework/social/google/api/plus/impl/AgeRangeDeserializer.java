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
package org.springframework.social.google.api.plus.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.social.google.api.plus.AgeRange;

/**
 * @author Michal Szwed
 */
public class AgeRangeDeserializer extends JsonDeserializer<AgeRange> {

	@Override
	public AgeRange deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		final JsonNode ageRangeNode = jp.readValueAs(JsonNode.class);
		final JsonNode minNode = ageRangeNode.get("min");
		final JsonNode maxNode = ageRangeNode.get("max");
		final Integer min = minNode != null ? minNode.asInt() : null;
		final Integer max = maxNode != null ? maxNode.asInt() : null;

		return AgeRange.fromMinMax(min, max);
	}
}
