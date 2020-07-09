package com.wl4g.devops.components.tools.common.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.ConcurrentReferenceHashMap;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.lang.ClassUtils2;
import com.wl4g.devops.components.tools.common.lang.ObjectUtils;
import com.wl4g.devops.components.tools.common.lang.StringUtils2;
import com.wl4g.devops.components.tools.common.reflect.ResolvableType.SerializableTypeWrapper.TypeProvider;
import com.wl4g.devops.components.tools.common.reflect.ResolvableType.SerializableTypeWrapper.FieldTypeProvider;
import com.wl4g.devops.components.tools.common.reflect.ResolvableType.SerializableTypeWrapper.MethodParameterTypeProvider;

/**
 * Encapsulates a Java {@link java.lang.reflect.Type}, providing access to
 * {@link #getSuperType() supertypes}, {@link #getInterfaces() interfaces}, and
 * {@link #getGeneric(int...) generic parameters} along with the ability to
 * ultimately {@link #resolve() resolve} to a {@link java.lang.Class}.
 *
 * <p>
 * {@code ResolvableTypes} may be obtained from {@link #forField(Field) fields},
 * {@link #forMethodParameter(Method, int) method parameters},
 * {@link #forMethodReturnType(Method) method returns} or
 * {@link #forClass(Class) classes}. Most methods on this class will themselves
 * return {@link ResolvableType ResolvableTypes}, allowing easy navigation. For
 * example:
 * 
 * <pre class="code">
 * private HashMap&lt;Integer, List&lt;String&gt;&gt; myMap;
 *
 * public void example() {
 * 	ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("myMap"));
 * 	t.getSuperType(); // AbstractMap&lt;Integer, List&lt;String&gt;&gt;
 * 	t.asMap(); // Map&lt;Integer, List&lt;String&gt;&gt;
 * 	t.getGeneric(0).resolve(); // Integer
 * 	t.getGeneric(1).resolve(); // List
 * 	t.getGeneric(1); // List&lt;String&gt;
 * 	t.resolveGeneric(1, 0); // String
 * }
 * </pre>
 *
 * @see #forField(Field)
 * @see #forMethodParameter(Method, int)
 * @see #forMethodReturnType(Method)
 * @see #forConstructorParameter(Constructor, int)
 * @see #forClass(Class)
 * @see #forType(Type)
 * @see #forInstance(Object)
 * @see ResolvableTypeProvider
 */
@SuppressWarnings("serial")
public class ResolvableType implements Serializable {

