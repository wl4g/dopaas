package com.wl4g.devops.components.tools.common.http.parse;

import java.io.IOException;
import java.lang.reflect.Type;

import com.wl4g.devops.components.tools.common.http.HttpMediaType;

/**
 * A specialization of {@link HttpMessageParser} that can convert an HTTP
 * request into a target object of a specified generic type and a source object
 * of a specified generic type into an HTTP response.
 */
public interface GenericHttpMessageParser<T> extends HttpMessageParser<T> {

	/**
	 * Indicates whether the given type can be read by this converter. This
	 * method should perform the same checks than
	 * {@link HttpMessageParser#canRead(Class, HttpMediaType)} with additional
	 * ones related to the generic type.
	 * 
	 * @param type
	 *            the (potentially generic) type to test for readability
	 * @param contextClass
	 *            a context class for the target type, for example a class in
	 *            which the target type appears in a method signature (can be
	 *            {@code null})
	 * @param mediaType
	 *            the media type to read, can be {@code null} if not specified.
	 *            Typically the value of a {@code Content-Type} header.
	 * @return {@code true} if readable; {@code false} otherwise
	 */
	boolean canRead(Type type, Class<?> contextClass, HttpMediaType mediaType);

	/**
	 * Read an object of the given type form the given input message, and
	 * returns it.
	 * 
	 * @param type
	 *            the (potentially generic) type of object to return. This type
	 *            must have previously been passed to the {@link #canRead
	 *            canRead} method of this interface, which must have returned
	 *            {@code true}.
	 * @param contextClass
	 *            a context class for the target type, for example a class in
	 *            which the target type appears in a method signature (can be
	 *            {@code null})
	 * @param inputMessage
	 *            the HTTP input message to read from
	 * @return the converted object
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws HttpMessageNotReadableException
	 *             in case of conversion errors
	 */
	T read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;

	/**
	 * Indicates whether the given class can be written by this converter.
	 * <p>
	 * This method should perform the same checks than
	 * {@link HttpMessageParser#canWrite(Class, HttpMediaType)} with additional
	 * ones related to the generic type.
	 * 
	 * @param type
	 *            the (potentially generic) type to test for writability (can be
	 *            {@code null} if not specified)
	 * @param clazz
	 *            the source object class to test for writability
	 * @param mediaType
	 *            the media type to write (can be {@code null} if not
	 *            specified); typically the value of an {@code Accept} header.
	 * @return {@code true} if writable; {@code false} otherwise
	 * @since 4.2
	 */
	boolean canWrite(Type type, Class<?> clazz, HttpMediaType mediaType);

	/**
	 * Write an given object to the given output message.
	 * 
	 * @param t
	 *            the object to write to the output message. The type of this
	 *            object must have previously been passed to the
	 *            {@link #canWrite canWrite} method of this interface, which
	 *            must have returned {@code true}.
	 * @param type
	 *            the (potentially generic) type of object to write. This type
	 *            must have previously been passed to the {@link #canWrite
	 *            canWrite} method of this interface, which must have returned
	 *            {@code true}. Can be {@code null} if not specified.
	 * @param contentType
	 *            the content type to use when writing. May be {@code null} to
	 *            indicate that the default content type of the converter must
	 *            be used. If not {@code null}, this media type must have
	 *            previously been passed to the {@link #canWrite canWrite}
	 *            method of this interface, which must have returned
	 *            {@code true}.
	 * @param outputMessage
	 *            the message to write to
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws HttpMessageNotWritableException
	 *             in case of conversion errors
	 * @since 4.2
	 */
	void write(T t, Type type, HttpMediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}
