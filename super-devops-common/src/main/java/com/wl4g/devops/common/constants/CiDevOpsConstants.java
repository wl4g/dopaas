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
package com.wl4g.devops.common.constants;

/**
 * CI/CD DevOps constants.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-20 09:21:00
 */
public abstract class CiDevOpsConstants extends DevOpsConstants {

	// task-create
	public static final int TASK_STATUS_CREATE = 0;
	// task-start running
	public static final int TASK_STATUS_RUNNING = 1;
	// task-success
	public static final int TASK_STATUS_SUCCESS = 2;
	// task-fail
	public static final int TASK_STATUS_FAIL = 3;
	// task-timeout
	public  static final int TASK_STATUS_TIMEOUT = 4;
	// stop
	public static final int TASK_STATUS_STOP = 5;
	// task-part-success
	public  static final int TASK_STATUS_PART_SUCCESS = 6;
	// stoping
	public static final int TASK_STATUS_STOPING = 7;


	// Auto trigger
	public static final int TASK_TYPE_TRIGGER = 1;
	// Manual trigger
	public static final int TASK_TYPE_MANUAL = 2;
	// Roll-back trigger
	public static final int TASK_TYPE_ROLLBACK = 3;
	// Auto trigger
	public static final int TASK_TYPE_TIMMING = 4;

	// TaskHistory Lock Status -- Lock
	public static final int TASK_LOCK_STATUS_LOCK = 1;
	// TaskHistory Lock Status -- unLock
	public static final int TASK_LOCK_STATUS_UNLOCK = 0;

	public static final int TASK_ENABLE_STATUS = 1;
	public static final int TASK_DISABLE_STATUS = 0;

	/**
	 * Pipeline dependencies locker.
	 */
	public static final String LOCK_DEPENDENCY_BUILD = "ci.pipeline.build_";

	/**
	 * Global timeout cleanup finalizer intervalMs.
	 */
	public static final String KEY_FINALIZER_INTERVALMS = "ci.timeoutCleanupFinalizer.intervalMs_";

	/**
	 * Log file start/end separation.
	 */
	public static final String LOG_FILE_START = "<[EOF]";
	public static final String LOG_FILE_END = "[EOF]>";

	// --- Codes analyzers. ---

	/** Analyzers API base URI path. */
	final public static String URL_ANALYZER_BASE_PATH = "/analyzer";

}