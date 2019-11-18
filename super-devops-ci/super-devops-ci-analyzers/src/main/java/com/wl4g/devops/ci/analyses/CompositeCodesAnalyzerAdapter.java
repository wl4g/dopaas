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
package com.wl4g.devops.ci.analyses;

import com.wl4g.devops.ci.analyses.model.SpotbugsProjectModel;
import com.wl4g.devops.common.utils.lang.OnceModifiableMap;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Composite codes analyzers adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public class CompositeCodesAnalyzerAdapter implements CodesAnalyzer {

	/**
	 * Codes analyzers.
	 */
	final protected Map<AnalyzerType, CodesAnalyzer> registry = new OnceModifiableMap<>(new HashMap<>());

	/**
	 * Real delegate CodeAnalyzer.
	 */
	final private ThreadLocal<CodesAnalyzer> delegate = new InheritableThreadLocal<>();

	public CompositeCodesAnalyzerAdapter(List<CodesAnalyzer> operators) {
		Assert.state(!isEmpty(operators), "Vcs operators has at least one.");
		// Duplicate checks.
		Set<AnalyzerType> analyzers = new HashSet<>();
		operators.forEach(o -> {
			notNull(o.provider(), String.format("Vcs provider must not be empty for CodeAnalyzer %s", o));
			state(!analyzers.contains(o.provider()), String.format("Repeated definition CodeAnalyzer with %s", o.provider()));
			analyzers.add(o.provider());
		});
		// Register.
		this.registry.putAll(operators.stream().collect(toMap(CodesAnalyzer::provider, oper -> oper)));
	}

	@Override
	public void analyze(SpotbugsProjectModel model) throws Exception {
		getAdapted().analyze(model);
	}

	/**
	 * Making the adaptation actually execute {@link CodesAnalyzer}.
	 * 
	 * @param analyzer
	 * @return
	 */
	public CodesAnalyzer forAdapt(@NotNull AnalyzerType analyzer) {
		CodesAnalyzer operator = registry.get(analyzer);
		notNull(operator, String.format("Unsupported CodeAnalyzer for '%s'", analyzer));
		delegate.set(operator);
		return operator;
	}

	/**
	 * Making the adaptation actually execute {@link CodesAnalyzer}.
	 *
	 * @param analyzer
	 * @return
	 */
	public CodesAnalyzer forAdapt(@NotNull Integer analyzer) {
		return forAdapt(AnalyzerType.of(analyzer));
	}

	/**
	 * Get adapted {@link CodesAnalyzer}.
	 * 
	 * @param type
	 * @return
	 */
	private CodesAnalyzer getAdapted() {
		CodesAnalyzer operator = delegate.get();
		Assert.state(operator != null,
				"Not adapted to specify actual CodeAnalyzer, You must use adapted() to adapt before you can.");
		return operator;
	}

}
