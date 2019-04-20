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
package com.wl4g.devops.common.bean.iam.model;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

/**
 * Concrete Implementation of the {@link TicketAssertion}.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @Long 2018年11月22日
 * @since
 */
public final class TicketAssertion {

	/** The Long from which the assertion is valid(start Long). */
	@NotNull
	private Date validFromDate;

	/** The Long the assertion is valid until(end Long). */
	@NotNull
	private Date validUntilDate;

	/**
	 * Map of key/value pairs associated with this assertion. I.e.
	 * authentication type.
	 */
	private Map<String, Object> attributes = new HashMap<>(4);

	/** The principal for which this assertion is valid for. */
	@NotNull
	private IamPrincipal principal;

	public TicketAssertion() {
		super();
	}

	/**
	 * Creates a new Assertion with the supplied Principal.
	 *
	 * @param principal
	 *            the Principal to associate with the Assertion.
	 */
	public TicketAssertion(final IamPrincipal principal) {
		this(principal, null, null);
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
	public TicketAssertion(final IamPrincipal principal, final Date validFromTime, final Date validUntilTime) {
		this.setPrincipal(principal);
		this.setValidFromDate(validFromTime);
		this.setValidUntilDate(validUntilTime);
		Assert.notNull(this.getPrincipal(), "'principal' cannot be null.");
		Assert.notNull(this.getValidFromDate(), "'validFromLong' cannot be null.");
		Assert.notNull(this.getValidUntilDate(), "'validUntilTime' cannot be null.");
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

	public final Map<String, Object> getAttributes() {
		return attributes;
	}

	public final void setAttributes(Map<String, Object> attributes) {
		if ((this.attributes == null || this.attributes.isEmpty()) && attributes != null) {
			this.attributes = attributes;
		}
	}

	@NotNull
	public final IamPrincipal getPrincipal() {
		return principal;
	}

	public final void setPrincipal(IamPrincipal principal) {
		if (this.principal == null && principal != null) {
			this.principal = principal;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	/**
	 * Extension to the standard Java Principal that includes a way to retrieve
	 * proxy tickets for a particular user and attributes.
	 * <p>
	 * Developer's who don't want their code tied to CAS merely need to work
	 * with the Java Principal then. Working with the CAS-specific features
	 * requires knowledge of the AttributePrincipal class.
	 */
	public final static class IamPrincipal implements Principal, Serializable {
		/** Unique Id for Serialization */
		private static final long serialVersionUID = -1443182634624927187L;

		/** The unique identifier for this principal. */
		@NotBlank
		private String name;

		/** Map of key/value pairs about this principal. */
		private Map<String, Object> attributes = new HashMap<>(4);

		public IamPrincipal() {
			super();
		}

		public IamPrincipal(final String name) {
			this(name, null);
		}

		/**
		 * Constructs a new principal with the supplied name and attributes.
		 *
		 * @param name
		 *            the unique identifier for the principal.
		 * @param attributes
		 *            the key/value pairs for this principal.
		 */
		public IamPrincipal(final String name, final Map<String, Object> attributes) {
			this.setName(name);
			this.setAttributes(attributes);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			if (this.name == null && name != null) {
				this.name = name;
			}
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public void setAttributes(Map<String, Object> attributes) {
			if ((this.attributes == null || this.attributes.isEmpty()) && attributes != null) {
				this.attributes = attributes;
			}
		}

		@Override
		public String toString() {
			return "IamPrincipal [name=" + name + ", attributes=" + attributes + "]";
		}

	}

}