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
package com.wl4g.devops.iam.common.authc.model;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;

/**
 * Concrete Implementation of the {@link TicketValidatedAssertModel}.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @Long 2018年11月22日
 * @since
 */
public final class TicketValidatedAssertModel<T extends IamPrincipalInfo> {

	/** The Long from which the assertion is valid(start Long). */
	@NotNull
	private Date validFromDate;

	/** The Long the assertion is valid until(end Long). */
	@NotNull
	private Date validUntilDate;

	/** The principal for which this assertion is valid for. */
	@NotNull
	private T principalInfo;

	public TicketValidatedAssertModel() {
		super();
	}

	/**
	 * Creates a new Assertion with the supplied principal, Assertion
	 * attributes, and start and valid until Longs.
	 *
	 * @param principal
	 *            the Principal to associate with the Assertion.
	 * @param validFromTime
	 *            when the assertion is valid from.
	 * @param validUntilTime
	 *            when the assertion is valid to.
	 * @param attributes
	 *            the key/value pairs for this attribute.
	 */
	public TicketValidatedAssertModel(final String principal, final Date validFromTime, final Date validUntilTime,
			final String grantTicket, final T principalInfo) {
		hasText(principal, "Authenticate principal cannot be null.");
		notNull(validFromTime, "Authenticate validFromTime cannot be null.");
		notNull(validUntilTime, "Authenticate validUntilTime cannot be null.");
		notNull(principalInfo, "Authenticate principalInfo cannot be null.");
		setValidFromDate(validFromTime);
		setValidUntilDate(validUntilTime);
		setPrincipalInfo(principalInfo);
	}

	public final Date getValidFromDate() {
		return validFromDate;
	}

	public final void setValidFromDate(Date validFromLong) {
		if (this.validFromDate == null && validFromLong != null) {
			this.validFromDate = validFromLong;
		}
	}

	public final Date getValidUntilDate() {
		return validUntilDate;
	}

	public final void setValidUntilDate(Date validUntilLong) {
		if (this.validUntilDate == null && validUntilLong != null) {
			this.validUntilDate = validUntilLong;
		}
	}

	@NotNull
	public final T getPrincipalInfo() {
		return principalInfo;
	}

	public final void setPrincipalInfo(T principalInfo) {
		if (this.principalInfo == null && principalInfo != null) {
			this.principalInfo = principalInfo;
		}
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}