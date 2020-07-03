package com.wl4g.devops.components.tools.common.http.parse;

import static com.google.common.base.Charsets.UTF_8;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wl4g.devops.components.tools.common.http.HttpMediaType;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.reflect.ResolvableType;

import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * Abstract base class for Jackson based and content type independent
 * {@link HttpMessageParser} implementations.
 *
 * <p>
 * Compatible with Jackson 2.6 and higher, as of Spring 4.3.
 */
public abstract class AbstractJackson2HttpMessageParser extends AbstractGenericHttpMessageParser<Object> {

	protected ObjectMapper objectMapper;
	private Boolean prettyPrint;
	private PrettyPrinter ssePrettyPrinter;

	protected AbstractJackson2HttpMessageParser(ObjectMapper objectMapper) {
		init(objectMapper);
	}

	protected AbstractJackson2HttpMessageParser(ObjectMapper objectMapper, HttpMediaType supportedMediaType) {
		super(supportedMediaType);
		init(objectMapper);
	}

	protected AbstractJackson2HttpMessageParser(ObjectMapper objectMapper, HttpMediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
		init(objectMapper);
	}

	protected void init(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		setDefaultCharset(UTF_8);
		DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
		prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
		this.ssePrettyPrinter = prettyPrinter;
	}

	/**
	 * Set the {@code ObjectMapper} for this view. If not set, a default
	 * {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
	 * <p>
	 * Setting a custom-configured {@code ObjectMapper} is one way to take
	 * further control of the JSON serialization process. For example, an
	 * extended {@link com.fasterxml.jackson.databind.ser.SerializerFactory} can
	 * be configured that provides custom serializers for specific types. The
	 * other option for refining the serialization process is to use Jackson's
	 * provided annotations on the types to be serialized, in which case a
	 * custom-configured ObjectMapper is unnecessary.
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		Assert2.notNull(objectMapper, "ObjectMapper must not be null");
		this.objectMapper = objectMapper;
		configurePrettyPrint();
	}

	/**
	 * Return the underlying {@code ObjectMapper} for this view.
	 */
	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}

	/**
	 * Whether to use the {@link DefaultPrettyPrinter} when writing JSON. This
	 * is a shortcut for setting up an {@code ObjectMapper} as follows:
	 * 
	 * <pre class="code">
	 * ObjectMapper mapper = new ObjectMapper();
	 * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	 * converter.setObjectMapper(mapper);
	 * </pre>
	 */
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
		configurePrettyPrint();
	}

	private void configurePrettyPrint() {
		if (this.prettyPrint != null) {
			this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
		}
	}

	@Override
	public boolean canRead(Class<?> clazz, HttpMediaType mediaType) {
		return canRead(clazz, null, mediaType);
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, HttpMediaType mediaType) {
		if (!canRead(mediaType)) {
			return false;
		}
		JavaType javaType = getJavaType(type, contextClass);
		AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
		if (this.objectMapper.canDeserialize(javaType, causeRef)) {
			return true;
		}
		logWarningIfNecessary(javaType, causeRef.get());
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, HttpMediaType mediaType) {
		if (!canWrite(mediaType)) {
			return false;
		}
		AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
		if (this.objectMapper.canSerialize(clazz, causeRef)) {
			return true;
		}
		logWarningIfNecessary(clazz, causeRef.get());
		return false;
	}

	/**
	 * Determine whether to log the given exception coming from a
	 * {@link ObjectMapper#canDeserialize} / {@link ObjectMapper#canSerialize}
	 * check.
	 * 
	 * @param type
	 *            the class that Jackson tested for (de-)serializability
	 * @param cause
	 *            the Jackson-thrown exception to evaluate (typically a
	 *            {@link JsonMappingException})
	 * @since 4.3
	 */
	protected void logWarningIfNecessary(Type type, Throwable cause) {
		if (cause == null) {
			return;
		}

		boolean debugLevel = (cause instanceof JsonMappingException && cause.getMessage().startsWith("Can not find"));

		if (debugLevel ? logger.isDebugEnabled() : logger.isWarnEnabled()) {
			String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type ["
					+ type + "]";
			if (debugLevel) {
				logger.debug(msg, cause);
			} else if (logger.isDebugEnabled()) {
				logger.warn(msg, cause);
			} else {
				logger.warn(msg + ": " + cause);
			}
		}
	}

	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		JavaType javaType = getJavaType(clazz, null);
		return readJavaType(javaType, inputMessage);
	}

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		JavaType javaType = getJavaType(type, contextClass);
		return readJavaType(javaType, inputMessage);
	}

	private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
		try {
			if (inputMessage instanceof MappingJacksonInputMessage) {
				Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
				if (deserializationView != null) {
					return this.objectMapper.readerWithView(deserializationView).forType(javaType)
							.readValue(inputMessage.getBody());
				}
			}
			return this.objectMapper.readValue(inputMessage.getBody(), javaType);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex);
		} catch (IOException ex) {
			throw new HttpMessageNotReadableException("I/O error while reading input message", ex);
		}
	}

	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		HttpMediaType contentType = outputMessage.getHeaders().getContentType();
		JsonEncoding encoding = getJsonEncoding(contentType);
		JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
		try {
			writePrefix(generator, object);

			Class<?> serializationView = null;
			FilterProvider filters = null;
			Object value = object;
			JavaType javaType = null;
			if (object instanceof MappingJacksonValue) {
				MappingJacksonValue container = (MappingJacksonValue) object;
				value = container.getValue();
				serializationView = container.getSerializationView();
				filters = container.getFilters();
			}
			if (type != null && value != null && TypeUtils.isAssignable(type, value.getClass())) {
				javaType = getJavaType(type, null);
			}
			ObjectWriter objectWriter;
			if (serializationView != null) {
				objectWriter = this.objectMapper.writerWithView(serializationView);
			} else if (filters != null) {
				objectWriter = this.objectMapper.writer(filters);
			} else {
				objectWriter = this.objectMapper.writer();
			}
			if (javaType != null && javaType.isContainerType()) {
				objectWriter = objectWriter.forType(javaType);
			}
			SerializationConfig config = objectWriter.getConfig();
			if (contentType != null && contentType.isCompatibleWith(HttpMediaType.TEXT_EVENT_STREAM)
					&& config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
				objectWriter = objectWriter.with(this.ssePrettyPrinter);
			}
			objectWriter.writeValue(generator, value);

			writeSuffix(generator, object);
			generator.flush();

		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
		}
	}

	/**
	 * Write a prefix before the main content.
	 * 
	 * @param generator
	 *            the generator to use for writing content.
	 * @param object
	 *            the object to write to the output message.
	 */
	protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
	}

	/**
	 * Write a suffix after the main content.
	 * 
	 * @param generator
	 *            the generator to use for writing content.
	 * @param object
	 *            the object to write to the output message.
	 */
	protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
	}

	/**
	 * Return the Jackson {@link JavaType} for the specified type and context
	 * class.
	 * <p>
	 * The default implementation returns
	 * {@code typeFactory.constructType(type, contextClass)}, but this can be
	 * overridden in subclasses, to allow for custom generic collection
	 * handling. For instance:
	 * 
	 * <pre class="code">
	 * protected JavaType getJavaType(Type type) {
	 * 	if (type instanceof Class && List.class.isAssignableFrom((Class) type)) {
	 * 		return TypeFactory.collectionType(ArrayList.class, MyBean.class);
	 * 	} else {
	 * 		return super.getJavaType(type);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param type
	 *            the generic type to return the Jackson JavaType for
	 * @param contextClass
	 *            a context class for the target type, for example a class in
	 *            which the target type appears in a method signature (can be
	 *            {@code null})
	 * @return the Jackson JavaType
	 */
	protected JavaType getJavaType(Type type, Class<?> contextClass) {
		TypeFactory typeFactory = this.objectMapper.getTypeFactory();
		if (contextClass != null) {
			ResolvableType resolvedType = ResolvableType.forType(type);
			if (type instanceof TypeVariable) {
				ResolvableType resolvedTypeVariable = resolveVariable((TypeVariable<?>) type,
						ResolvableType.forClass(contextClass));
				if (resolvedTypeVariable != ResolvableType.NONE) {
					return typeFactory.constructType(resolvedTypeVariable.resolve());
				}
			} else if (type instanceof ParameterizedType && resolvedType.hasUnresolvableGenerics()) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?>[] generics = new Class<?>[parameterizedType.getActualTypeArguments().length];
				Type[] typeArguments = parameterizedType.getActualTypeArguments();
				for (int i = 0; i < typeArguments.length; i++) {
					Type typeArgument = typeArguments[i];
					if (typeArgument instanceof TypeVariable) {
						ResolvableType resolvedTypeArgument = resolveVariable((TypeVariable<?>) typeArgument,
								ResolvableType.forClass(contextClass));
						if (resolvedTypeArgument != ResolvableType.NONE) {
							generics[i] = resolvedTypeArgument.resolve();
						} else {
							generics[i] = ResolvableType.forType(typeArgument).resolve();
						}
					} else {
						generics[i] = ResolvableType.forType(typeArgument).resolve();
					}
				}
				return typeFactory
						.constructType(ResolvableType.forClassWithGenerics(resolvedType.getRawClass(), generics).getType());
			}
		}
		return typeFactory.constructType(type);
	}

	private ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
		ResolvableType resolvedType;
		if (contextType.hasGenerics()) {
			resolvedType = ResolvableType.forType(typeVariable, contextType);
			if (resolvedType.resolve() != null) {
				return resolvedType;
			}
		}

		ResolvableType superType = contextType.getSuperType();
		if (superType != ResolvableType.NONE) {
			resolvedType = resolveVariable(typeVariable, superType);
			if (resolvedType.resolve() != null) {
				return resolvedType;
			}
		}
		for (ResolvableType ifc : contextType.getInterfaces()) {
			resolvedType = resolveVariable(typeVariable, ifc);
			if (resolvedType.resolve() != null) {
				return resolvedType;
			}
		}
		return ResolvableType.NONE;
	}

	/**
	 * Determine the JSON encoding to use for the given content type.
	 * 
	 * @param contentType
	 *            the media type as requested by the caller
	 * @return the JSON encoding to use (never {@code null})
	 */
	protected JsonEncoding getJsonEncoding(HttpMediaType contentType) {
		if (contentType != null && contentType.getCharset() != null) {
			Charset charset = contentType.getCharset();
			for (JsonEncoding encoding : JsonEncoding.values()) {
				if (charset.name().equals(encoding.getJavaName())) {
					return encoding;
				}
			}
		}
		return JsonEncoding.UTF8;
	}

	@Override
	protected HttpMediaType getDefaultContentType(Object object) throws IOException {
		if (object instanceof MappingJacksonValue) {
			object = ((MappingJacksonValue) object).getValue();
		}
		return super.getDefaultContentType(object);
	}

	@Override
	protected Long getContentLength(Object object, HttpMediaType contentType) throws IOException {
		if (object instanceof MappingJacksonValue) {
			object = ((MappingJacksonValue) object).getValue();
		}
		return super.getContentLength(object, contentType);
	}

}