	/**
	 * {@code ResolvableType} returned when no value is available. {@code NONE}
	 * is used in preference to {@code null} so that multiple method calls can
	 * be safely chained.
	 */
	public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE, null, null, 0);
	private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];
	private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache = new ConcurrentReferenceHashMap<>(256);

	/**
	 * The underlying Java type being managed.
	 */
	private final Type type;

	/**
	 * Optional provider for the type.
	 */
	@Nullable
	private final TypeProvider typeProvider;

	/**
	 * The {@code VariableResolver} to use or {@code null} if no resolver is
	 * available.
	 */
	@Nullable
	private final VariableResolver variableResolver;

	/**
	 * The component type for an array or {@code null} if the type should be
	 * deduced.
	 */
	@Nullable
	private final ResolvableType componentType;

	@Nullable
	private final Integer hash;

	@Nullable
	private Class<?> resolved;

	@Nullable
	private volatile ResolvableType superType;

	@Nullable
	private volatile ResolvableType[] interfaces;

	@Nullable
	private volatile ResolvableType[] generics;

	/**
	 * Private constructor used to create a new {@link ResolvableType} for cache
	 * key purposes, with no upfront resolution.
	 */
	private ResolvableType(Type type, @Nullable TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {

		this.type = type;
		this.typeProvider = typeProvider;
		this.variableResolver = variableResolver;
		this.componentType = null;
		this.hash = calculateHashCode();
		this.resolved = null;
	}

	/**
	 * Private constructor used to create a new {@link ResolvableType} for cache
	 * value purposes, with upfront resolution and a pre-calculated hash.
	 * 
	 * @since 4.2
	 */
	private ResolvableType(Type type, @Nullable TypeProvider typeProvider, @Nullable VariableResolver variableResolver,
			@Nullable Integer hash) {

		this.type = type;
		this.typeProvider = typeProvider;
		this.variableResolver = variableResolver;
		this.componentType = null;
		this.hash = hash;
		this.resolved = resolveClass();
	}

	/**
	 * Private constructor used to create a new {@link ResolvableType} for
	 * uncached purposes, with upfront resolution but lazily calculated hash.
	 */
	private ResolvableType(Type type, @Nullable TypeProvider typeProvider, @Nullable VariableResolver variableResolver,
			@Nullable ResolvableType componentType) {

		this.type = type;
		this.typeProvider = typeProvider;
		this.variableResolver = variableResolver;
		this.componentType = componentType;
		this.hash = null;
		this.resolved = resolveClass();
	}

	/**
	 * Private constructor used to create a new {@link ResolvableType} on a
	 * {@link Class} basis. Avoids all {@code instanceof} checks in order to
	 * create a straight {@link Class} wrapper.
	 * 
	 * @since 4.2
	 */
	private ResolvableType(@Nullable Class<?> clazz) {
		this.resolved = (clazz != null ? clazz : Object.class);
		this.type = this.resolved;
		this.typeProvider = null;
		this.variableResolver = null;
		this.componentType = null;
		this.hash = null;
	}

	/**
	 * Return the underling Java {@link Type} being managed.
	 */
	public Type getType() {
		return SerializableTypeWrapper.unwrap(this.type);
	}

	/**
	 * Return the underlying Java {@link Class} being managed, if available;
	 * otherwise {@code null}.
	 */
	@Nullable
	public Class<?> getRawClass() {
		if (this.type == this.resolved) {
			return this.resolved;
		}
		Type rawType = this.type;
		if (rawType instanceof ParameterizedType) {
			rawType = ((ParameterizedType) rawType).getRawType();
		}
		return (rawType instanceof Class ? (Class<?>) rawType : null);
	}

	/**
	 * Return the underlying source of the resolvable type. Will return a
	 * {@link Field}, {@link MethodParameter} or {@link Type} depending on how
	 * the {@link ResolvableType} was constructed. With the exception of the
	 * {@link #NONE} constant, this method will never return {@code null}. This
	 * method is primarily to provide access to additional type information or
	 * meta-data that alternative JVM languages may provide.
	 */
	public Object getSource() {
		Object source = (this.typeProvider != null ? this.typeProvider.getSource() : null);
		return (source != null ? source : this.type);
	}

	/**
	 * Return this type as a resolved {@code Class}, falling back to
	 * {@link java.lang.Object} if no specific class can be resolved.
	 * 
	 * @return the resolved {@link Class} or the {@code Object} fallback
	 * @since 5.1
	 * @see #getRawClass()
	 * @see #resolve(Class)
	 */
	public Class<?> toClass() {
		return resolve(Object.class);
	}

	/**
	 * Determine whether the given object is an instance of this
	 * {@code ResolvableType}.
	 * 
	 * @param obj
	 *            the object to check
	 * @since 4.2
	 * @see #isAssignableFrom(Class)
	 */
	public boolean isInstance(@Nullable Object obj) {
		return (obj != null && isAssignableFrom(obj.getClass()));
	}

	/**
	 * Determine whether this {@code ResolvableType} is assignable from the
	 * specified other type.
	 * 
	 * @param other
	 *            the type to be checked against (as a {@code Class})
	 * @since 4.2
	 * @see #isAssignableFrom(ResolvableType)
	 */
	public boolean isAssignableFrom(Class<?> other) {
		return isAssignableFrom(forClass(other), null);
	}

	/**
	 * Determine whether this {@code ResolvableType} is assignable from the
	 * specified other type.
	 * <p>
	 * Attempts to follow the same rules as the Java compiler, considering
	 * whether both the {@link #resolve() resolved} {@code Class} is
	 * {@link Class#isAssignableFrom(Class) assignable from} the given type as
	 * well as whether all {@link #getGenerics() generics} are assignable.
	 * 
	 * @param other
	 *            the type to be checked against (as a {@code ResolvableType})
	 * @return {@code true} if the specified other type can be assigned to this
	 *         {@code ResolvableType}; {@code false} otherwise
	 */
	public boolean isAssignableFrom(ResolvableType other) {
		return isAssignableFrom(other, null);
	}

	private boolean isAssignableFrom(ResolvableType other, @Nullable Map<Type, Type> matchedBefore) {
		Assert2.notNull(other, "ResolvableType must not be null");

		// If we cannot resolve types, we are not assignable
		if (this == NONE || other == NONE) {
			return false;
		}

		// Deal with array by delegating to the component type
		if (isArray()) {
			return (other.isArray() && getComponentType().isAssignableFrom(other.getComponentType()));
		}

		if (matchedBefore != null && matchedBefore.get(this.type) == other.type) {
			return true;
		}

		// Deal with wildcard bounds
		WildcardBounds ourBounds = WildcardBounds.get(this);
		WildcardBounds typeBounds = WildcardBounds.get(other);

		// In the form X is assignable to <? extends Number>
		if (typeBounds != null) {
			return (ourBounds != null && ourBounds.isSameKind(typeBounds) && ourBounds.isAssignableFrom(typeBounds.getBounds()));
		}

		// In the form <? extends Number> is assignable to X...
		if (ourBounds != null) {
			return ourBounds.isAssignableFrom(other);
		}

		// Main assignability check about to follow
		boolean exactMatch = (matchedBefore != null); // We're checking nested
														// generic variables
														// now...
		boolean checkGenerics = true;
		Class<?> ourResolved = null;
		if (this.type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			// Try default variable resolution
			if (this.variableResolver != null) {
				ResolvableType resolved = this.variableResolver.resolveVariable(variable);
				if (resolved != null) {
					ourResolved = resolved.resolve();
				}
			}
			if (ourResolved == null) {
				// Try variable resolution against target type
				if (other.variableResolver != null) {
					ResolvableType resolved = other.variableResolver.resolveVariable(variable);
					if (resolved != null) {
						ourResolved = resolved.resolve();
						checkGenerics = false;
					}
				}
			}
			if (ourResolved == null) {
				// Unresolved type variable, potentially nested -> never insist
				// on exact match
				exactMatch = false;
			}
		}
		if (ourResolved == null) {
			ourResolved = resolve(Object.class);
		}
		Class<?> otherResolved = other.toClass();

		// We need an exact type match for generics
		// List<CharSequence> is not assignable from List<String>
		if (exactMatch ? !ourResolved.equals(otherResolved) : !ClassUtils2.isAssignable(ourResolved, otherResolved)) {
			return false;
		}

		if (checkGenerics) {
			// Recursively check each generic
			ResolvableType[] ourGenerics = getGenerics();
			ResolvableType[] typeGenerics = other.as(ourResolved).getGenerics();
			if (ourGenerics.length != typeGenerics.length) {
				return false;
			}
			if (matchedBefore == null) {
				matchedBefore = new IdentityHashMap<>(1);
			}
			matchedBefore.put(this.type, other.type);
			for (int i = 0; i < ourGenerics.length; i++) {
				if (!ourGenerics[i].isAssignableFrom(typeGenerics[i], matchedBefore)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Return {@code true} if this type resolves to a Class that represents an
	 * array.
	 * 
	 * @see #getComponentType()
	 */
	public boolean isArray() {
		if (this == NONE) {
			return false;
		}
		return ((this.type instanceof Class && ((Class<?>) this.type).isArray()) || this.type instanceof GenericArrayType
				|| resolveType().isArray());
	}

	/**
	 * Return the ResolvableType representing the component type of the array or
	 * {@link #NONE} if this type does not represent an array.
	 * 
	 * @see #isArray()
	 */
	public ResolvableType getComponentType() {
		if (this == NONE) {
			return NONE;
		}
		if (this.componentType != null) {
			return this.componentType;
		}
		if (this.type instanceof Class) {
			Class<?> componentType = ((Class<?>) this.type).getComponentType();
			return forType(componentType, this.variableResolver);
		}
		if (this.type instanceof GenericArrayType) {
			return forType(((GenericArrayType) this.type).getGenericComponentType(), this.variableResolver);
		}
		return resolveType().getComponentType();
	}

	/**
	 * Convenience method to return this type as a resolvable {@link Collection}
	 * type. Returns {@link #NONE} if this type does not implement or extend
	 * {@link Collection}.
	 * 
	 * @see #as(Class)
	 * @see #asMap()
	 */
	public ResolvableType asCollection() {
		return as(Collection.class);
	}

	/**
	 * Convenience method to return this type as a resolvable {@link Map} type.
	 * Returns {@link #NONE} if this type does not implement or extend
	 * {@link Map}.
	 * 
	 * @see #as(Class)
	 * @see #asCollection()
	 */
	public ResolvableType asMap() {
		return as(Map.class);
	}

	/**
	 * Return this type as a {@link ResolvableType} of the specified class.
	 * Searches {@link #getSuperType() supertype} and {@link #getInterfaces()
	 * interface} hierarchies to find a match, returning {@link #NONE} if this
	 * type does not implement or extend the specified class.
	 * 
	 * @param type
	 *            the required type (typically narrowed)
	 * @return a {@link ResolvableType} representing this object as the
	 *         specified type, or {@link #NONE} if not resolvable as that type
	 * @see #asCollection()
	 * @see #asMap()
	 * @see #getSuperType()
	 * @see #getInterfaces()
	 */
	public ResolvableType as(Class<?> type) {
		if (this == NONE) {
			return NONE;
		}
		Class<?> resolved = resolve();
		if (resolved == null || resolved == type) {
			return this;
		}
		for (ResolvableType interfaceType : getInterfaces()) {
			ResolvableType interfaceAsType = interfaceType.as(type);
			if (interfaceAsType != NONE) {
				return interfaceAsType;
			}
		}
		return getSuperType().as(type);
	}

	/**
	 * Return a {@link ResolvableType} representing the direct supertype of this
	 * type. If no supertype is available this method returns {@link #NONE}.
	 * <p>
	 * Note: The resulting {@link ResolvableType} instance may not be
	 * {@link Serializable}.
	 * 
	 * @see #getInterfaces()
	 */
	public ResolvableType getSuperType() {
		Class<?> resolved = resolve();
		if (resolved == null || resolved.getGenericSuperclass() == null) {
			return NONE;
		}
		ResolvableType superType = this.superType;
		if (superType == null) {
			superType = forType(resolved.getGenericSuperclass(), this);
			this.superType = superType;
		}
		return superType;
	}

	/**
	 * Return a {@link ResolvableType} array representing the direct interfaces
	 * implemented by this type. If this type does not implement any interfaces
	 * an empty array is returned.
	 * <p>
	 * Note: The resulting {@link ResolvableType} instances may not be
	 * {@link Serializable}.
	 * 
	 * @see #getSuperType()
	 */
	public ResolvableType[] getInterfaces() {
		Class<?> resolved = resolve();
		if (resolved == null) {
			return EMPTY_TYPES_ARRAY;
		}
		ResolvableType[] interfaces = this.interfaces;
		if (interfaces == null) {
			Type[] genericIfcs = resolved.getGenericInterfaces();
			interfaces = new ResolvableType[genericIfcs.length];
			for (int i = 0; i < genericIfcs.length; i++) {
				interfaces[i] = forType(genericIfcs[i], this);
			}
			this.interfaces = interfaces;
		}
		return interfaces;
	}

	/**
	 * Return {@code true} if this type contains generic parameters.
	 * 
	 * @see #getGeneric(int...)
	 * @see #getGenerics()
	 */
	public boolean hasGenerics() {
		return (getGenerics().length > 0);
	}

	/**
	 * Return {@code true} if this type contains unresolvable generics only,
	 * that is, no substitute for any of its declared type variables.
	 */
	boolean isEntirelyUnresolvable() {
		if (this == NONE) {
			return false;
		}
		ResolvableType[] generics = getGenerics();
		for (ResolvableType generic : generics) {
			if (!generic.isUnresolvableTypeVariable() && !generic.isWildcardWithoutBounds()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine whether the underlying type has any unresolvable generics:
	 * either through an unresolvable type variable on the type itself or
	 * through implementing a generic interface in a raw fashion, i.e. without
	 * substituting that interface's type variables. The result will be
	 * {@code true} only in those two scenarios.
	 */
	public boolean hasUnresolvableGenerics() {
		if (this == NONE) {
			return false;
		}
		ResolvableType[] generics = getGenerics();
		for (ResolvableType generic : generics) {
			if (generic.isUnresolvableTypeVariable() || generic.isWildcardWithoutBounds()) {
				return true;
			}
		}
		Class<?> resolved = resolve();
		if (resolved != null) {
			for (Type genericInterface : resolved.getGenericInterfaces()) {
				if (genericInterface instanceof Class) {
					if (forClass((Class<?>) genericInterface).hasGenerics()) {
						return true;
					}
				}
			}
			return getSuperType().hasUnresolvableGenerics();
		}
		return false;
	}

	/**
	 * Determine whether the underlying type is a type variable that cannot be
	 * resolved through the associated variable resolver.
	 */
	private boolean isUnresolvableTypeVariable() {
		if (this.type instanceof TypeVariable) {
			if (this.variableResolver == null) {
				return true;
			}
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			ResolvableType resolved = this.variableResolver.resolveVariable(variable);
			if (resolved == null || resolved.isUnresolvableTypeVariable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether the underlying type represents a wildcard without
	 * specific bounds (i.e., equal to {@code ? extends Object}).
	 */
	private boolean isWildcardWithoutBounds() {
		if (this.type instanceof WildcardType) {
			WildcardType wt = (WildcardType) this.type;
			if (wt.getLowerBounds().length == 0) {
				Type[] upperBounds = wt.getUpperBounds();
				if (upperBounds.length == 0 || (upperBounds.length == 1 && Object.class == upperBounds[0])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return a {@link ResolvableType} for the specified nesting level. See
	 * {@link #getNested(int, Map)} for details.
	 * 
	 * @param nestingLevel
	 *            the nesting level
	 * @return the {@link ResolvableType} type, or {@code #NONE}
	 */
	public ResolvableType getNested(int nestingLevel) {
		return getNested(nestingLevel, null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified nesting level.
	 * <p>
	 * The nesting level refers to the specific generic parameter that should be
	 * returned. A nesting level of 1 indicates this type; 2 indicates the first
	 * nested generic; 3 the second; and so on. For example, given
	 * {@code List<Set<Integer>>} level 1 refers to the {@code List}, level 2
	 * the {@code Set}, and level 3 the {@code Integer}.
	 * <p>
	 * The {@code typeIndexesPerLevel} map can be used to reference a specific
	 * generic for the given level. For example, an index of 0 would refer to a
	 * {@code Map} key; whereas, 1 would refer to the value. If the map does not
	 * contain a value for a specific level the last generic will be used (e.g.
	 * a {@code Map} value).
	 * <p>
	 * Nesting levels may also apply to array types; for example given
	 * {@code String[]}, a nesting level of 2 refers to {@code String}.
	 * <p>
	 * If a type does not {@link #hasGenerics() contain} generics the
	 * {@link #getSuperType() supertype} hierarchy will be considered.
	 * 
	 * @param nestingLevel
	 *            the required nesting level, indexed from 1 for the current
	 *            type, 2 for the first nested generic, 3 for the second and so
	 *            on
	 * @param typeIndexesPerLevel
	 *            a map containing the generic index for a given nesting level
	 *            (may be {@code null})
	 * @return a {@link ResolvableType} for the nested level, or {@link #NONE}
	 */
	public ResolvableType getNested(int nestingLevel, @Nullable Map<Integer, Integer> typeIndexesPerLevel) {
		ResolvableType result = this;
		for (int i = 2; i <= nestingLevel; i++) {
			if (result.isArray()) {
				result = result.getComponentType();
			} else {
				// Handle derived types
				while (result != ResolvableType.NONE && !result.hasGenerics()) {
					result = result.getSuperType();
				}
				Integer index = (typeIndexesPerLevel != null ? typeIndexesPerLevel.get(i) : null);
				index = (index == null ? result.getGenerics().length - 1 : index);
				result = result.getGeneric(index);
			}
		}
		return result;
	}

	/**
	 * Return a {@link ResolvableType} representing the generic parameter for
	 * the given indexes. Indexes are zero based; for example given the type
	 * {@code Map<Integer, List<String>>}, {@code getGeneric(0)} will access the
	 * {@code Integer}. Nested generics can be accessed by specifying multiple
	 * indexes; for example {@code getGeneric(1, 0)} will access the
	 * {@code String} from the nested {@code List}. For convenience, if no
	 * indexes are specified the first generic is returned.
	 * <p>
	 * If no generic is available at the specified indexes {@link #NONE} is
	 * returned.
	 * 
	 * @param indexes
	 *            the indexes that refer to the generic parameter (may be
	 *            omitted to return the first generic)
	 * @return a {@link ResolvableType} for the specified generic, or
	 *         {@link #NONE}
	 * @see #hasGenerics()
	 * @see #getGenerics()
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public ResolvableType getGeneric(@Nullable int... indexes) {
		ResolvableType[] generics = getGenerics();
		if (indexes == null || indexes.length == 0) {
			return (generics.length == 0 ? NONE : generics[0]);
		}
		ResolvableType generic = this;
		for (int index : indexes) {
			generics = generic.getGenerics();
			if (index < 0 || index >= generics.length) {
				return NONE;
			}
			generic = generics[index];
		}
		return generic;
	}

	/**
	 * Return an array of {@link ResolvableType ResolvableTypes} representing
	 * the generic parameters of this type. If no generics are available an
	 * empty array is returned. If you need to access a specific generic
	 * consider using the {@link #getGeneric(int...)} method as it allows access
	 * to nested generics and protects against
	 * {@code IndexOutOfBoundsExceptions}.
	 * 
	 * @return an array of {@link ResolvableType ResolvableTypes} representing
	 *         the generic parameters (never {@code null})
	 * @see #hasGenerics()
	 * @see #getGeneric(int...)
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public ResolvableType[] getGenerics() {
		if (this == NONE) {
			return EMPTY_TYPES_ARRAY;
		}
		ResolvableType[] generics = this.generics;
		if (generics == null) {
			if (this.type instanceof Class) {
				Type[] typeParams = ((Class<?>) this.type).getTypeParameters();
				generics = new ResolvableType[typeParams.length];
				for (int i = 0; i < generics.length; i++) {
					generics[i] = ResolvableType.forType(typeParams[i], this);
				}
			} else if (this.type instanceof ParameterizedType) {
				Type[] actualTypeArguments = ((ParameterizedType) this.type).getActualTypeArguments();
				generics = new ResolvableType[actualTypeArguments.length];
				for (int i = 0; i < actualTypeArguments.length; i++) {
					generics[i] = forType(actualTypeArguments[i], this.variableResolver);
				}
			} else {
				generics = resolveType().getGenerics();
			}
			this.generics = generics;
		}
		return generics;
	}

	/**
	 * Convenience method that will {@link #getGenerics() get} and
	 * {@link #resolve() resolve} generic parameters.
	 * 
	 * @return an array of resolved generic parameters (the resulting array will
	 *         never be {@code null}, but it may contain {@code null} elements})
	 * @see #getGenerics()
	 * @see #resolve()
	 */
	public Class<?>[] resolveGenerics() {
		ResolvableType[] generics = getGenerics();
		Class<?>[] resolvedGenerics = new Class<?>[generics.length];
		for (int i = 0; i < generics.length; i++) {
			resolvedGenerics[i] = generics[i].resolve();
		}
		return resolvedGenerics;
	}

	/**
	 * Convenience method that will {@link #getGenerics() get} and
	 * {@link #resolve() resolve} generic parameters, using the specified
	 * {@code fallback} if any type cannot be resolved.
	 * 
	 * @param fallback
	 *            the fallback class to use if resolution fails
	 * @return an array of resolved generic parameters
	 * @see #getGenerics()
	 * @see #resolve()
	 */
	public Class<?>[] resolveGenerics(Class<?> fallback) {
		ResolvableType[] generics = getGenerics();
		Class<?>[] resolvedGenerics = new Class<?>[generics.length];
		for (int i = 0; i < generics.length; i++) {
			resolvedGenerics[i] = generics[i].resolve(fallback);
		}
		return resolvedGenerics;
	}

	/**
	 * Convenience method that will {@link #getGeneric(int...) get} and
	 * {@link #resolve() resolve} a specific generic parameters.
	 * 
	 * @param indexes
	 *            the indexes that refer to the generic parameter (may be
	 *            omitted to return the first generic)
	 * @return a resolved {@link Class} or {@code null}
	 * @see #getGeneric(int...)
	 * @see #resolve()
	 */
	@Nullable
	public Class<?> resolveGeneric(int... indexes) {
		return getGeneric(indexes).resolve();
	}

	/**
	 * Resolve this type to a {@link java.lang.Class}, returning {@code null} if
	 * the type cannot be resolved. This method will consider bounds of
	 * {@link TypeVariable TypeVariables} and {@link WildcardType WildcardTypes}
	 * if direct resolution fails; however, bounds of {@code Object.class} will
	 * be ignored.
	 * <p>
	 * If this method returns a non-null {@code Class} and
	 * {@link #hasGenerics()} returns {@code false}, the given type effectively
	 * wraps a plain {@code Class}, allowing for plain {@code Class} processing
	 * if desirable.
	 * 
	 * @return the resolved {@link Class}, or {@code null} if not resolvable
	 * @see #resolve(Class)
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	@Nullable
	public Class<?> resolve() {
		return this.resolved;
	}

	/**
	 * Resolve this type to a {@link java.lang.Class}, returning the specified
	 * {@code fallback} if the type cannot be resolved. This method will
	 * consider bounds of {@link TypeVariable TypeVariables} and
	 * {@link WildcardType WildcardTypes} if direct resolution fails; however,
	 * bounds of {@code Object.class} will be ignored.
	 * 
	 * @param fallback
	 *            the fallback class to use if resolution fails
	 * @return the resolved {@link Class} or the {@code fallback}
	 * @see #resolve()
	 * @see #resolveGeneric(int...)
	 * @see #resolveGenerics()
	 */
	public Class<?> resolve(Class<?> fallback) {
		return (this.resolved != null ? this.resolved : fallback);
	}

	@Nullable
	private Class<?> resolveClass() {
		if (this.type == EmptyType.INSTANCE) {
			return null;
		}
		if (this.type instanceof Class) {
			return (Class<?>) this.type;
		}
		if (this.type instanceof GenericArrayType) {
			Class<?> resolvedComponent = getComponentType().resolve();
			return (resolvedComponent != null ? Array.newInstance(resolvedComponent, 0).getClass() : null);
		}
		return resolveType().resolve();
	}

	/**
	 * Resolve this type by a single level, returning the resolved value or
	 * {@link #NONE}.
	 * <p>
	 * Note: The returned {@link ResolvableType} should only be used as an
	 * intermediary as it cannot be serialized.
	 */
	ResolvableType resolveType() {
		if (this.type instanceof ParameterizedType) {
			return forType(((ParameterizedType) this.type).getRawType(), this.variableResolver);
		}
		if (this.type instanceof WildcardType) {
			Type resolved = resolveBounds(((WildcardType) this.type).getUpperBounds());
			if (resolved == null) {
				resolved = resolveBounds(((WildcardType) this.type).getLowerBounds());
			}
			return forType(resolved, this.variableResolver);
		}
		if (this.type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			// Try default variable resolution
			if (this.variableResolver != null) {
				ResolvableType resolved = this.variableResolver.resolveVariable(variable);
				if (resolved != null) {
					return resolved;
				}
			}
			// Fallback to bounds
			return forType(resolveBounds(variable.getBounds()), this.variableResolver);
		}
		return NONE;
	}

	@Nullable
	private Type resolveBounds(Type[] bounds) {
		if (bounds.length == 0 || bounds[0] == Object.class) {
			return null;
		}
		return bounds[0];
	}

	@Nullable
	private ResolvableType resolveVariable(TypeVariable<?> variable) {
		if (this.type instanceof TypeVariable) {
			return resolveType().resolveVariable(variable);
		}
		if (this.type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) this.type;
			Class<?> resolved = resolve();
			if (resolved == null) {
				return null;
			}
			TypeVariable<?>[] variables = resolved.getTypeParameters();
			for (int i = 0; i < variables.length; i++) {
				if (ObjectUtils.nullSafeEquals(variables[i].getName(), variable.getName())) {
					Type actualType = parameterizedType.getActualTypeArguments()[i];
					return forType(actualType, this.variableResolver);
				}
			}
			Type ownerType = parameterizedType.getOwnerType();
			if (ownerType != null) {
				return forType(ownerType, this.variableResolver).resolveVariable(variable);
			}
		}
		if (this.type instanceof WildcardType) {
			ResolvableType resolved = resolveType().resolveVariable(variable);
			if (resolved != null) {
				return resolved;
			}
		}
		if (this.variableResolver != null) {
			return this.variableResolver.resolveVariable(variable);
		}
		return null;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ResolvableType)) {
			return false;
		}

		ResolvableType otherType = (ResolvableType) other;
		if (!ObjectUtils.nullSafeEquals(this.type, otherType.type)) {
			return false;
		}
		if (this.typeProvider != otherType.typeProvider && (this.typeProvider == null || otherType.typeProvider == null
				|| !ObjectUtils.nullSafeEquals(this.typeProvider.getType(), otherType.typeProvider.getType()))) {
			return false;
		}
		if (this.variableResolver != otherType.variableResolver && (this.variableResolver == null
				|| otherType.variableResolver == null
				|| !ObjectUtils.nullSafeEquals(this.variableResolver.getSource(), otherType.variableResolver.getSource()))) {
			return false;
		}
		if (!ObjectUtils.nullSafeEquals(this.componentType, otherType.componentType)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (this.hash != null ? this.hash : calculateHashCode());
	}

	private int calculateHashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(this.type);
		if (this.typeProvider != null) {
			hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.typeProvider.getType());
		}
		if (this.variableResolver != null) {
			hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.variableResolver.getSource());
		}
		if (this.componentType != null) {
			hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.componentType);
		}
		return hashCode;
	}

	/**
	 * Adapts this {@link ResolvableType} to a {@link VariableResolver}.
	 */
	@Nullable
	VariableResolver asVariableResolver() {
		if (this == NONE) {
			return null;
		}
		return new DefaultVariableResolver(this);
	}

	/**
	 * Custom serialization support for {@link #NONE}.
	 */
	private Object readResolve() {
		return (this.type == EmptyType.INSTANCE ? NONE : this);
	}

	/**
	 * Return a String representation of this type in its fully resolved form
	 * (including any generic parameters).
	 */
	@Override
	public String toString() {
		if (isArray()) {
			return getComponentType() + "[]";
		}
		if (this.resolved == null) {
			return "?";
		}
		if (this.type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) this.type;
			if (this.variableResolver == null || this.variableResolver.resolveVariable(variable) == null) {
				// Don't bother with variable boundaries for toString()...
				// Can cause infinite recursions in case of self-references
				return "?";
			}
		}
		if (hasGenerics()) {
			return this.resolved.getName() + '<' + StringUtils2.arrayToDelimitedString(getGenerics(), ", ") + '>';
		}
		return this.resolved.getName();
	}

	// Factory methods

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class}, using
	 * the full generic type information for assignability checks. For example:
	 * {@code ResolvableType.forClass(MyArrayList.class)}.
	 * 
	 * @param clazz
	 *            the class to introspect ({@code null} is semantically
	 *            equivalent to {@code Object.class} for typical use cases here)
	 * @return a {@link ResolvableType} for the specified class
	 * @see #forClass(Class, Class)
	 * @see #forClassWithGenerics(Class, Class...)
	 */
	public static ResolvableType forClass(@Nullable Class<?> clazz) {
		return new ResolvableType(clazz);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class}, doing
	 * assignability checks against the raw class only (analogous to
	 * {@link Class#isAssignableFrom}, which this serves as a wrapper for. For
	 * example: {@code ResolvableType.forRawClass(List.class)}.
	 * 
	 * @param clazz
	 *            the class to introspect ({@code null} is semantically
	 *            equivalent to {@code Object.class} for typical use cases here)
	 * @return a {@link ResolvableType} for the specified class
	 * @since 4.2
	 * @see #forClass(Class)
	 * @see #getRawClass()
	 */
	public static ResolvableType forRawClass(@Nullable Class<?> clazz) {
		return new ResolvableType(clazz) {
			@Override
			public ResolvableType[] getGenerics() {
				return EMPTY_TYPES_ARRAY;
			}

			@Override
			public boolean isAssignableFrom(Class<?> other) {
				return (clazz == null || ClassUtils2.isAssignable(clazz, other));
			}

			@Override
			public boolean isAssignableFrom(ResolvableType other) {
				Class<?> otherClass = other.resolve();
				return (otherClass != null && (clazz == null || ClassUtils2.isAssignable(clazz, otherClass)));
			}
		};
	}

	/**
	 * Return a {@link ResolvableType} for the specified base type (interface or
	 * base class) with a given implementation class. For example:
	 * {@code ResolvableType.forClass(List.class, MyArrayList.class)}.
	 * 
	 * @param baseType
	 *            the base type (must not be {@code null})
	 * @param implementationClass
	 *            the implementation class
	 * @return a {@link ResolvableType} for the specified base type backed by
	 *         the given implementation class
	 * @see #forClass(Class)
	 * @see #forClassWithGenerics(Class, Class...)
	 */
	public static ResolvableType forClass(Class<?> baseType, Class<?> implementationClass) {
		Assert2.notNull(baseType, "Base type must not be null");
		ResolvableType asType = forType(implementationClass).as(baseType);
		return (asType == NONE ? forType(baseType) : asType);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class} with
	 * pre-declared generics.
	 * 
	 * @param clazz
	 *            the class (or interface) to introspect
	 * @param generics
	 *            the generics of the class
	 * @return a {@link ResolvableType} for the specific class and generics
	 * @see #forClassWithGenerics(Class, ResolvableType...)
	 */
	public static ResolvableType forClassWithGenerics(Class<?> clazz, Class<?>... generics) {
		Assert2.notNull(clazz, "Class must not be null");
		Assert2.notNull(generics, "Generics array must not be null");
		ResolvableType[] resolvableGenerics = new ResolvableType[generics.length];
		for (int i = 0; i < generics.length; i++) {
			resolvableGenerics[i] = forClass(generics[i]);
		}
		return forClassWithGenerics(clazz, resolvableGenerics);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Class} with
	 * pre-declared generics.
	 * 
	 * @param clazz
	 *            the class (or interface) to introspect
	 * @param generics
	 *            the generics of the class
	 * @return a {@link ResolvableType} for the specific class and generics
	 * @see #forClassWithGenerics(Class, Class...)
	 */
	public static ResolvableType forClassWithGenerics(Class<?> clazz, ResolvableType... generics) {
		Assert2.notNull(clazz, "Class must not be null");
		Assert2.notNull(generics, "Generics array must not be null");
		TypeVariable<?>[] variables = clazz.getTypeParameters();
		Assert2.isTrue(variables.length == generics.length, "Mismatched number of generics specified");

		Type[] arguments = new Type[generics.length];
		for (int i = 0; i < generics.length; i++) {
			ResolvableType generic = generics[i];
			Type argument = (generic != null ? generic.getType() : null);
			arguments[i] = (argument != null && !(argument instanceof TypeVariable) ? argument : variables[i]);
		}

		ParameterizedType syntheticType = new SyntheticParameterizedType(clazz, arguments);
		return forType(syntheticType, new TypeVariablesVariableResolver(variables, generics));
	}

	/**
	 * Return a {@link ResolvableType} for the specified instance. The instance
	 * does not convey generic information but if it implements
	 * {@link ResolvableTypeProvider} a more precise {@link ResolvableType} can
	 * be used than the simple one based on the {@link #forClass(Class) Class
	 * instance}.
	 * 
	 * @param instance
	 *            the instance
	 * @return a {@link ResolvableType} for the specified instance
	 * @since 4.2
	 * @see ResolvableTypeProvider
	 */
	public static ResolvableType forInstance(Object instance) {
		Assert2.notNull(instance, "Instance must not be null");
		if (instance instanceof ResolvableTypeProvider) {
			ResolvableType type = ((ResolvableTypeProvider) instance).getResolvableType();
			if (type != null) {
				return type;
			}
		}
		return ResolvableType.forClass(instance.getClass());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field}.
	 * 
	 * @param field
	 *            the source field
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field, Class)
	 */
	public static ResolvableType forField(Field field) {
		Assert2.notNull(field, "Field must not be null");
		return forType(null, new FieldTypeProvider(field), null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with a
	 * given implementation.
	 * <p>
	 * Use this variant when the class that declares the field includes generic
	 * parameter variables that are satisfied by the implementation class.
	 * 
	 * @param field
	 *            the source field
	 * @param implementationClass
	 *            the implementation class
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, Class<?> implementationClass) {
		Assert2.notNull(field, "Field must not be null");
		ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
		return forType(null, new FieldTypeProvider(field), owner.asVariableResolver());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with a
	 * given implementation.
	 * <p>
	 * Use this variant when the class that declares the field includes generic
	 * parameter variables that are satisfied by the implementation type.
	 * 
	 * @param field
	 *            the source field
	 * @param implementationType
	 *            the implementation type
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, @Nullable ResolvableType implementationType) {
		Assert2.notNull(field, "Field must not be null");
		ResolvableType owner = (implementationType != null ? implementationType : NONE);
		owner = owner.as(field.getDeclaringClass());
		return forType(null, new FieldTypeProvider(field), owner.asVariableResolver());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with the
	 * given nesting level.
	 * 
	 * @param field
	 *            the source field
	 * @param nestingLevel
	 *            the nesting level (1 for the outer level; 2 for a nested
	 *            generic type; etc)
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, int nestingLevel) {
		Assert2.notNull(field, "Field must not be null");
		return forType(null, new FieldTypeProvider(field), null).getNested(nestingLevel);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Field} with a
	 * given implementation and the given nesting level.
	 * <p>
	 * Use this variant when the class that declares the field includes generic
	 * parameter variables that are satisfied by the implementation class.
	 * 
	 * @param field
	 *            the source field
	 * @param nestingLevel
	 *            the nesting level (1 for the outer level; 2 for a nested
	 *            generic type; etc)
	 * @param implementationClass
	 *            the implementation class
	 * @return a {@link ResolvableType} for the specified field
	 * @see #forField(Field)
	 */
	public static ResolvableType forField(Field field, int nestingLevel, @Nullable Class<?> implementationClass) {
		Assert2.notNull(field, "Field must not be null");
		ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
		return forType(null, new FieldTypeProvider(field), owner.asVariableResolver()).getNested(nestingLevel);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Constructor}
	 * parameter.
	 * 
	 * @param constructor
	 *            the source constructor (must not be {@code null})
	 * @param parameterIndex
	 *            the parameter index
	 * @return a {@link ResolvableType} for the specified constructor parameter
	 * @see #forConstructorParameter(Constructor, int, Class)
	 */
	public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex) {
		Assert2.notNull(constructor, "Constructor must not be null");
		return forMethodParameter(new MethodParameter(constructor, parameterIndex));
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Constructor}
	 * parameter with a given implementation. Use this variant when the class
	 * that declares the constructor includes generic parameter variables that
	 * are satisfied by the implementation class.
	 * 
	 * @param constructor
	 *            the source constructor (must not be {@code null})
	 * @param parameterIndex
	 *            the parameter index
	 * @param implementationClass
	 *            the implementation class
	 * @return a {@link ResolvableType} for the specified constructor parameter
	 * @see #forConstructorParameter(Constructor, int)
	 */
	public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex,
			Class<?> implementationClass) {

		Assert2.notNull(constructor, "Constructor must not be null");
		MethodParameter methodParameter = new MethodParameter(constructor, parameterIndex, implementationClass);
		return forMethodParameter(methodParameter);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method} return
	 * type.
	 * 
	 * @param method
	 *            the source for the method return type
	 * @return a {@link ResolvableType} for the specified method return
	 * @see #forMethodReturnType(Method, Class)
	 */
	public static ResolvableType forMethodReturnType(Method method) {
		Assert2.notNull(method, "Method must not be null");
		return forMethodParameter(new MethodParameter(method, -1));
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method} return
	 * type. Use this variant when the class that declares the method includes
	 * generic parameter variables that are satisfied by the implementation
	 * class.
	 * 
	 * @param method
	 *            the source for the method return type
	 * @param implementationClass
	 *            the implementation class
	 * @return a {@link ResolvableType} for the specified method return
	 * @see #forMethodReturnType(Method)
	 */
	public static ResolvableType forMethodReturnType(Method method, Class<?> implementationClass) {
		Assert2.notNull(method, "Method must not be null");
		MethodParameter methodParameter = new MethodParameter(method, -1, implementationClass);
		return forMethodParameter(methodParameter);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method}
	 * parameter.
	 * 
	 * @param method
	 *            the source method (must not be {@code null})
	 * @param parameterIndex
	 *            the parameter index
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int, Class)
	 * @see #forMethodParameter(MethodParameter)
	 */
	public static ResolvableType forMethodParameter(Method method, int parameterIndex) {
		Assert2.notNull(method, "Method must not be null");
		return forMethodParameter(new MethodParameter(method, parameterIndex));
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Method}
	 * parameter with a given implementation. Use this variant when the class
	 * that declares the method includes generic parameter variables that are
	 * satisfied by the implementation class.
	 * 
	 * @param method
	 *            the source method (must not be {@code null})
	 * @param parameterIndex
	 *            the parameter index
	 * @param implementationClass
	 *            the implementation class
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int, Class)
	 * @see #forMethodParameter(MethodParameter)
	 */
	public static ResolvableType forMethodParameter(Method method, int parameterIndex, Class<?> implementationClass) {
		Assert2.notNull(method, "Method must not be null");
		MethodParameter methodParameter = new MethodParameter(method, parameterIndex, implementationClass);
		return forMethodParameter(methodParameter);
	}

	/**
	 * Return a {@link ResolvableType} for the specified
	 * {@link MethodParameter}.
	 * 
	 * @param methodParameter
	 *            the source method parameter (must not be {@code null})
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int)
	 */
	public static ResolvableType forMethodParameter(MethodParameter methodParameter) {
		return forMethodParameter(methodParameter, (Type) null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link MethodParameter}
	 * with a given implementation type. Use this variant when the class that
	 * declares the method includes generic parameter variables that are
	 * satisfied by the implementation type.
	 * 
	 * @param methodParameter
	 *            the source method parameter (must not be {@code null})
	 * @param implementationType
	 *            the implementation type
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(MethodParameter)
	 */
	public static ResolvableType forMethodParameter(MethodParameter methodParameter,
			@Nullable ResolvableType implementationType) {

		Assert2.notNull(methodParameter, "MethodParameter must not be null");
		implementationType = (implementationType != null ? implementationType : forType(methodParameter.getContainingClass()));
		ResolvableType owner = implementationType.as(methodParameter.getDeclaringClass());
		return forType(null, new MethodParameterTypeProvider(methodParameter), owner.asVariableResolver())
				.getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
	}

	/**
	 * Return a {@link ResolvableType} for the specified
	 * {@link MethodParameter}, overriding the target type to resolve with a
	 * specific given type.
	 * 
	 * @param methodParameter
	 *            the source method parameter (must not be {@code null})
	 * @param targetType
	 *            the type to resolve (a part of the method parameter's type)
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @see #forMethodParameter(Method, int)
	 */
	public static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable Type targetType) {
		Assert2.notNull(methodParameter, "MethodParameter must not be null");
		return forMethodParameter(methodParameter, targetType, methodParameter.getNestingLevel());
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link MethodParameter}
	 * at a specific nesting level, overriding the target type to resolve with a
	 * specific given type.
	 * 
	 * @param methodParameter
	 *            the source method parameter (must not be {@code null})
	 * @param targetType
	 *            the type to resolve (a part of the method parameter's type)
	 * @param nestingLevel
	 *            the nesting level to use
	 * @return a {@link ResolvableType} for the specified method parameter
	 * @since 5.2
	 * @see #forMethodParameter(Method, int)
	 */
	static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable Type targetType, int nestingLevel) {

		ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
		return forType(targetType, new MethodParameterTypeProvider(methodParameter), owner.asVariableResolver())
				.getNested(nestingLevel, methodParameter.typeIndexesPerLevel);
	}

	/**
	 * Return a {@link ResolvableType} as a array of the specified
	 * {@code componentType}.
	 * 
	 * @param componentType
	 *            the component type
	 * @return a {@link ResolvableType} as an array of the specified component
	 *         type
	 */
	public static ResolvableType forArrayComponent(ResolvableType componentType) {
		Assert2.notNull(componentType, "Component type must not be null");
		Class<?> arrayClass = Array.newInstance(componentType.resolve(), 0).getClass();
		return new ResolvableType(arrayClass, null, null, componentType);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type}.
	 * <p>
	 * Note: The resulting {@link ResolvableType} instance may not be
	 * {@link Serializable}.
	 * 
	 * @param type
	 *            the source type (potentially {@code null})
	 * @return a {@link ResolvableType} for the specified {@link Type}
	 * @see #forType(Type, ResolvableType)
	 */
	public static ResolvableType forType(@Nullable Type type) {
		return forType(type, null, null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type} backed by
	 * the given owner type.
	 * <p>
	 * Note: The resulting {@link ResolvableType} instance may not be
	 * {@link Serializable}.
	 * 
	 * @param type
	 *            the source type or {@code null}
	 * @param owner
	 *            the owner type used to resolve variables
	 * @return a {@link ResolvableType} for the specified {@link Type} and owner
	 * @see #forType(Type)
	 */
	public static ResolvableType forType(@Nullable Type type, @Nullable ResolvableType owner) {
		VariableResolver variableResolver = null;
		if (owner != null) {
			variableResolver = owner.asVariableResolver();
		}
		return forType(type, variableResolver);
	}

	/**
	 * Return a {@link ResolvableType} for the specified
	 * {@link ParameterizedTypeReference}.
	 * <p>
	 * Note: The resulting {@link ResolvableType} instance may not be
	 * {@link Serializable}.
	 * 
	 * @param typeReference
	 *            the reference to obtain the source type from
	 * @return a {@link ResolvableType} for the specified
	 *         {@link ParameterizedTypeReference}
	 * @since 4.3.12
	 * @see #forType(Type)
	 */
	public static ResolvableType forType(ParameterizedTypeReference<?> typeReference) {
		return forType(typeReference.getType(), null, null);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type} backed by
	 * a given {@link VariableResolver}.
	 * 
	 * @param type
	 *            the source type or {@code null}
	 * @param variableResolver
	 *            the variable resolver or {@code null}
	 * @return a {@link ResolvableType} for the specified {@link Type} and
	 *         {@link VariableResolver}
	 */
	static ResolvableType forType(@Nullable Type type, @Nullable VariableResolver variableResolver) {
		return forType(type, null, variableResolver);
	}

	/**
	 * Return a {@link ResolvableType} for the specified {@link Type} backed by
	 * a given {@link VariableResolver}.
	 * 
	 * @param type
	 *            the source type or {@code null}
	 * @param typeProvider
	 *            the type provider or {@code null}
	 * @param variableResolver
	 *            the variable resolver or {@code null}
	 * @return a {@link ResolvableType} for the specified {@link Type} and
	 *         {@link VariableResolver}
	 */
	static ResolvableType forType(@Nullable Type type, @Nullable TypeProvider typeProvider,
			@Nullable VariableResolver variableResolver) {

		if (type == null && typeProvider != null) {
			type = SerializableTypeWrapper.forTypeProvider(typeProvider);
		}
		if (type == null) {
			return NONE;
		}

		// For simple Class references, build the wrapper right away -
		// no expensive resolution necessary, so not worth caching...
		if (type instanceof Class) {
			return new ResolvableType(type, typeProvider, variableResolver, (ResolvableType) null);
		}

		// Purge empty entries on access since we don't have a clean-up thread
		// or the like.
		cache.purgeUnreferencedEntries();

		// Check the cache - we may have a ResolvableType which has been
		// resolved before...
		ResolvableType resultType = new ResolvableType(type, typeProvider, variableResolver);
		ResolvableType cachedType = cache.get(resultType);
		if (cachedType == null) {
			cachedType = new ResolvableType(type, typeProvider, variableResolver, resultType.hash);
			cache.put(cachedType, cachedType);
		}
		resultType.resolved = cachedType.resolved;
		return resultType;
	}

	/**
	 * Clear the internal {@code ResolvableType}/{@code SerializableTypeWrapper}
	 * cache.
	 * 
	 * @since 4.2
	 */
	public static void clearCache() {
		cache.clear();
		SerializableTypeWrapper.cache.clear();
	}

	/**
	 * Strategy interface used to resolve {@link TypeVariable TypeVariables}.
	 */
	interface VariableResolver extends Serializable {

		/**
		 * Return the source of the resolver (used for hashCode and equals).
		 */
		Object getSource();

		/**
		 * Resolve the specified variable.
		 * 
		 * @param variable
		 *            the variable to resolve
		 * @return the resolved variable, or {@code null} if not found
		 */
		@Nullable
		ResolvableType resolveVariable(TypeVariable<?> variable);
	}

	private static class DefaultVariableResolver implements VariableResolver {

		private final ResolvableType source;

		DefaultVariableResolver(ResolvableType resolvableType) {
			this.source = resolvableType;
		}

		@Override
		@Nullable
		public ResolvableType resolveVariable(TypeVariable<?> variable) {
			return this.source.resolveVariable(variable);
		}

		@Override
		public Object getSource() {
			return this.source;
		}
	}

	private static class TypeVariablesVariableResolver implements VariableResolver {

		private final TypeVariable<?>[] variables;

		private final ResolvableType[] generics;

		public TypeVariablesVariableResolver(TypeVariable<?>[] variables, ResolvableType[] generics) {
			this.variables = variables;
			this.generics = generics;
		}

		@Override
		@Nullable
		public ResolvableType resolveVariable(TypeVariable<?> variable) {
			TypeVariable<?> variableToCompare = SerializableTypeWrapper.unwrap(variable);
			for (int i = 0; i < this.variables.length; i++) {
				TypeVariable<?> resolvedVariable = SerializableTypeWrapper.unwrap(this.variables[i]);
				if (ObjectUtils.nullSafeEquals(resolvedVariable, variableToCompare)) {
					return this.generics[i];
				}
			}
			return null;
		}

		@Override
		public Object getSource() {
			return this.generics;
		}
	}

	private static final class SyntheticParameterizedType implements ParameterizedType, Serializable {

		private final Type rawType;

		private final Type[] typeArguments;

		public SyntheticParameterizedType(Type rawType, Type[] typeArguments) {
			this.rawType = rawType;
			this.typeArguments = typeArguments;
		}

		@Override
		public String getTypeName() {
			String typeName = this.rawType.getTypeName();
			if (this.typeArguments.length > 0) {
				StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
				for (Type argument : this.typeArguments) {
					stringJoiner.add(argument.getTypeName());
				}
				return typeName + stringJoiner;
			}
			return typeName;
		}

		@Override
		@Nullable
		public Type getOwnerType() {
			return null;
		}

		@Override
		public Type getRawType() {
			return this.rawType;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return this.typeArguments;
		}

		@Override
		public boolean equals(@Nullable Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof ParameterizedType)) {
				return false;
			}
			ParameterizedType otherType = (ParameterizedType) other;
			return (otherType.getOwnerType() == null && this.rawType.equals(otherType.getRawType())
					&& Arrays.equals(this.typeArguments, otherType.getActualTypeArguments()));
		}

		@Override
		public int hashCode() {
			return (this.rawType.hashCode() * 31 + Arrays.hashCode(this.typeArguments));
		}

		@Override
		public String toString() {
			return getTypeName();
		}
	}

	/**
	 * Internal helper to handle bounds from {@link WildcardType WildcardTypes}.
	 */
	private static class WildcardBounds {

		private final Kind kind;

		private final ResolvableType[] bounds;

		/**
		 * Internal constructor to create a new {@link WildcardBounds} instance.
		 * 
		 * @param kind
		 *            the kind of bounds
		 * @param bounds
		 *            the bounds
		 * @see #get(ResolvableType)
		 */
		public WildcardBounds(Kind kind, ResolvableType[] bounds) {
			this.kind = kind;
			this.bounds = bounds;
		}

		/**
		 * Return {@code true} if this bounds is the same kind as the specified
		 * bounds.
		 */
		public boolean isSameKind(WildcardBounds bounds) {
			return this.kind == bounds.kind;
		}

		/**
		 * Return {@code true} if this bounds is assignable to all the specified
		 * types.
		 * 
		 * @param types
		 *            the types to test against
		 * @return {@code true} if this bounds is assignable to all types
		 */
		public boolean isAssignableFrom(ResolvableType... types) {
			for (ResolvableType bound : this.bounds) {
				for (ResolvableType type : types) {
					if (!isAssignable(bound, type)) {
						return false;
					}
				}
			}
			return true;
		}

		private boolean isAssignable(ResolvableType source, ResolvableType from) {
			return (this.kind == Kind.UPPER ? source.isAssignableFrom(from) : from.isAssignableFrom(source));
		}

		/**
		 * Return the underlying bounds.
		 */
		public ResolvableType[] getBounds() {
			return this.bounds;
		}

		/**
		 * Get a {@link WildcardBounds} instance for the specified type,
		 * returning {@code null} if the specified type cannot be resolved to a
		 * {@link WildcardType}.
		 * 
		 * @param type
		 *            the source type
		 * @return a {@link WildcardBounds} instance or {@code null}
		 */
		@Nullable
		public static WildcardBounds get(ResolvableType type) {
			ResolvableType resolveToWildcard = type;
			while (!(resolveToWildcard.getType() instanceof WildcardType)) {
				if (resolveToWildcard == NONE) {
					return null;
				}
				resolveToWildcard = resolveToWildcard.resolveType();
			}
			WildcardType wildcardType = (WildcardType) resolveToWildcard.type;
			Kind boundsType = (wildcardType.getLowerBounds().length > 0 ? Kind.LOWER : Kind.UPPER);
			Type[] bounds = (boundsType == Kind.UPPER ? wildcardType.getUpperBounds() : wildcardType.getLowerBounds());
			ResolvableType[] resolvableBounds = new ResolvableType[bounds.length];
			for (int i = 0; i < bounds.length; i++) {
				resolvableBounds[i] = ResolvableType.forType(bounds[i], type.variableResolver);
			}
			return new WildcardBounds(boundsType, resolvableBounds);
		}

		/**
		 * The various kinds of bounds.
		 */
		enum Kind {
			UPPER, LOWER
		}
	}

	/**
	 * Internal {@link Type} used to represent an empty value.
	 */
	private static class EmptyType implements Type, Serializable {

		static final Type INSTANCE = new EmptyType();

		Object readResolve() {
			return INSTANCE;
		}
	}

	/**
	 * Internal utility class that can be used to obtain wrapped
	 * {@link Serializable} variants of {@link java.lang.reflect.Type
	 * java.lang.reflect.Types}.
	 *
	 * <p>
	 * {@link #forField(Field) Fields} or
	 * {@link #forMethodParameter(MethodParameter) MethodParameters} can be used
	 * as the root source for a serializable type. Alternatively, a regular
	 * {@link Class} can also be used as source.
	 *
	 * <p>
	 * The returned type will either be a {@link Class} or a serializable proxy
	 * of {@link GenericArrayType}, {@link ParameterizedType},
	 * {@link TypeVariable} or {@link WildcardType}. With the exception of
	 * {@link Class} (which is final) calls to methods that return further
	 * {@link Type Types} (for example
	 * {@link GenericArrayType#getGenericComponentType()}) will be automatically
	 * wrapped.
	 */
	final static class SerializableTypeWrapper {

		private SerializableTypeWrapper() {
		}

		/**
		 * Return a {@link Serializable} variant of
		 * {@link Field#getGenericType()}.
		 */
		@Nullable
		public static Type forField(Field field) {
			return forTypeProvider(new FieldTypeProvider(field));
		}

		/**
		 * Return a {@link Serializable} variant of
		 * {@link MethodParameter#getGenericParameterType()}.
		 */
		@Nullable
		public static Type forMethodParameter(MethodParameter methodParameter) {
			return forTypeProvider(new MethodParameterTypeProvider(methodParameter));
		}

		/**
		 * Unwrap the given type, effectively returning the original
		 * non-serializable type.
		 * 
		 * @param type
		 *            the type to unwrap
		 * @return the original non-serializable type
		 */
		@SuppressWarnings("unchecked")
		public static <T extends Type> T unwrap(T type) {
			Type unwrapped = null;
			if (type instanceof SerializableTypeProxy) {
				unwrapped = ((SerializableTypeProxy) type).getTypeProvider().getType();
			}
			return (unwrapped != null ? (T) unwrapped : type);
		}

		/**
		 * Return a {@link Serializable} {@link Type} backed by a
		 * {@link TypeProvider} .
		 * <p>
		 * If type artifacts are generally not serializable in the current
		 * runtime environment, this delegate will simply return the original
		 * {@code Type} as-is.
		 */
		@Nullable
		static Type forTypeProvider(TypeProvider provider) {
			Type providedType = provider.getType();
			if (providedType == null || providedType instanceof Serializable) {
				// No serializable type wrapping necessary (e.g. for
				// java.lang.Class)
				return providedType;
			}
			if (!Serializable.class.isAssignableFrom(Class.class)) {
				// Let's skip any wrapping attempts if types are generally not
				// serializable in
				// the current runtime environment (even java.lang.Class itself,
				// e.g. on Graal)
				return providedType;
			}

			// Obtain a serializable type proxy for the given provider...
			Type cached = cache.get(providedType);
			if (cached != null) {
				return cached;
			}
			for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
				if (type.isInstance(providedType)) {
					ClassLoader classLoader = provider.getClass().getClassLoader();
					Class<?>[] interfaces = new Class<?>[] { type, SerializableTypeProxy.class, Serializable.class };
					InvocationHandler handler = new TypeProxyInvocationHandler(provider);
					cached = (Type) Proxy.newProxyInstance(classLoader, interfaces, handler);
					cache.put(providedType, cached);
					return cached;
				}
			}
			throw new IllegalArgumentException("Unsupported Type class: " + providedType.getClass().getName());
		}

		/**
		 * Additional interface implemented by the type proxy.
		 */
		interface SerializableTypeProxy {

			/**
			 * Return the underlying type provider.
			 */
			TypeProvider getTypeProvider();
		}

		/**
		 * A {@link Serializable} interface providing access to a {@link Type}.
		 */
		interface TypeProvider extends Serializable {

			/**
			 * Return the (possibly non {@link Serializable}) {@link Type}.
			 */
			@Nullable
			Type getType();

			/**
			 * Return the source of the type, or {@code null} if not known.
			 * <p>
			 * The default implementations returns {@code null}.
			 */
			@Nullable
			default Object getSource() {
				return null;
			}
		}

		/**
		 * {@link Serializable} {@link InvocationHandler} used by the proxied
		 * {@link Type}. Provides serialization support and enhances any methods
		 * that return {@code Type} or {@code Type[]}.
		 */
		static class TypeProxyInvocationHandler implements InvocationHandler, Serializable {

			private final TypeProvider provider;

			public TypeProxyInvocationHandler(TypeProvider provider) {
				this.provider = provider;
			}

			@Override
			@Nullable
			public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
				if (method.getName().equals("equals") && args != null) {
					Object other = args[0];
					// Unwrap proxies for speed
					if (other instanceof Type) {
						other = unwrap((Type) other);
					}
					return ObjectUtils.nullSafeEquals(this.provider.getType(), other);
				} else if (method.getName().equals("hashCode")) {
					return ObjectUtils.nullSafeHashCode(this.provider.getType());
				} else if (method.getName().equals("getTypeProvider")) {
					return this.provider;
				}

				if (Type.class == method.getReturnType() && args == null) {
					return forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, -1));
				} else if (Type[].class == method.getReturnType() && args == null) {
					Type[] result = new Type[((Type[]) method.invoke(this.provider.getType())).length];
					for (int i = 0; i < result.length; i++) {
						result[i] = forTypeProvider(new MethodInvokeTypeProvider(this.provider, method, i));
					}
					return result;
				}

				try {
					return method.invoke(this.provider.getType(), args);
				} catch (InvocationTargetException ex) {
					throw ex.getTargetException();
				}
			}
		}

		/**
		 * {@link TypeProvider} for {@link Type Types} obtained from a
		 * {@link Field}.
		 */
		static class FieldTypeProvider implements TypeProvider {

			private final String fieldName;

			private final Class<?> declaringClass;

			private transient Field field;

			public FieldTypeProvider(Field field) {
				this.fieldName = field.getName();
				this.declaringClass = field.getDeclaringClass();
				this.field = field;
			}

			@Override
			public Type getType() {
				return this.field.getGenericType();
			}

			@Override
			public Object getSource() {
				return this.field;
			}

			private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
				inputStream.defaultReadObject();
				try {
					this.field = this.declaringClass.getDeclaredField(this.fieldName);
				} catch (Throwable ex) {
					throw new IllegalStateException("Could not find original class structure", ex);
				}
			}
		}

		/**
		 * {@link TypeProvider} for {@link Type Types} obtained from a
		 * {@link MethodParameter}.
		 */
		static class MethodParameterTypeProvider implements TypeProvider {

			@Nullable
			private final String methodName;

			private final Class<?>[] parameterTypes;

			private final Class<?> declaringClass;

			private final int parameterIndex;

			private transient MethodParameter methodParameter;

			public MethodParameterTypeProvider(MethodParameter methodParameter) {
				this.methodName = (methodParameter.getMethod() != null ? methodParameter.getMethod().getName() : null);
				this.parameterTypes = methodParameter.getExecutable().getParameterTypes();
				this.declaringClass = methodParameter.getDeclaringClass();
				this.parameterIndex = methodParameter.getParameterIndex();
				this.methodParameter = methodParameter;
			}

			@Override
			public Type getType() {
				return this.methodParameter.getGenericParameterType();
			}

			@Override
			public Object getSource() {
				return this.methodParameter;
			}

			private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
				inputStream.defaultReadObject();
				try {
					if (this.methodName != null) {
						this.methodParameter = new MethodParameter(
								this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
					} else {
						this.methodParameter = new MethodParameter(
								this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
					}
				} catch (Throwable ex) {
					throw new IllegalStateException("Could not find original class structure", ex);
				}
			}
		}

		/**
		 * {@link TypeProvider} for {@link Type Types} obtained by invoking a
		 * no-arg method.
		 */
		static class MethodInvokeTypeProvider implements TypeProvider {

			private final TypeProvider provider;

			private final String methodName;

			private final Class<?> declaringClass;

			private final int index;

			private transient Method method;

			@Nullable
			private transient volatile Object result;

			public MethodInvokeTypeProvider(TypeProvider provider, Method method, int index) {
				this.provider = provider;
				this.methodName = method.getName();
				this.declaringClass = method.getDeclaringClass();
				this.index = index;
				this.method = method;
			}

			@Override
			@Nullable
			public Type getType() {
				Object result = this.result;
				if (result == null) {
					// Lazy invocation of the target method on the provided type
					result = ReflectionUtils2.invokeMethod(this.method, this.provider.getType());
					// Cache the result for further calls to getType()
					this.result = result;
				}
				return (result instanceof Type[] ? ((Type[]) result)[this.index] : (Type) result);
			}

			@Override
			@Nullable
			public Object getSource() {
				return null;
			}

			private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
				inputStream.defaultReadObject();
				Method method = ReflectionUtils2.findMethod(this.declaringClass, this.methodName);
				if (method == null) {
					throw new IllegalStateException("Cannot find method on deserialization: " + this.methodName);
				}
				if (method.getReturnType() != Type.class && method.getReturnType() != Type[].class) {
					throw new IllegalStateException(
							"Invalid return type on deserialized method - needs to be Type or Type[]: " + method);
				}
				this.method = method;
			}
		}

		final private static Class<?>[] SUPPORTED_SERIALIZABLE_TYPES = { GenericArrayType.class, ParameterizedType.class,
				TypeVariable.class, WildcardType.class };

		final static ConcurrentReferenceHashMap<Type, Type> cache = new ConcurrentReferenceHashMap<>(256);

	}

	/**
	 * Any object can implement this interface to provide its actual
	 * {@link ResolvableType}.
	 *
	 * <p>
	 * Such information is very useful when figuring out if the instance matches
	 * a generic signature as Java does not convey the signature at runtime.
	 *
	 * <p>
	 * Users of this interface should be careful in complex hierarchy scenarios,
	 * especially when the generic type signature of the class changes in
	 * sub-classes. It is always possible to return {@code null} to fallback on
	 * a default behavior.
	 *
	 * @author Stephane Nicoll
	 * @since 4.2
	 */
	public static interface ResolvableTypeProvider {

		/**
		 * Return the {@link ResolvableType} describing this instance (or
		 * {@code null} if some sort of default should be applied instead).
		 */
		ResolvableType getResolvableType();

	}

	/**
	 * Helper class that encapsulates the specification of a method parameter,
	 * i.e. a {@link Method} or {@link Constructor} plus a parameter index and a
	 * nested type index for a declared generic type. Useful as a specification
	 * object to pass along.
	 * <p>
	 * subclass available which synthesizes annotations with attribute aliases.
	 * That subclass is used for web and message endpoint processing, in
	 * particular.
	 */
	public static class MethodParameter {

		private final Executable executable;
		private final int parameterIndex;
		@Nullable
		private volatile Parameter parameter;
		private int nestingLevel;

		/** Map from Integer level to Integer type index. */
		@Nullable
		Map<Integer, Integer> typeIndexesPerLevel;

		/**
		 * The containing class. Could also be supplied by overriding
		 * {@link #getContainingClass()}
		 */
		@Nullable
		private volatile Class<?> containingClass;

		@Nullable
		private volatile Class<?> parameterType;

		@Nullable
		private volatile Type genericParameterType;

		@Nullable
		private volatile Annotation[] parameterAnnotations;

		@Nullable
		private volatile String parameterName;

		@Nullable
		private volatile MethodParameter nestedMethodParameter;

		/**
		 * Create a new {@code MethodParameter} for the given method, with
		 * nesting level 1.
		 * 
		 * @param method
		 *            the Method to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter: -1 for the method return type;
		 *            0 for the first method parameter; 1 for the second method
		 *            parameter, etc.
		 */
		public MethodParameter(Method method, int parameterIndex) {
			this(method, parameterIndex, 1);
		}

		/**
		 * Create a new {@code MethodParameter} for the given method.
		 * 
		 * @param method
		 *            the Method to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter: -1 for the method return type;
		 *            0 for the first method parameter; 1 for the second method
		 *            parameter, etc.
		 * @param nestingLevel
		 *            the nesting level of the target type (typically 1; e.g. in
		 *            case of a List of Lists, 1 would indicate the nested List,
		 *            whereas 2 would indicate the element of the nested List)
		 */
		public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
			Assert2.notNull(method, "Method must not be null");
			this.executable = method;
			this.parameterIndex = validateIndex(method, parameterIndex);
			this.nestingLevel = nestingLevel;
		}

		/**
		 * Create a new MethodParameter for the given constructor, with nesting
		 * level 1.
		 * 
		 * @param constructor
		 *            the Constructor to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter
		 */
		public MethodParameter(Constructor<?> constructor, int parameterIndex) {
			this(constructor, parameterIndex, 1);
		}

		/**
		 * Create a new MethodParameter for the given constructor.
		 * 
		 * @param constructor
		 *            the Constructor to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter
		 * @param nestingLevel
		 *            the nesting level of the target type (typically 1; e.g. in
		 *            case of a List of Lists, 1 would indicate the nested List,
		 *            whereas 2 would indicate the element of the nested List)
		 */
		public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
			Assert2.notNull(constructor, "Constructor must not be null");
			this.executable = constructor;
			this.parameterIndex = validateIndex(constructor, parameterIndex);
			this.nestingLevel = nestingLevel;
		}

		/**
		 * Internal constructor used to create a {@link MethodParameter} with a
		 * containing class already set.
		 * 
		 * @param executable
		 *            the Executable to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter
		 * @param containingClass
		 *            the containing class
		 * @since 5.2
		 */
		MethodParameter(Executable executable, int parameterIndex, @Nullable Class<?> containingClass) {
			Assert2.notNull(executable, "Executable must not be null");
			this.executable = executable;
			this.parameterIndex = validateIndex(executable, parameterIndex);
			this.nestingLevel = 1;
			this.containingClass = containingClass;
		}

		/**
		 * Copy constructor, resulting in an independent MethodParameter object
		 * based on the same metadata and cache state that the original object
		 * was in.
		 * 
		 * @param original
		 *            the original MethodParameter object to copy from
		 */
		public MethodParameter(MethodParameter original) {
			Assert2.notNull(original, "Original must not be null");
			this.executable = original.executable;
			this.parameterIndex = original.parameterIndex;
			this.parameter = original.parameter;
			this.nestingLevel = original.nestingLevel;
			this.typeIndexesPerLevel = original.typeIndexesPerLevel;
			this.containingClass = original.containingClass;
			this.parameterType = original.parameterType;
			this.genericParameterType = original.genericParameterType;
			this.parameterAnnotations = original.parameterAnnotations;
			this.parameterName = original.parameterName;
		}

		/**
		 * Return the wrapped Method, if any.
		 * <p>
		 * Note: Either Method or Constructor is available.
		 * 
		 * @return the Method, or {@code null} if none
		 */
		@Nullable
		public Method getMethod() {
			return (this.executable instanceof Method ? (Method) this.executable : null);
		}

		/**
		 * Return the wrapped Constructor, if any.
		 * <p>
		 * Note: Either Method or Constructor is available.
		 * 
		 * @return the Constructor, or {@code null} if none
		 */
		@Nullable
		public Constructor<?> getConstructor() {
			return (this.executable instanceof Constructor ? (Constructor<?>) this.executable : null);
		}

		/**
		 * Return the class that declares the underlying Method or Constructor.
		 */
		public Class<?> getDeclaringClass() {
			return this.executable.getDeclaringClass();
		}

		/**
		 * Return the wrapped member.
		 * 
		 * @return the Method or Constructor as Member
		 */
		public Member getMember() {
			return this.executable;
		}

		/**
		 * Return the wrapped annotated element.
		 * <p>
		 * Note: This method exposes the annotations declared on the
		 * method/constructor itself (i.e. at the method/constructor level, not
		 * at the parameter level).
		 * 
		 * @return the Method or Constructor as AnnotatedElement
		 */
		public AnnotatedElement getAnnotatedElement() {
			return this.executable;
		}

		/**
		 * Return the wrapped executable.
		 * 
		 * @return the Method or Constructor as Executable
		 * @since 5.0
		 */
		public Executable getExecutable() {
			return this.executable;
		}

		/**
		 * Return the {@link Parameter} descriptor for method/constructor
		 * parameter.
		 * 
		 * @since 5.0
		 */
		public Parameter getParameter() {
			if (this.parameterIndex < 0) {
				throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
			}
			Parameter parameter = this.parameter;
			if (parameter == null) {
				parameter = getExecutable().getParameters()[this.parameterIndex];
				this.parameter = parameter;
			}
			return parameter;
		}

		/**
		 * Return the index of the method/constructor parameter.
		 * 
		 * @return the parameter index (-1 in case of the return type)
		 */
		public int getParameterIndex() {
			return this.parameterIndex;
		}

		/**
		 * Increase this parameter's nesting level.
		 * 
		 * @see #getNestingLevel()
		 * @deprecated since 5.2 in favor of {@link #nested(Integer)}
		 */
		@Deprecated
		public void increaseNestingLevel() {
			this.nestingLevel++;
		}

		/**
		 * Decrease this parameter's nesting level.
		 * 
		 * @see #getNestingLevel()
		 * @deprecated since 5.2 in favor of retaining the original
		 *             MethodParameter and using {@link #nested(Integer)} if
		 *             nesting is required
		 */
		@Deprecated
		public void decreaseNestingLevel() {
			getTypeIndexesPerLevel().remove(this.nestingLevel);
			this.nestingLevel--;
		}

		/**
		 * Return the nesting level of the target type (typically 1; e.g. in
		 * case of a List of Lists, 1 would indicate the nested List, whereas 2
		 * would indicate the element of the nested List).
		 */
		public int getNestingLevel() {
			return this.nestingLevel;
		}

		/**
		 * Return a variant of this {@code MethodParameter} with the type for
		 * the current level set to the specified value.
		 * 
		 * @param typeIndex
		 *            the new type index
		 * @since 5.2
		 */
		public MethodParameter withTypeIndex(int typeIndex) {
			return nested(this.nestingLevel, typeIndex);
		}

		/**
		 * Set the type index for the current nesting level.
		 * 
		 * @param typeIndex
		 *            the corresponding type index (or {@code null} for the
		 *            default type index)
		 * @see #getNestingLevel()
		 * @deprecated since 5.2 in favor of {@link #withTypeIndex}
		 */
		@Deprecated
		public void setTypeIndexForCurrentLevel(int typeIndex) {
			getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
		}

		/**
		 * Return the type index for the current nesting level.
		 * 
		 * @return the corresponding type index, or {@code null} if none
		 *         specified (indicating the default type index)
		 * @see #getNestingLevel()
		 */
		@Nullable
		public Integer getTypeIndexForCurrentLevel() {
			return getTypeIndexForLevel(this.nestingLevel);
		}

		/**
		 * Return the type index for the specified nesting level.
		 * 
		 * @param nestingLevel
		 *            the nesting level to check
		 * @return the corresponding type index, or {@code null} if none
		 *         specified (indicating the default type index)
		 */
		@Nullable
		public Integer getTypeIndexForLevel(int nestingLevel) {
			return getTypeIndexesPerLevel().get(nestingLevel);
		}

		/**
		 * Obtain the (lazily constructed) type-indexes-per-level Map.
		 */
		private Map<Integer, Integer> getTypeIndexesPerLevel() {
			if (this.typeIndexesPerLevel == null) {
				this.typeIndexesPerLevel = new HashMap<>(4);
			}
			return this.typeIndexesPerLevel;
		}

		/**
		 * Return a variant of this {@code MethodParameter} which points to the
		 * same parameter but one nesting level deeper.
		 * 
		 * @since 4.3
		 */
		public MethodParameter nested() {
			return nested(null);
		}

		/**
		 * Return a variant of this {@code MethodParameter} which points to the
		 * same parameter but one nesting level deeper.
		 * 
		 * @param typeIndex
		 *            the type index for the new nesting level
		 * @since 5.2
		 */
		public MethodParameter nested(@Nullable Integer typeIndex) {
			MethodParameter nestedParam = this.nestedMethodParameter;
			if (nestedParam != null && typeIndex == null) {
				return nestedParam;
			}
			nestedParam = nested(this.nestingLevel + 1, typeIndex);
			if (typeIndex == null) {
				this.nestedMethodParameter = nestedParam;
			}
			return nestedParam;
		}

		private MethodParameter nested(int nestingLevel, @Nullable Integer typeIndex) {
			MethodParameter copy = clone();
			copy.nestingLevel = nestingLevel;
			if (this.typeIndexesPerLevel != null) {
				copy.typeIndexesPerLevel = new HashMap<>(this.typeIndexesPerLevel);
			}
			if (typeIndex != null) {
				copy.getTypeIndexesPerLevel().put(copy.nestingLevel, typeIndex);
			}
			copy.parameterType = null;
			copy.genericParameterType = null;
			return copy;
		}

		/**
		 * Return whether this method indicates a parameter which is not
		 * required: either in the form of Java 8's {@link java.util.Optional},
		 * any variant of a parameter-level {@code Nullable} annotation (such as
		 * from JSR-305 or the FindBugs set of annotations), or a language-level
		 * nullable type declaration or {@code Continuation} parameter in
		 * Kotlin.
		 * 
		 * @since 4.3
		 */
		public boolean isOptional() {
			return (getParameterType() == Optional.class || hasNullableAnnotation());
		}

		/**
		 * Check whether this method parameter is annotated with any variant of
		 * a {@code Nullable} annotation, e.g. {@code javax.annotation.Nullable}
		 * or {@code edu.umd.cs.findbugs.annotations.Nullable}.
		 */
		private boolean hasNullableAnnotation() {
			for (Annotation ann : getParameterAnnotations()) {
				if ("Nullable".equals(ann.annotationType().getSimpleName())) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Return a variant of this {@code MethodParameter} which points to the
		 * same parameter but one nesting level deeper in case of a
		 * {@link java.util.Optional} declaration.
		 * 
		 * @since 4.3
		 * @see #isOptional()
		 * @see #nested()
		 */
		public MethodParameter nestedIfOptional() {
			return (getParameterType() == Optional.class ? nested() : this);
		}

		/**
		 * Return a variant of this {@code MethodParameter} which refers to the
		 * given containing class.
		 * 
		 * @param containingClass
		 *            a specific containing class (potentially a subclass of the
		 *            declaring class, e.g. substituting a type variable)
		 * @since 5.2
		 * @see #getParameterType()
		 */
		public MethodParameter withContainingClass(@Nullable Class<?> containingClass) {
			MethodParameter result = clone();
			result.containingClass = containingClass;
			result.parameterType = null;
			return result;
		}

		/**
		 * Set a containing class to resolve the parameter type against.
		 */
		@Deprecated
		void setContainingClass(Class<?> containingClass) {
			this.containingClass = containingClass;
			this.parameterType = null;
		}

		/**
		 * Return the containing class for this method parameter.
		 * 
		 * @return a specific containing class (potentially a subclass of the
		 *         declaring class), or otherwise simply the declaring class
		 *         itself
		 * @see #getDeclaringClass()
		 */
		public Class<?> getContainingClass() {
			Class<?> containingClass = this.containingClass;
			return (containingClass != null ? containingClass : getDeclaringClass());
		}

		/**
		 * Set a resolved (generic) parameter type.
		 */
		@Deprecated
		void setParameterType(@Nullable Class<?> parameterType) {
			this.parameterType = parameterType;
		}

		/**
		 * Return the type of the method/constructor parameter.
		 * 
		 * @return the parameter type (never {@code null})
		 */
		public Class<?> getParameterType() {
			Class<?> paramType = this.parameterType;
			if (paramType != null) {
				return paramType;
			}
			if (getContainingClass() != getDeclaringClass()) {
				paramType = ResolvableType.forMethodParameter(this, null, 1).resolve();
			}
			if (paramType == null) {
				paramType = computeParameterType();
			}
			this.parameterType = paramType;
			return paramType;
		}

		/**
		 * Return the generic type of the method/constructor parameter.
		 * 
		 * @return the parameter type (never {@code null})
		 * @since 3.0
		 */
		public Type getGenericParameterType() {
			Type paramType = this.genericParameterType;
			if (paramType == null) {
				if (this.parameterIndex >= 0) {
					Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
					int index = this.parameterIndex;
					if (this.executable instanceof Constructor && ClassUtils2.isInnerClass(this.executable.getDeclaringClass())
							&& genericParameterTypes.length == this.executable.getParameterCount() - 1) {
						// Bug in javac: type array excludes enclosing instance
						// parameter
						// for inner classes with at least one generic
						// constructor
						// parameter,
						// so access it with the actual parameter index lowered
						// by 1
						index = this.parameterIndex - 1;
					}
					paramType = (index >= 0 && index < genericParameterTypes.length ? genericParameterTypes[index]
							: computeParameterType());
				}
				this.genericParameterType = paramType;
			}
			return paramType;
		}

		private Class<?> computeParameterType() {
			if (this.parameterIndex < 0) {
				Method method = getMethod();
				if (method == null) {
					return void.class;
				}
				return method.getReturnType();
			}
			return this.executable.getParameterTypes()[this.parameterIndex];
		}

		/**
		 * Return the nested type of the method/constructor parameter.
		 * 
		 * @return the parameter type (never {@code null})
		 * @since 3.1
		 * @see #getNestingLevel()
		 */
		public Class<?> getNestedParameterType() {
			if (this.nestingLevel > 1) {
				Type type = getGenericParameterType();
				for (int i = 2; i <= this.nestingLevel; i++) {
					if (type instanceof ParameterizedType) {
						Type[] args = ((ParameterizedType) type).getActualTypeArguments();
						Integer index = getTypeIndexForLevel(i);
						type = args[index != null ? index : args.length - 1];
					}
					// TODO: Object.class if unresolvable
				}
				if (type instanceof Class) {
					return (Class<?>) type;
				} else if (type instanceof ParameterizedType) {
					Type arg = ((ParameterizedType) type).getRawType();
					if (arg instanceof Class) {
						return (Class<?>) arg;
					}
				}
				return Object.class;
			} else {
				return getParameterType();
			}
		}

		/**
		 * Return the nested generic type of the method/constructor parameter.
		 * 
		 * @return the parameter type (never {@code null})
		 * @since 4.2
		 * @see #getNestingLevel()
		 */
		public Type getNestedGenericParameterType() {
			if (this.nestingLevel > 1) {
				Type type = getGenericParameterType();
				for (int i = 2; i <= this.nestingLevel; i++) {
					if (type instanceof ParameterizedType) {
						Type[] args = ((ParameterizedType) type).getActualTypeArguments();
						Integer index = getTypeIndexForLevel(i);
						type = args[index != null ? index : args.length - 1];
					}
				}
				return type;
			} else {
				return getGenericParameterType();
			}
		}

		/**
		 * Return the annotations associated with the target method/constructor
		 * itself.
		 */
		public Annotation[] getMethodAnnotations() {
			return adaptAnnotationArray(getAnnotatedElement().getAnnotations());
		}

		/**
		 * Return the method/constructor annotation of the given type, if
		 * available.
		 * 
		 * @param annotationType
		 *            the annotation type to look for
		 * @return the annotation object, or {@code null} if not found
		 */
		@Nullable
		public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
			A annotation = getAnnotatedElement().getAnnotation(annotationType);
			return (annotation != null ? adaptAnnotation(annotation) : null);
		}

		/**
		 * Return whether the method/constructor is annotated with the given
		 * type.
		 * 
		 * @param annotationType
		 *            the annotation type to look for
		 * @since 4.3
		 * @see #getMethodAnnotation(Class)
		 */
		public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
			return getAnnotatedElement().isAnnotationPresent(annotationType);
		}

		/**
		 * Return the annotations associated with the specific
		 * method/constructor parameter.
		 */
		public Annotation[] getParameterAnnotations() {
			Annotation[] paramAnns = this.parameterAnnotations;
			if (paramAnns == null) {
				Annotation[][] annotationArray = this.executable.getParameterAnnotations();
				int index = this.parameterIndex;
				if (this.executable instanceof Constructor && ClassUtils2.isInnerClass(this.executable.getDeclaringClass())
						&& annotationArray.length == this.executable.getParameterCount() - 1) {
					// Bug in javac in JDK <9: annotation array excludes
					// enclosing
					// instance parameter
					// for inner classes, so access it with the actual parameter
					// index lowered by 1
					index = this.parameterIndex - 1;
				}
				paramAnns = (index >= 0 && index < annotationArray.length ? adaptAnnotationArray(annotationArray[index])
						: EMPTY_ANNOTATION_ARRAY);
				this.parameterAnnotations = paramAnns;
			}
			return paramAnns;
		}

		/**
		 * Return {@code true} if the parameter has at least one annotation,
		 * {@code false} if it has none.
		 * 
		 * @see #getParameterAnnotations()
		 */
		public boolean hasParameterAnnotations() {
			return (getParameterAnnotations().length != 0);
		}

		/**
		 * Return the parameter annotation of the given type, if available.
		 * 
		 * @param annotationType
		 *            the annotation type to look for
		 * @return the annotation object, or {@code null} if not found
		 */
		@SuppressWarnings("unchecked")
		@Nullable
		public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
			Annotation[] anns = getParameterAnnotations();
			for (Annotation ann : anns) {
				if (annotationType.isInstance(ann)) {
					return (A) ann;
				}
			}
			return null;
		}

		/**
		 * Return whether the parameter is declared with the given annotation
		 * type.
		 * 
		 * @param annotationType
		 *            the annotation type to look for
		 * @see #getParameterAnnotation(Class)
		 */
		public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
			return (getParameterAnnotation(annotationType) != null);
		}

		/**
		 * Return the name of the method/constructor parameter.
		 * 
		 * @return the parameter name (may be {@code null} if no parameter name
		 *         metadata is contained in the class file or no
		 *         {@link #initParameterNameDiscovery ParameterNameDiscoverer}
		 *         has been set to begin with)
		 */
		@Nullable
		public String getParameterName() {
			if (this.parameterIndex < 0) {
				return null;
			}
			return this.parameterName;
		}

		/**
		 * A template method to post-process a given annotation instance before
		 * returning it to the caller.
		 * <p>
		 * The default implementation simply returns the given annotation as-is.
		 * 
		 * @param annotation
		 *            the annotation about to be returned
		 * @return the post-processed annotation (or simply the original one)
		 * @since 4.2
		 */
		protected <A extends Annotation> A adaptAnnotation(A annotation) {
			return annotation;
		}

		/**
		 * A template method to post-process a given annotation array before
		 * returning it to the caller.
		 * <p>
		 * The default implementation simply returns the given annotation array
		 * as-is.
		 * 
		 * @param annotations
		 *            the annotation array about to be returned
		 * @return the post-processed annotation array (or simply the original
		 *         one)
		 * @since 4.2
		 */
		protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
			return annotations;
		}

		@Override
		public boolean equals(@Nullable Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof MethodParameter)) {
				return false;
			}
			MethodParameter otherParam = (MethodParameter) other;
			return (getContainingClass() == otherParam.getContainingClass()
					&& ObjectUtils.nullSafeEquals(this.typeIndexesPerLevel, otherParam.typeIndexesPerLevel)
					&& this.nestingLevel == otherParam.nestingLevel && this.parameterIndex == otherParam.parameterIndex
					&& this.executable.equals(otherParam.executable));
		}

		@Override
		public int hashCode() {
			return (31 * this.executable.hashCode() + this.parameterIndex);
		}

		@Override
		public String toString() {
			Method method = getMethod();
			return (method != null ? "method '" + method.getName() + "'" : "constructor") + " parameter " + this.parameterIndex;
		}

		@Override
		public MethodParameter clone() {
			return new MethodParameter(this);
		}

		/**
		 * Create a new MethodParameter for the given method or constructor.
		 * <p>
		 * This is a convenience factory method for scenarios where a Method or
		 * Constructor reference is treated in a generic fashion.
		 * 
		 * @param methodOrConstructor
		 *            the Method or Constructor to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter
		 * @return the corresponding MethodParameter instance
		 * @deprecated as of 5.0, in favor of {@link #forExecutable}
		 */
		@Deprecated
		public static MethodParameter forMethodOrConstructor(Object methodOrConstructor, int parameterIndex) {
			if (!(methodOrConstructor instanceof Executable)) {
				throw new IllegalArgumentException(
						"Given object [" + methodOrConstructor + "] is neither a Method nor a Constructor");
			}
			return forExecutable((Executable) methodOrConstructor, parameterIndex);
		}

		/**
		 * Create a new MethodParameter for the given method or constructor.
		 * <p>
		 * This is a convenience factory method for scenarios where a Method or
		 * Constructor reference is treated in a generic fashion.
		 * 
		 * @param executable
		 *            the Method or Constructor to specify a parameter for
		 * @param parameterIndex
		 *            the index of the parameter
		 * @return the corresponding MethodParameter instance
		 * @since 5.0
		 */
		public static MethodParameter forExecutable(Executable executable, int parameterIndex) {
			if (executable instanceof Method) {
				return new MethodParameter((Method) executable, parameterIndex);
			} else if (executable instanceof Constructor) {
				return new MethodParameter((Constructor<?>) executable, parameterIndex);
			} else {
				throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
			}
		}

		/**
		 * Create a new MethodParameter for the given parameter descriptor.
		 * <p>
		 * This is a convenience factory method for scenarios where a Java 8
		 * {@link Parameter} descriptor is already available.
		 * 
		 * @param parameter
		 *            the parameter descriptor
		 * @return the corresponding MethodParameter instance
		 * @since 5.0
		 */
		public static MethodParameter forParameter(Parameter parameter) {
			return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
		}

		protected static int findParameterIndex(Parameter parameter) {
			Executable executable = parameter.getDeclaringExecutable();
			Parameter[] allParams = executable.getParameters();
			// Try first with identity checks for greater performance.
			for (int i = 0; i < allParams.length; i++) {
				if (parameter == allParams[i]) {
					return i;
				}
			}
			// Potentially try again with object equality checks in order to
			// avoid
			// race
			// conditions while invoking
			// java.lang.reflect.Executable.getParameters().
			for (int i = 0; i < allParams.length; i++) {
				if (parameter.equals(allParams[i])) {
					return i;
				}
			}
			throw new IllegalArgumentException(
					"Given parameter [" + parameter + "] does not match any parameter in the declaring executable");
		}

		private static int validateIndex(Executable executable, int parameterIndex) {
			int count = executable.getParameterCount();
			Assert2.isTrue(parameterIndex >= -1 && parameterIndex < count,
					"Parameter index needs to be between -1 and " + (count - 1));
			return parameterIndex;
		}

		final private static Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

	}

}
