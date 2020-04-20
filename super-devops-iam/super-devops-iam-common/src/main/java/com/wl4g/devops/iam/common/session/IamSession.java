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
package com.wl4g.devops.iam.common.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.tool.common.log.SmartLogger;

import io.protostuff.Tag;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.StoppedSessionException;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

import static com.wl4g.devops.tool.common.collection.Collections2.safeMap;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.valueOf;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.shiro.subject.support.DefaultSubjectContext.PRINCIPALS_SESSION_KEY;

/**
 * IAM session implements {@link org.apache.shiro.session.mgt.SimpleSession}
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年12月7日
 * @see {@link SimpleSession}
 * @since
 */
public class IamSession implements ValidatingSession, Serializable {
	private static final long serialVersionUID = -8534902655046557168L;

	// Serialization reminder:
	// You _MUST_ change this number if you introduce a change to this class
	// that is NOT serialization backwards compatible. Serialization-compatible
	// changes do not require a change to this number. If you need to generate
	// a new number in this case, use the JDK's 'serialver' program to generate
	// it.
	private static final transient SmartLogger log = getLogger(IamSession.class);

	protected static final transient long MILLIS_PER_SECOND = 1000;
	protected static final transient long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
	protected static final transient long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

	// serialization bitmask fields. DO NOT CHANGE THE ORDER THEY ARE DECLARED!
	static int bitIndexCounter = 0;
	private static final transient int ID_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int START_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int STOP_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int LAST_ACCESS_TIME_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int TIMEOUT_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int EXPIRED_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int HOST_BIT_MASK = 1 << bitIndexCounter++;
	private static final transient int ATTRIBUTES_BIT_MASK = 1 << bitIndexCounter++;

	// ==============================================================
	// NOTICE:
	//
	// The following fields are marked as transient to avoid
	// double-serialization.
	// They are in fact serialized (even though 'transient' usually indicates
	// otherwise),
	// but they are serialized explicitly via the writeObject and readObject
	// implementations
	// in this class.
	//
	// If we didn't declare them as transient, the out.defaultWriteObject();
	// call in writeObject would
	// serialize all non-transient fields as well, effectively doubly
	// serializing the fields (also
	// doubling the serialization size).
	//
	// This finding, with discussion, was covered here:
	//
	// http://mail-archives.apache.org/mod_mbox/shiro-user/201109.mbox/%3C4E81BCBD.8060909@metaphysis.net%3E
	//
	// ==============================================================

	/** That session ID. */
	@Tag(value = 1, alias = "id")
	private Serializable id;

	/** That session create timestamp. */
	@Tag(value = 2, alias = "startTimestamp")
	private Date startTimestamp = new Date();

	/** That session destroy timestamp. */
	@Tag(value = 3, alias = "stopTimestamp")
	private Date stopTimestamp;

	/** That session expired. */
	@Tag(value = 4, alias = "lastAccessTime")
	private Date lastAccessTime;

	// Remove concrete reference to DefaultSessionManager
	@Tag(value = 5, alias = "timeout")
	private long timeout = DefaultSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;

	/** That session expired. */
	@Tag(value = 6, alias = "expired")
	private boolean expired;

	/** Remote client host. */
	@Tag(value = 7, alias = "host")
	private String host;

	/** Attributes properties. */
	@Tag(value = 8, alias = "attributes")
	private Map<Object, Object> attributes;

	/** Relations attributes {@link IamCache} */
	protected transient IamCache relationAttrsCache;

	public IamSession() {
	}

	public IamSession(Serializable id) {
		setId(id);
	}

	public IamSession(String host) {
		setHost(host);
	}

	@Override
	public Serializable getId() {
		return this.id;
	}

	public void setId(Serializable id) {
		// isTrue((isNull(id) || isBlank(id.toString())), "Iam session id must
		// not be empty.");
		this.id = id;
	}

