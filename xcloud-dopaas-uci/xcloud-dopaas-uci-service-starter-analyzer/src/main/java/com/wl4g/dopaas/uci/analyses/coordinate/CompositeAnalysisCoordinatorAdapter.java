/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.analyses.coordinate;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.wl4g.component.common.collection.OnceUnmodifiableMap;
import com.wl4g.dopaas.uci.analyses.model.AnalysingModel;
import com.wl4g.dopaas.uci.analyses.model.AnalysisQueryModel;
import com.wl4g.dopaas.uci.analyses.model.AnalysisResultModel;

/**
 * Composite codes analyzers adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public class CompositeAnalysisCoordinatorAdapter implements AnalysisCoordinator<AnalysingModel> {

    /**
     * Codes analyzers.
     */
    final protected Map<AnalyzerKind, AnalysisCoordinator<AnalysingModel>> registry = new OnceUnmodifiableMap<>(new HashMap<>());

    /**
     * Real delegate CodeAnalyzer.
     */
    final private ThreadLocal<AnalysisCoordinator<AnalysingModel>> delegate = new InheritableThreadLocal<>();

    public CompositeAnalysisCoordinatorAdapter(List<AnalysisCoordinator<AnalysingModel>> analyzers) {
        Assert.state(!isEmpty(analyzers), "CodeAnalyzers has at least one.");
        // Duplicate checks.
        Set<AnalyzerKind> kinds = new HashSet<>();
        analyzers.forEach(o -> {
            notNull(o.kind(), String.format("Vcs provider must not be empty for CodeAnalyzer %s", o));
            state(!kinds.contains(o.kind()), String.format("Repeated definition CodeAnalyzer with %s", o.kind()));
            kinds.add(o.kind());
        });
        // Register.
        this.registry.putAll(analyzers.stream().collect(toMap(AnalysisCoordinator::kind, oper -> oper)));
    }

    /**
     * Making the adaptation actually execute {@link AnalysisCoordinator}.
     * 
     * @param analyzer
     * @return
     */
    public AnalysisCoordinator<AnalysingModel> forAdapt(@NotNull AnalyzerKind analyzer) {
        AnalysisCoordinator<AnalysingModel> operator = registry.get(analyzer);
        notNull(operator, String.format("Unsupported CodeAnalyzer for '%s'", analyzer));
        delegate.set(operator);
        return operator;
    }

    /**
     * Making the adaptation actually execute {@link AnalysisCoordinator}.
     *
     * @param analyzer
     * @return
     */
    public AnalysisCoordinator<AnalysingModel> forAdapt(@NotNull Integer analyzer) {
        return forAdapt(AnalyzerKind.of(analyzer));
    }

    /**
     * Get adapted {@link AnalysisCoordinator}.
     * 
     * @param type
     * @return
     */
    private AnalysisCoordinator<AnalysingModel> getAdapted() {
        AnalysisCoordinator<AnalysingModel> analyzer = delegate.get();
        Assert.state(analyzer != null,
                "Not adapted to specify actual CodeAnalyzer, You must use adapted() to adapt before you can.");
        return analyzer;
    }

    @Override
    public void analyze(AnalysingModel model) throws Exception {
        getAdapted().analyze(model);
    }

    @Override
    public AnalysisResultModel getBugCollection(AnalysisQueryModel model) {
        return getAdapted().getBugCollection(model);
    }

}