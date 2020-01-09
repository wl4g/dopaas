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

import com.wl4g.devops.tool.common.collection.RegisteredUnmodifiableMap;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import org.slf4j.Logger;
import org.springframework.core.ResolvableType;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Composite generic operator adapter.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2019年11月1日
 * @since
 */
public abstract class GenericOperatorAdapter<K extends Enum<?>, O extends Operator<K>> implements Operator<K> {
	final protected Logger log = getLogger(getClass());

	/**
	 * Generic registrar of operator alias names.
	 */
	final protected Map<K, O> operatorAliasRegistry = new RegisteredUnmodifiableMap<>(new HashMap<>());

	/**
	 * Generic registrar of operator classes.
	 */
	final protected Map<Class<?>, O> operatorClassRegistry = new RegisteredUnmodifiableMap<>(new HashMap<>());

	/**
	 * Real delegate {@link O}
	 */
	final private ThreadLocal<O> delegate = new InheritableThreadLocal<>();

	/**
	 * Kind type class of operator provider.
	 */
	final Class<? extends Enum<?>> kindClass;

	@SuppressWarnings("unchecked")
	public GenericOperatorAdapter(List<O> operators) {
		state(!isEmpty(operators), "Vcs operators has at least one.");
		// Duplicate checks.
		Set<K> kinds = new HashSet<>();
		operators.forEach(o -> {
			notNull(o.kind(), String.format("Provider kind can't empty, operator: %s", o));
			state(!kinds.contains(o.kind()), String.format("Repeated definition operator with kind: %s", o.kind()));
			kinds.add(o.kind());
		});
		// Register of kind aliases.
		this.operatorAliasRegistry.putAll(operators.stream().collect(toMap(O::kind, o -> o)));
		// Register of kind classes.
		this.operatorClassRegistry.putAll(operators.stream().collect(toMap(O::getClass, o -> o)));

		// Resolving real Kind class.
		ResolvableType resolveType = ResolvableType.forClass(getClass());
		this.kindClass = (Class<? extends Enum<?>>) resolveType.getSuperType().getGeneric(0).resolve();
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
	 */
	@SuppressWarnings("unchecked")
	public <T> T forAdapt(@NotNull Class<T> operatorClass) {
		O operator = operatorClassRegistry.get(operatorClass);
		notNull(operator, String.format("No such operator bean instance of class: '%s'", operatorClass));
		delegate.set(operator);
		return (T) operator;
	}

	/**
	 * Making the adaptation actually execute {@link O}.
	 * 
	 * @param vcs
	 * @return
	 */
	public O forAdapt(@NotNull K k) {
		return forAdapt(k.name());
	}

	/**
	 * Making the adaptation actually execute {@link O}.
	 *
	 * @param P
	 * @return
	 */
	public O forAdapt(@NotNull String kindName) {
		K kind = getParseKind(kindName);
		O operator = operatorAliasRegistry.get(kind);
		notNull(operator, String.format("No such operator bean instance for kind name: '%s'", kind));
		delegate.set(operator);
		return operator;
	}

	/**
	 * Get adapted {@link O}.
	 * 
	 * @param type
	 * @return
	 */
	protected O getAdapted() {
		O operator = delegate.get();
		state(nonNull(operator), String.format(
				"No such to the specific operator(kind class: %s). Please configure the operator instance before calling the specific function method",
				kindClass));
		return operator;
	}

	/**
	 * Parse kind instance of kind name.
	 * 
	 * @param kindName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private K getParseKind(@NotNull String kindName) {
		for (Enum<?> kind : kindClass.getEnumConstants()) {
			if (kind.name().equalsIgnoreCase(kindName)) {
				return (K) kind;
			}
		}
		throw new IllegalArgumentException(String.format("No such kind: %s of kind class: %s", kindName, kindClass));
	}

}