	@Override
	public Date getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Date startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	/**
	 * Returns the time the session was stopped, or <tt>null</tt> if the session
	 * is still active.
	 * <p/>
	 * A session may become stopped under a number of conditions:
	 * <ul>
	 * <li>If the user logs out of the system, their current session is
	 * terminated (released).</li>
	 * <li>If the session expires</li>
	 * <li>The application explicitly calls {@link #stop()}</li>
	 * <li>If there is an internal system error and the session state can no
	 * longer accurately reflect the user's behavior, such in the case of a
	 * system crash</li>
	 * </ul>
	 * <p/>
	 * Once stopped, a session may no longer be used. It is locked from all
	 * further activity.
	 *
	 * @return The time the session was stopped, or <tt>null</tt> if the session
	 *         is still active.
	 */
	public Date getStopTimestamp() {
		return stopTimestamp;
	}

	public void setStopTimestamp(Date stopTimestamp) {
		this.stopTimestamp = stopTimestamp;
	}

	@Override
	public Date getLastAccessTime() {
		return isNull(lastAccessTime) ? getStartTimestamp() : lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	/**
	 * Returns true if this session has expired, false otherwise. If the session
	 * has expired, no further user interaction with the system may be done
	 * under this session.
	 *
	 * @return true if this session has expired, false otherwise.
	 */
	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<Object, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets relation attributes cache.
	 * 
	 * @return relationAttrsCache
	 */
	public IamCache getRelationAttrsCache() {
		notNullOf(relationAttrsCache, "relationAttrsCache");
		return this.relationAttrsCache;
	}

	/**
	 * Sets relation attributes cache.
	 * 
	 * @param relationAttrsCache
	 */
	public void setRelationAttrsCache(IamCache relationAttrsCache) {
		this.relationAttrsCache = relationAttrsCache;
	}

	/**
	 * Gets {@link IamSession} priary principal.
	 *
	 * @see {@link org.apache.shiro.subject.PrincipalCollection#getPrimaryPrincipal()}
	 * @return
	 */
	public Object getPrimaryPrincipal() {
		// Authentication principal.
		PrincipalCollection principals = (PrincipalCollection) getAttribute(PRINCIPALS_SESSION_KEY);
		if (nonNull(principals) && !principals.isEmpty()) {
			return principals.getPrimaryPrincipal();
		}
		return null;
	}

	@JsonIgnore
	@Override
	public void touch() {
		this.lastAccessTime = new Date();
	}

	@JsonIgnore
	@Override
	public void stop() {
		if (this.stopTimestamp == null) {
			this.stopTimestamp = new Date();
		}
	}

	@JsonIgnore
	protected boolean isStopped() {
		return getStopTimestamp() != null;
	}

	@JsonIgnore
	protected void expire() {
		stop();
		this.expired = true;
	}

	/**
	 * @since 0.9
	 */
	@JsonIgnore
	@Override
	public boolean isValid() {
		return !isStopped() && !isExpired();
	}

	/**
	 * Determines if this session is expired.
	 *
	 * @return true if the specified session has expired, false otherwise.
	 */
	@JsonIgnore
	protected boolean isTimedOut() {
		if (isExpired()) {
			return true;
		}

		long timeout = getTimeout();
		if (timeout >= 0l) {
			Date lastAccessTime = getLastAccessTime();
			if (lastAccessTime == null) {
				String msg = "session.lastAccessTime for session with id [" + getId() + "] is null.  This value must be set at "
						+ "least once, preferably at least upon instantiation.  Please check the " + getClass().getName()
						+ " implementation and ensure " + "this value will be set (perhaps in the constructor?)";
				throw new IllegalStateException(msg);
			}

			// Calculate at what time a session would have been last accessed
			// for it to be expired at this point. In other words, subtract
			// from the current time the amount of time that a session can
			// be inactive before expiring. If the session was last accessed
			// before this time, it is expired.
			long expireTimeMillis = System.currentTimeMillis() - timeout;
			Date expireTime = new Date(expireTimeMillis);
			return lastAccessTime.before(expireTime);
		} else {
			if (log.isTraceEnabled()) {
				log.trace("No timeout for session with id [" + getId() + "].  Session is not considered expired.");
			}
		}

		return false;
	}

	@JsonIgnore
	@Override
	public void validate() throws InvalidSessionException {
		// check for stopped:
		if (isStopped()) {
			// timestamp is set, so the session is considered stopped:
			String msg = "Session with id [" + getId() + "] has been "
					+ "explicitly stopped.  No further interaction under this session is " + "allowed.";
			throw new StoppedSessionException(msg);
		}

		// check for expiration
		if (isTimedOut()) {
			expire();

			// throw an exception explaining details of why it expired:
			Date lastAccessTime = getLastAccessTime();
			long timeout = getTimeout();

			Serializable sessionId = getId();

			DateFormat df = DateFormat.getInstance();
			String msg = "Session with id [" + sessionId + "] has expired. " + "Last access time: " + df.format(lastAccessTime)
					+ ".  Current time: " + df.format(new Date()) + ".  Session timeout is set to " + timeout / MILLIS_PER_SECOND
					+ " seconds (" + timeout / MILLIS_PER_MINUTE + " minutes)";
			if (log.isTraceEnabled()) {
				log.trace(msg);
			}
			throw new ExpiredSessionException(msg);
		}
	}

	@JsonIgnore
	private Map<Object, Object> getAttributesLazy() {
		Map<Object, Object> attributes = getAttributes();
		if (attributes == null) {
			attributes = new HashMap<Object, Object>();
			setAttributes(attributes);
		}
		return attributes;
	}

	@JsonIgnore
	@Override
	public Collection<Object> getAttributeKeys() throws InvalidSessionException {
		Map<Object, Object> attributes = getAttributes();
		if (isNull(attributes)) {
			return emptySet();
		}
		return attributes.keySet();
	}

	@JsonIgnore
	@Override
	public Object getAttribute(Object key) {
		if (isRelationAttrKey(key)) {
			return getRelationAttrsCache().getMapField((RelationAttrKey) key);
		}
		return safeMap(getAttributes()).get(key);
	}

	@JsonIgnore
	@Override
	public void setAttribute(Object key, Object value) {
		if (isNull(value)) {
			removeAttribute(key);
		} else {
			if (isRelationAttrKey(key)) {
				// Put relation attribute.
				getRelationAttrsCache().mapPut((RelationAttrKey) key, value);
			} else {
				getAttributesLazy().put(key, value);
			}
		}
	}

	@JsonIgnore
	@Override
	public Object removeAttribute(Object key) {
		Map<Object, Object> attributes = getAttributes();
		if (isNull(attributes)) {
			return null;
		} else {
			if (isRelationAttrKey(key)) {
				// Removing relation attribute.
				return getRelationAttrsCache().mapRemove(valueOf(key));
			} else {
				return attributes.remove(key);
			}
		}
	}

	/**
	 * Returns {@code true} if the specified argument is an {@code instanceof}
	 * {@code SimpleSession} and both {@link #getId() id}s are equal. If the
	 * argument is a {@code SimpleSession} and either 'this' or the argument
	 * does not yet have an ID assigned, the value of
	 * {@link #onEquals(SimpleSession) onEquals} is returned, which does a
	 * necessary attribute-based comparison when IDs are not available.
	 * <p/>
	 * Do your best to ensure {@code SimpleSession} instances receive an ID very
	 * early in their lifecycle to avoid the more expensive attributes-based
	 * comparison.
	 *
	 * @param obj
	 *            the object to compare with this one for equality.
	 * @return {@code true} if this object is equivalent to the specified
	 *         argument, {@code false} otherwise.
	 */
	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SimpleSession) {
			SimpleSession other = (SimpleSession) obj;
			Serializable thisId = getId();
			Serializable otherId = other.getId();
			if (thisId != null && otherId != null) {
				return thisId.equals(otherId);
			} else {
				// fall back to an attribute based comparison:
				return onEquals(other);
			}
		}
		return false;
	}

	/**
	 * Provides an attribute-based comparison (no ID comparison) - incurred
	 * <em>only</em> when 'this' or the session object being compared for
	 * equality do not have a session id.
	 *
	 * @param ss
	 *            the SimpleSession instance to compare for equality.
	 * @return true if all the attributes, except the id, are equal to this
	 *         object's attributes.
	 * @since 1.0
	 */
	@JsonIgnore
	protected boolean onEquals(SimpleSession ss) {
		return (getStartTimestamp() != null ? getStartTimestamp().equals(ss.getStartTimestamp()) : ss.getStartTimestamp() == null)
				&& (getStopTimestamp() != null ? getStopTimestamp().equals(ss.getStopTimestamp()) : ss.getStopTimestamp() == null)
				&& (getLastAccessTime() != null ? getLastAccessTime().equals(ss.getLastAccessTime())
						: ss.getLastAccessTime() == null)
				&& (getTimeout() == ss.getTimeout()) && (isExpired() == ss.isExpired())
				&& (getHost() != null ? getHost().equals(ss.getHost()) : ss.getHost() == null)
				&& (getAttributes() != null ? getAttributes().equals(ss.getAttributes()) : ss.getAttributes() == null);
	}

	/**
	 * Returns the hashCode. If the {@link #getId() id} is not {@code null}, its
	 * hashcode is returned immediately. If it is {@code null}, an
	 * attributes-based hashCode will be calculated and returned.
	 * <p/>
	 * Do your best to ensure {@code SimpleSession} instances receive an ID very
	 * early in their lifecycle to avoid the more expensive attributes-based
	 * calculation.
	 *
	 * @return this object's hashCode
	 * @since 1.0
	 */
	@JsonIgnore
	@Override
	public int hashCode() {
		Serializable id = getId();
		if (id != null) {
			return id.hashCode();
		}
		int hashCode = getStartTimestamp() != null ? getStartTimestamp().hashCode() : 0;
		hashCode = 31 * hashCode + (getStopTimestamp() != null ? getStopTimestamp().hashCode() : 0);
		hashCode = 31 * hashCode + (getLastAccessTime() != null ? getLastAccessTime().hashCode() : 0);
		hashCode = 31 * hashCode + Long.valueOf(Math.max(getTimeout(), 0)).hashCode();
		hashCode = 31 * hashCode + Boolean.valueOf(isExpired()).hashCode();
		hashCode = 31 * hashCode + (getHost() != null ? getHost().hashCode() : 0);
		hashCode = 31 * hashCode + (getAttributes() != null ? getAttributes().hashCode() : 0);
		return hashCode;
	}

	/**
	 * Returns the string representation of this SimpleSession, equal to
	 * <code>getClass().getName() + &quot;,id=&quot; + getId()</code>.
	 *
	 * @return the string representation of this SimpleSession, equal to
	 *         <code>getClass().getName() + &quot;,id=&quot; + getId()</code>.
	 * @since 1.0
	 */
	@JsonIgnore
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName()).append(",id=").append(getId());
		return sb.toString();
	}

	/**
	 * Serializes this object to the specified output stream for JDK
	 * Serialization.
	 *
	 * @param out
	 *            output stream used for Object serialization.
	 * @throws IOException
	 *             if any of this object's fields cannot be written to the
	 *             stream.
	 * @since 1.0
	 */
	@JsonIgnore
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		short alteredFieldsBitMask = getAlteredFieldsBitMask();
		out.writeShort(alteredFieldsBitMask);
		if (id != null) {
			out.writeObject(id);
		}
		if (startTimestamp != null) {
			out.writeObject(startTimestamp);
		}
		if (stopTimestamp != null) {
			out.writeObject(stopTimestamp);
		}
		if (lastAccessTime != null) {
			out.writeObject(lastAccessTime);
		}
		if (timeout != 0l) {
			out.writeLong(timeout);
		}
		if (expired) {
			out.writeBoolean(expired);
		}
		if (host != null) {
			out.writeUTF(host);
		}
		if (!CollectionUtils.isEmpty(attributes)) {
			out.writeObject(attributes);
		}
	}

	/**
	 * Reconstitutes this object based on the specified InputStream for JDK
	 * Serialization.
	 *
	 * @param in
	 *            the input stream to use for reading data to populate this
	 *            object.
	 * @throws IOException
	 *             if the input stream cannot be used.
	 * @throws ClassNotFoundException
	 *             if a required class needed for instantiation is not available
	 *             in the present JVM
	 * @since 1.0
	 */
	@JsonIgnore
	@SuppressWarnings({ "unchecked" })
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		short bitMask = in.readShort();

		if (isFieldPresent(bitMask, ID_BIT_MASK)) {
			this.id = (String) in.readObject();
		}
		if (isFieldPresent(bitMask, START_TIMESTAMP_BIT_MASK)) {
			this.startTimestamp = (Date) in.readObject();
		}
		if (isFieldPresent(bitMask, STOP_TIMESTAMP_BIT_MASK)) {
			this.stopTimestamp = (Date) in.readObject();
		}
		if (isFieldPresent(bitMask, LAST_ACCESS_TIME_BIT_MASK)) {
			this.lastAccessTime = (Date) in.readObject();
		}
		if (isFieldPresent(bitMask, TIMEOUT_BIT_MASK)) {
			this.timeout = in.readLong();
		}
		if (isFieldPresent(bitMask, EXPIRED_BIT_MASK)) {
			this.expired = in.readBoolean();
		}
		if (isFieldPresent(bitMask, HOST_BIT_MASK)) {
			this.host = in.readUTF();
		}
		if (isFieldPresent(bitMask, ATTRIBUTES_BIT_MASK)) {
			this.attributes = (Map<Object, Object>) in.readObject();
		}
	}

	/**
	 * Returns a bit mask used during serialization indicating which fields have
	 * been serialized. Fields that have been altered (not null and/or not
	 * retaining the class defaults) will be serialized and have 1 in their
	 * respective index, fields that are null and/or retain class default values
	 * have 0.
	 *
	 * @return a bit mask used during serialization indicating which fields have
	 *         been serialized.
	 * @since 1.0
	 */
	@JsonIgnore
	private short getAlteredFieldsBitMask() {
		int bitMask = 0;
		bitMask = id != null ? bitMask | ID_BIT_MASK : bitMask;
		bitMask = startTimestamp != null ? bitMask | START_TIMESTAMP_BIT_MASK : bitMask;
		bitMask = stopTimestamp != null ? bitMask | STOP_TIMESTAMP_BIT_MASK : bitMask;
		bitMask = lastAccessTime != null ? bitMask | LAST_ACCESS_TIME_BIT_MASK : bitMask;
		bitMask = timeout != 0l ? bitMask | TIMEOUT_BIT_MASK : bitMask;
		bitMask = expired ? bitMask | EXPIRED_BIT_MASK : bitMask;
		bitMask = host != null ? bitMask | HOST_BIT_MASK : bitMask;
		bitMask = !CollectionUtils.isEmpty(attributes) ? bitMask | ATTRIBUTES_BIT_MASK : bitMask;
		return (short) bitMask;
	}

	/**
	 * Returns {@code true} if the given {@code bitMask} argument indicates that
	 * the specified field has been serialized and therefore should be read
	 * during deserialization, {@code false} otherwise.
	 *
	 * @param bitMask
	 *            the aggregate bitmask for all fields that have been
	 *            serialized. Individual bits represent the fields that have
	 *            been serialized. A bit set to 1 means that corresponding field
	 *            has been serialized, 0 means it hasn't been serialized.
	 * @param fieldBitMask
	 *            the field bit mask constant identifying which bit to inspect
	 *            (corresponds to a class attribute).
	 * @return {@code true} if the given {@code bitMask} argument indicates that
	 *         the specified field has been serialized and therefore should be
	 *         read during deserialization, {@code false} otherwise.
	 * @since 1.0
	 */
	@JsonIgnore
	private static boolean isFieldPresent(short bitMask, int fieldBitMask) {
		return (bitMask & fieldBitMask) != 0;
	}

	/**
	 * Check is relation attribute key.
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isRelationAttrKey(Object key) {
		return !isNull(key) && (key instanceof RelationAttrKey);
	}

	/**
	 * Relation attribute key.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年4月16日
	 * @since
	 */
	public static class RelationAttrKey extends CacheKey {
		private static final long serialVersionUID = -999087974868579671L;

		public RelationAttrKey(Serializable key) {
			super(key);
		}

		public RelationAttrKey(byte[] key) {
			super(key);
		}

		public RelationAttrKey(Serializable key, Class<?> valueClass) {
			super(key, valueClass);
		}

		public RelationAttrKey(Serializable key, long expireMs) {
			super(key, expireMs);
		}

		public RelationAttrKey(Serializable key, int expireSec) {
			super(key, expireSec);
		}

	}

}