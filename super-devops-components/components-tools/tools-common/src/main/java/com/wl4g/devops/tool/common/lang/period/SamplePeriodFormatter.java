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
package com.wl4g.devops.tool.common.lang.period;

import static java.lang.System.currentTimeMillis;
import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static java.lang.String.valueOf;

/**
 * {@link SamplePeriodFormatter}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月16日
 * @since
 */
public class SamplePeriodFormatter extends PeriodFormatterHolder {

	@Override
	public String formatHumanDate(long nowTime, long targetTime) {
		isTrue((nowTime - targetTime) >= 0, "Current time: %s must be greater than or equal to the target time: %s", nowTime,
				targetTime);

		StringBuffer sb = new StringBuffer();
		long diffSec = (nowTime - targetTime) / 1000;

		long sec = (diffSec >= 60 ? diffSec % 60 : diffSec);
		long min = (diffSec = (diffSec / 60)) >= 60 ? diffSec % 60 : diffSec;
		long hrs = (diffSec = (diffSec / 60)) >= 24 ? diffSec % 24 : diffSec;
		long days = (diffSec = (diffSec / 24)) >= 30 ? diffSec % 30 : diffSec;
		long months = (diffSec = (diffSec / 30)) >= 12 ? diffSec % 12 : diffSec;
		long years = (diffSec = (diffSec / 12));

		if (years > 0) {
			if (years == 1) {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.year")));
			} else {
				sb.append(" ".concat(valueOf(years).concat(" ")).concat(getLocalizedMessage("period.formatter.years")));
			}
			if (years <= 6 && months > 0) {
				if (months == 1) {
					sb.append(" ".concat(getLocalizedMessage("period.formatter.month")));
				} else {
					sb.append(" ".concat(valueOf(months)).concat(" ").concat(getLocalizedMessage("period.formatter.months")));
				}
			}
		} else if (months > 0) {
			if (months == 1) {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.month")));
			} else {
				sb.append(" ".concat(valueOf(months)).concat(" ").concat(getLocalizedMessage("period.formatter.months")));
			}
			if (months <= 6 && days > 0) {
				if (days == 1) {
					sb.append(" ".concat(getLocalizedMessage("period.formatter.day")));
				} else {
					sb.append(" ".concat(valueOf(days)).concat(" ").concat(getLocalizedMessage("period.formatter.days")));
				}
			}
		} else if (days > 0) {
			if (days == 1) {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.day")));
			} else {
				sb.append(" ".concat(valueOf(days).concat(" ")).concat(getLocalizedMessage("period.formatter.days")));
			}
			if (days <= 3 && hrs > 0) {
				if (hrs == 1) {
					sb.append(" ".concat(getLocalizedMessage("period.formatter.hour")));
				} else {
					sb.append(" ".concat(valueOf(hrs).concat(" ").concat(getLocalizedMessage("period.formatter.hours"))));
				}
			}
		} else if (hrs > 0) {
			if (hrs == 1) {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.hour")));
			} else {
				sb.append(" ".concat(valueOf(hrs).concat(" ")).concat(getLocalizedMessage("period.formatter.hours")));
			}
			if (min > 1) {
				sb.append(" ".concat(valueOf(min).concat(" ")).concat(getLocalizedMessage("period.formatter.minutes")));
			}
		} else if (min > 0) {
			if (min == 1) {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.minute")));
			} else {
				sb.append(" ".concat(valueOf(min)).concat(" ").concat(getLocalizedMessage("period.formatter.minutes")));
			}
			if (sec > 1) {
				sb.append(" ".concat(valueOf(sec)).concat(" ").concat(getLocalizedMessage("period.formatter.seconds")));
			}
		} else {
			if (sec <= 1) {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.about")).concat(" ")
						.concat(getLocalizedMessage("period.formatter.second")));
			} else {
				sb.append(" ".concat(getLocalizedMessage("period.formatter.about")).concat(" ")
						.concat(getLocalizedMessage("period.formatter.seconds")));
			}
		}
		sb.append(" ".concat(getLocalizedMessage("period.formatter.ago")));

		return cleanupDateEmptyString(sb.toString());
	}

	@Override
	public String formatHumanDate(long targetTime) {
		return formatHumanDate(currentTimeMillis(), targetTime);
	}

}
