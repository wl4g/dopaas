/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.common.constants;

/**
 * CI/CD devops constants
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-20 09:21:00
 */
public abstract class CiDevOpsConstants extends DevOpsConstants {

	// tar
	public static final int TAR_TYPE_TAR = 1;
	// jar
	public static final int TAR_TYPE_JAR = 2;
	// other
	public static final int TAR_TYPE_OTHER = 3;

	// task-create
	public static final int TASK_STATUS_CREATE = 0;
	// task-start running
	public static final int TASK_STATUS_RUNNING = 1;
	// task-success
	public static final int TASK_STATUS_SUCCESS = 2;
	// task-fail
	public static final int TASK_STATUS_FAIL = 3;

	// Auto trigger
	public static final int TASK_TYPE_TRIGGER = 1;
	// Manual trigger
	public static final int TASK_TYPE_MANUAL = 2;
	// Rollback trigger
	public static final int TASK_TYPE_ROLLBACK = 3;

	public static final int TASK_ENABLE_STATUS = 1;
	public static final int TASK_DISABLE_STATUS = 0;

}
