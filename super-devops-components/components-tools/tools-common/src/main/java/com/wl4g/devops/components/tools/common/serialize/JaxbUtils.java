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
package com.wl4g.devops.components.tools.common.serialize;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notEmptyOf;
import static java.util.Objects.isNull;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;

/**
 * Using JAXB 2.0 to implement binder of XML <-> Java object, especially support
 * the case that root object is list.
 * 
 * <pre>
 * Step 1: Convert the sample XML data into an XSD definition file. 
 * 	       Recommended online conversion: <a href=
"https://www.freeformatter.com/xsd-generator.html#ad -output">https://www.freeformatter.com/xsd-generator.html#ad -output</a>
 *
 * Step 2: Generate the JAXB class from the XSD definition file. For example, 
 * 		   Use Eclipse: Select XSD file -> Right-click -> Generate -> JAXB classes...
 * 		   Use IDEA: Tools > JAXB > Generate Jaca Code From Xml Schema Using JAXB...
 * </pre>
 * 
 * <p>
 * More Example Tests Refer: {@link JaxbUtilsTests}
 * </p>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-21
 * @since
 */
public abstract class JaxbUtils {

	/**
	 * Java Object -> Xml.
	 */
	public static String toXml(Object root, String encoding, Class<?>... types) {
		return getJaxbConverter(types).toXml(root, encoding);
	}

	/**
	 * Java Object-> Xml, especially supports the case that root element is
	 * collection
	 */
	public static String toXml(Collection<?> root, String rootName, String encoding, Class<?>... types) {
		return getJaxbConverter(types).toXml(root, rootName, encoding);
	}

	/**
	 * Xml->Java Object.
	 * 
	 * @param xml
	 * @return
	 */
	public static <T> T fromXml(String xml, Class<?>... types) {
		return getJaxbConverter(types).fromXml(xml);
	}

	/**
	 * Xml->Java Object, support case sensitive or insensitive
	 * 
	 * @param xml
	 * @param caseSensitive
	 * @return
	 */
	public static <T> T fromXml(String xml, boolean caseSensitive, Class<?>... types) {
		return getJaxbConverter(types).fromXml(xml, caseSensitive);
	}

	/**
	 * Create Marshall and set encoding (nullable)
	 * 
	 * @param encoding
	 * @return
	 */
	public static Marshaller createMarshaller(String encoding, Class<?>... types) {
		return getJaxbConverter(types).createMarshaller(encoding);
	}

	/**
	 * Create unmarshaller
	 * 
	 * @return
	 */
	public static Unmarshaller createUnmarshaller(Class<?>... types) {
		return getJaxbConverter(types).createUnmarshaller();
	}

	/**
	 * Gets {@link JaxbConverter} instance with local cache.
	 * 
	 * @param types
	 * @return
	 */
	private static JaxbConverter getJaxbConverter(Class<?>... types) {
		String cacheKey = getConverterCacheKey(types);

		JaxbConverter converter = converterCache.get(cacheKey);
		if (isNull(converter)) {
			converterCache.put(cacheKey, (converter = new JaxbConverter(types)));
		}

		return converter;
	}

	/**
	 * Gets {@link JaxbConverter} instance with local cache key.
	 * 
	 * @param types
	 * @return
	 */
	private static String getConverterCacheKey(Class<?>... types) {
		notEmptyOf(types, "jaxbTypes");

		StringBuffer key = new StringBuffer(types.length * 30);
		for (Class<?> cls : types) {
			key.append(cls.getName());
			key.append("-");
		}

		return key.toString();
	}

	/**
	 * {@link JaxbConverter}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-07-21
	 * @since
	 */
	final private static class JaxbConverter {

		/**
		 * Multi thread safe context
		 */
		private JAXBContext jaxbContext;

		/**
		 * Intializte {@link JaxbConverter} construct.
		 * 
		 * @param types
		 *            The type of all root objects that need to be serialized
		 */
		public JaxbConverter(Class<?>... types) {
			try {
				jaxbContext = JAXBContext.newInstance(types);
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Java Object -> Xml.
		 */
		public String toXml(Object root, String encoding) {
			try {
				StringWriter writer = new StringWriter();
				createMarshaller(encoding).marshal(root, writer);
				return writer.toString();
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Java Object-> Xml, especially supports the case that root element is
		 * collection
		 */
		public String toXml(Collection<?> root, String rootName, String encoding) {
			try {
				CollectionWrapper wrapper = new CollectionWrapper();
				wrapper.collection = root;

				JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
						CollectionWrapper.class, wrapper);

				StringWriter writer = new StringWriter();
				createMarshaller(encoding).marshal(wrapperElement, writer);

				return writer.toString();
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Xml->Java Object.
		 * 
		 * @param xml
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T> T fromXml(String xml) {
			try {
				StringReader reader = new StringReader(xml);
				return (T) createUnmarshaller().unmarshal(reader);
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Xml->Java Object, support case sensitive or insensitive
		 * 
		 * @param xml
		 * @param caseSensitive
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T> T fromXml(String xml, boolean caseSensitive) {
			try {
				String fromXml = xml;
				if (!caseSensitive)
					fromXml = xml.toLowerCase();
				StringReader reader = new StringReader(fromXml);
				return (T) createUnmarshaller().unmarshal(reader);
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Create Marshall and set encoding (nullable)
		 * 
		 * @param encoding
		 * @return
		 */
		public Marshaller createMarshaller(String encoding) {
			try {
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				if (StringUtils.isNotBlank(encoding)) {
					marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
				}
				return marshaller;
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Create unmarshaller
		 * 
		 * @return
		 */
		public Unmarshaller createUnmarshaller() {
			try {
				return jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Encapsulate the case that the root element is a collection. . *
		 * 
		 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
		 * @version v1.0 2020-07-20
		 * @since
		 */
		class CollectionWrapper {
			@XmlAnyElement
			protected Collection<?> collection;
		}

	}

	/**
	 * {@link JaxbConverter} fast local cache.
	 */
	final private static Map<String, JaxbConverter> converterCache = new ConcurrentHashMap<>();

}
