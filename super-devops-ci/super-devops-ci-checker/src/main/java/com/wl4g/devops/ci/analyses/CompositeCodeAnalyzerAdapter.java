///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.devops.ci.analyses;
//
//import com.wl4g.devops.common.bean.ci.Vcs;
//import com.wl4g.devops.common.utils.lang.OnceModifiableMap;
//import org.eclipse.jgit.api.PullResult;
//import org.eclipse.jgit.lib.Ref;
//import org.springframework.util.Assert;
//
//import javax.validation.constraints.NotNull;
//import java.io.IOException;
//import java.util.*;
//
//import static java.util.stream.Collectors.toMap;
//import static org.springframework.util.Assert.notNull;
//import static org.springframework.util.Assert.state;
//import static org.springframework.util.CollectionUtils.isEmpty;
//
///**
// * Composite VCS operator adapter.
// * 
// * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
// * @version v1.0 2019年11月1日
// * @since
// */
//public class CompositeCodeAnalyzerAdapter implements CodeAnalyzer {
//
//	/**
//	 * Vcs operator.
//	 */
//	final protected Map<VcsProvider, CodeAnalyzer> registry = new OnceModifiableMap<>(new HashMap<>());
//
//	/**
//	 * Real delegate CodeAnalyzer.
//	 */
//	final private ThreadLocal<CodeAnalyzer> delegate = new InheritableThreadLocal<>();
//
//	public CompositeCodeAnalyzerAdapter(List<CodeAnalyzer> operators) {
//		Assert.state(!isEmpty(operators), "Vcs operators has at least one.");
//		// Duplicate checks.
//		Set<VcsProvider> vcsProviders = new HashSet<>();
//		operators.forEach(o -> {
//			notNull(o.vcsProvider(), String.format("Vcs provider must not be empty for CodeAnalyzer %s", o));
//			state(!vcsProviders.contains(o.vcsProvider()),
//					String.format("Repeated definition CodeAnalyzer with %s", o.vcsProvider()));
//			vcsProviders.add(o.vcsProvider());
//		});
//		// Register.
//		this.registry.putAll(operators.stream().collect(toMap(CodeAnalyzer::vcsProvider, oper -> oper)));
//	}
//
//	/**
//	 * Making the adaptation actually execute {@link Vcs}.
//	 * 
//	 * @param vcs
//	 * @return
//	 */
//	public CodeAnalyzer forAdapt(@NotNull Vcs vcs) {
//		return forAdapt(vcs.getProvider());
//	}
//
//	/**
//	 * Making the adaptation actually execute {@link CodeAnalyzer}.
//	 * 
//	 * @param vcsProvider
//	 * @return
//	 */
//	public CodeAnalyzer forAdapt(@NotNull VcsProvider vcsProvider) {
//		CodeAnalyzer operator = registry.get(vcsProvider);
//		notNull(operator, String.format("Unsupported CodeAnalyzer for '%s'", vcsProvider));
//		delegate.set(operator);
//		return operator;
//	}
//
//	/**
//	 * Making the adaptation actually execute {@link CodeAnalyzer}.
//	 *
//	 * @param vcsProvider
//	 * @return
//	 */
//	public CodeAnalyzer forAdapt(@NotNull Integer vcsProvider) {
//		return forAdapt(VcsProvider.of(vcsProvider));
//	}
//
//	/**
//	 * Get adapted {@link CodeAnalyzer}.
//	 * 
//	 * @param type
//	 * @return
//	 */
//	private CodeAnalyzer getAdapted() {
//		CodeAnalyzer operator = delegate.get();
//		Assert.state(operator != null,
//				"Not adapted to specify actual CodeAnalyzer, You must use adapted() to adapt before you can.");
//		return operator;
//	}
//
//}
