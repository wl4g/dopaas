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
package com.wl4g.devops.common.framework.operator;

import com.wl4g.devops.components.tools.common.collection.RegisteredUnmodifiableMap;
import com.wl4g.devops.components.tools.common.log.SmartLogger;

import javax.validation.constraints.NotNull;

import org.springframework.core.ResolvableType;

import java.util.*;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Composite generic operator adapter.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2019年11月1日
 * @since
 */
public abstract class GenericOperatorAdapter<K extends Enum<?>, O extends Operator<K>> implements Operator<K> {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Generic registrar of operator alias names.
	 */
	final protected Map<K, O> operatorAliasRegistry = new RegisteredUnmodifiableMap<>(new HashMap<>());

	/**
	 * Generic registrar of operator classes.
	 */
	final protected Map<Class<? extends Operator<Enum<?>>>, O> operatorClassRegistry = new RegisteredUnmodifiableMap<>(
			new HashMap<>());

	/**
	 * Kind type class of operator provider.
	 */
	final private Class<? extends Enum<?>> kindClass;

	/**
	 * Fallback no operation of operator.
	 */
	final private O fallbackNoOp;

	public GenericOperatorAdapter() {
		this(null, null);
	}

	public GenericOperatorAdapter(O fallbackNoOp) {
		this(null, fallbackNoOp);
	}

	public GenericOperatorAdapter(List<O> operators) {
		this(operators, null);
	}

	@SuppressWarnings("unchecked")
	public GenericOperatorAdapter(List<O> operators, O fallbackNoOp) {
		this.fallbackNoOp = fallbackNoOp;

		// Resolving real Kind class.
		ResolvableType resolveType = ResolvableType.forClass(getClass());
		this.kindClass = (Class<? extends Enum<?>>) resolveType.getSuperType().getGeneric(0).resolve();
		Class<?> adapterInterfaceClass = resolveType.getSuperType().getGeneric(1).resolve();
		notNullOf(kindClass, "kindClass");
		notNullOf(adapterInterfaceClass, "adapterInterfaceClass");

		// Maybe none of the specific operators has been instantiated.
		if (!isEmpty(operators)) {
			// Duplicate checks.
			Set<K> kinds = new HashSet<>();
			operators.forEach(o -> {
				notNull(o.kind(), format("Provider kind can't empty, operator: %s", o));
				state(!kinds.contains(o.kind()), format("Repeated definition operator with kind: %s", o.kind()));
				kinds.add(o.kind());
			});

			// Register of kind aliases.
			this.operatorAliasRegistry.putAll(operators.stream().collect(toMap(O::kind, o -> o)));
			// Register of kind classes.
			this.operatorClassRegistry
					.putAll(operators.stream().collect(toMap(o -> (Class<Operator<Enum<?>>>) o.getClass(), o -> o)));

			log.info("Registered operator '{}' instances of: {}", adapterInterfaceClass, operators);
		} else {
			log.warn("Skip '{}' composite adapter registered, because inject operators is empty.", adapterInterfaceClass);
		}

	}

	@Override
	public K kind() {
		// No such situation, It must be ignored.
		throw new UnsupportedOperationException();
	}

	/**
	 * Making the adaptation actually execute {@link O}.
	 * 
	 * @param vcs
	 * @return
	 * @throws NoSuchOperatorException
	 */
	public <T> O forOperator(@NotNull Class<T> operatorClass) throws NoSuchOperatorException {
		O operator = ensureOperator(operatorClassRegistry.get(operatorClass),
				format("No such operator instance of class: '%s'", operatorClass));
		return operator;
	}

	/**
	 * Making the adaptation actually execute {@link O}.
	 * 
	 * @param vcs
	 * @return
	 */
	public O forOperator(@NotNull K k) throws NoSuchOperatorException {
		return forOperator(k.name());
	}

	/**
	 * Making the adaptation actually execute {@link O}.
	 *
	 * @param P
	 * @return
	 * @throws NoSuchOperatorException
	 */
	public O forOperator(@NotNull String kindName) throws NoSuchOperatorException {
		K kind = parseKind(kindName);
		O operator = ensureOperator(operatorAliasRegistry.get(kind),
				format("No such operator bean instance for kind name: '%s'", kind));
		return operator;
	}

	/**
	 * Gets the currently running operator kind.
	 * 
	 * @return
	 */
	public Set<K> getRunningKinds() {
		return operatorAliasRegistry.keySet();
	}

	/**
	 * Ensure operator.
	 * 
	 * @param operator
	 * @param assertMsg
	 * @return
	 */
	private O ensureOperator(O operator, String assertMsg) {
		if (isNull(operator)) {
			if (isNull(fallbackNoOp)) {
				notNull(operator, NoSuchOperatorException.class, assertMsg);
			} else {
				log.warn("Using default fallbackNoOp, caused by: {}", assertMsg);
				operator = fallbackNoOp;
			}
		}
		return operator;
	}

	/**
	 * Parse kind instance of kind name.
	 * 
	 * @param kindName
	 * @return
	 * @throws NoSuchOperatorException
	 */
	@SuppressWarnings("unchecked")
	private K parseKind(@NotNull String kindName) throws NoSuchOperatorException {
		for (Enum<?> kind : kindClass.getEnumConstants()) {
			if (kind.name().equalsIgnoreCase(kindName)) {
				return (K) kind;
			}
		}
		throw new NoSuchOperatorException(format("No such kind: %s of kind class: %s", kindName, kindClass));
	}

}