/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.lite.ui.service;

import org.apache.shardingsphere.elasticjob.lite.ui.dto.request.FindJobExecutionEventsRequest;
import org.apache.shardingsphere.elasticjob.lite.ui.dto.request.FindJobStatusTraceEventsRequest;
import org.apache.shardingsphere.elasticjob.tracing.event.JobExecutionEvent;
import org.apache.shardingsphere.elasticjob.tracing.event.JobStatusTraceEvent;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Event trace history service.
 */
public interface EventTraceHistoryService {
    
    /**
     * Find job execution events.
     *
     * @param findJobExecutionEventsRequest query params
     * @return job execution events
     */
    Page<JobExecutionEvent> findJobExecutionEvents(FindJobExecutionEventsRequest findJobExecutionEventsRequest);
    
    /**
     * Find job names with specific prefix.
     *
     * @param jobNamePrefix job name prefix
     * @return matched job names
     */
    List<String> findJobNamesInExecutionLog(String jobNamePrefix);
    
    /**
     * Find ip addresses with specific prefix.
     *
     * @param ipPrefix ip prefix
     * @return matched ip addresses
     */
    List<String> findIpInExecutionLog(String ipPrefix);
    
    /**
     * Find job status trace events.
     *
     * @param findJobStatusTraceEventsRequest query params
     * @return job status trace events
     */
    Page<JobStatusTraceEvent> findJobStatusTraceEvents(FindJobStatusTraceEventsRequest findJobStatusTraceEventsRequest);
    
    /**
     * Find job names with specific prefix in status trace log.
     *
     * @param jobNamePrefix job name prefix
     * @return matched job names
     */
    List<String> findJobNamesInStatusTraceLog(String jobNamePrefix);
}
