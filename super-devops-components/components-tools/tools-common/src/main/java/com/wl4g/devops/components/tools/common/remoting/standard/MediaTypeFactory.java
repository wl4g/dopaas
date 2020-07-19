/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.components.tools.common.remoting.standard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.google.common.io.Resources;
import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.devops.components.tools.common.collection.multimap.MultiValueMap;
import com.wl4g.devops.components.tools.common.lang.StringUtils2;
import com.wl4g.devops.components.tools.common.resource.StreamResource;

/**
 * A factory delegate for resolving {@link HttpMediaType} objects from
 * {@link StreamResource} handles or filenames.
 *
 */
public final class MediaTypeFactory {

	private MediaTypeFactory() {
	}

	/**
	 * Parse the {@code mime.types} file found in the resources. Format is:
	 * <code>
	 * # comments begin with a '#'<br>
	 * # the format is &lt;mime type> &lt;space separated file extensions><br>
	 * # for example:<br>
	 * text/plain    txt text<br>
	 * # this would map file.txt and file.text to<br>
	 * # the mime type "text/plain"<br>
	 * </code>
	 * 
	 * @return a multi-value map, mapping media types to file extensions.
	 */
	private static MultiValueMap<String, HttpMediaType> parseMimeTypes() {
		InputStream is;
		try {
			is = Resources.getResource(defaultMimeTypesFileName).openStream();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
			MultiValueMap<String, HttpMediaType> result = new LinkedMultiValueMap<>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#') {
					continue;
				}
				String[] tokens = StringUtils2.tokenizeToStringArray(line, " \t\n\r\f");
				HttpMediaType mediaType = HttpMediaType.parseMediaType(tokens[0]);
				for (int i = 1; i < tokens.length; i++) {
					String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
					result.add(fileExtension, mediaType);
				}
			}
			return result;
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load '" + defaultMimeTypesFileName + "'", ex);
		}
	}

	/**
	 * Determine a media type for the given resource, if possible.
	 * 
	 * @param resource
	 *            the resource to introspect
	 * @return the corresponding media type, or {@code null} if none found
	 */
	public static Optional<HttpMediaType> getMediaType(@Nullable StreamResource resource) {
		return Optional.ofNullable(resource).map(StreamResource::getFilename).flatMap(MediaTypeFactory::getMediaType);
	}

	/**
	 * Determine a media type for the given file name, if possible.
	 * 
	 * @param filename
	 *            the file name plus extension
	 * @return the corresponding media type, or {@code null} if none found
	 */
	public static Optional<HttpMediaType> getMediaType(@Nullable String filename) {
		return getMediaTypes(filename).stream().findFirst();
	}

	/**
	 * Determine the media types for the given file name, if possible.
	 * 
	 * @param filename
	 *            the file name plus extension
	 * @return the corresponding media types, or an empty list if none found
	 */
	public static List<HttpMediaType> getMediaTypes(@Nullable String filename) {
		return Optional.ofNullable(StringUtils2.getFilenameExtension(filename)).map(s -> s.toLowerCase(Locale.ENGLISH))
				.map(fileExtensionToMediaTypes::get).orElse(Collections.emptyList());
	}

	// e.g: com/wl4g/devops/components/tools/common/remoting/mime.types
	private static final String defaultMimeTypesFileName = MediaTypeFactory.class.getName().replace(".", "/")
			.replace(MediaTypeFactory.class.getSimpleName(), "").concat("mime.types");

	private static final MultiValueMap<String, HttpMediaType> fileExtensionToMediaTypes = parseMimeTypes();

}