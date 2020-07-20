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
package com.wl4g.devops.components.tools.common.lang.period;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Locale.*;
import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * {@link JodaPeriodFormatter}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月16日
 * @since
 */
public class JodaPeriodFormatter extends PeriodFormatterHolder {

	@Override
	public String formatHumanDate(long nowTime, long targetTime) {
		isTrue((nowTime - targetTime) >= 0, "Current time: %s must be greater than or equal to the target time: %s", nowTime,
				targetTime);

		Period period = new Period(new DateTime(targetTime), new DateTime(nowTime));
		String elapsed = getPeriodFormatter(CANADA).print(period);
		return cleanupDateEmptyString(elapsed.concat(getLocalizedMessage("period.formatter.ago")));
	}

	@Override
	public String formatHumanDate(long targetTime) {
		return formatHumanDate(currentTimeMillis(), targetTime);
	}

	/**
	 * Gets create period {@link PeriodFormatter} instances.
	 * 
	 * @param locale
	 * @return
	 */
	private synchronized static PeriodFormatter getPeriodFormatter(Locale locale) {
		notNullOf(locale, "locale");

		PeriodFormatter formatter = localizedPeriodFormatters.get(locale);
		if (isNull(formatter)) {
			PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
			builder.appendYears().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.year")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.years").concat(" ")));

			builder.appendMonths().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.month")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.months").concat(" ")));

			builder.appendWeeks().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.week")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.weeks").concat(" ")));

			builder.appendDays().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.day")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.days").concat(" ")));

			builder.appendHours().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.hour")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.hours").concat(" ")));

			builder.appendMinutes().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.minute")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.minutes").concat(" ")));

			builder.appendSeconds().appendSuffix(" ".concat(getLocalizedMessage("period.formatter.second")).concat(" "),
					" ".concat(getLocalizedMessage("period.formatter.seconds").concat(" ")));

			formatter = builder.printZeroNever().toFormatter();
			localizedPeriodFormatters.put(locale, formatter);
		}

		return formatter;
	}

	/**
	 * Default {@link PeriodFormatter} instances.
	 */
	final private static Map<Locale, PeriodFormatter> localizedPeriodFormatters = new HashMap<>();

}