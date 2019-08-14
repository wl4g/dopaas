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
package com.wl4g.devops.umc.notify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.devops.common.constants.UMCDevOpsConstants;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.model.StatusMessage;

import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.model.StatusInfo;
import de.codecentric.boot.admin.notify.AbstractStatusChangeNotifier;

/**
 * Note that when multiple types of notifications are opened, the circular
 * execution notification will be executed synchronously. If the exception is
 * executed before, the latter will not be executed. <br/>
 * <b>Specific reference source: </b> <br/>
 * de.codecentric.boot.admin.notify.AbstractEventNotifier.notify() <br/>
 * de.codecentric.boot.admin.notify.CompositeNotifier.doNotify() <br/>
 * <br/>
 * Reference: http://www.gdtarena.com/gdkc/javacxy/13554.html <br/>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月28日
 * @since
 */
public abstract class AbstractAdvancedNotifier extends AbstractStatusChangeNotifier {
	final protected Logger logger = LoggerFactory.getLogger(getClass());
	final private static String DEFAULT_APPINFO = "#{application.name}-#{application.id}";
	final private static String DEFAULT_APPHEALTHURL = "#{application.healthUrl}";
	final private static String DEFAULT_FROMSTATUS = "#{from.status}";
	final private static String DEFAULT_TOSTATUS = "#{to.status}";
	final private SpelExpressionParser parser = new SpelExpressionParser();
	final private ObjectMapper mapper = new ObjectMapper();
	final public static String INFO_PREFIX = "sba_event_";
	private Expression appInfo;
	private Expression healthUrl;
	private Expression fromStatus;
	private Expression toStatus;

	private String subject = "Server Healthy";
	private String fromName = "DevOps Monitor";
	private String[] phoneTo;
	private String[] mailTo;
	private int expireSec = 3 * 24 * 60 * 60; // State message save time.
	private String hrefUrl;

	@Autowired
	protected JedisService jedisService;

	public AbstractAdvancedNotifier() {
		this.appInfo = this.parser.parseExpression(DEFAULT_APPINFO, ParserContext.TEMPLATE_EXPRESSION);
		this.healthUrl = this.parser.parseExpression(DEFAULT_APPHEALTHURL, ParserContext.TEMPLATE_EXPRESSION);
		this.fromStatus = this.parser.parseExpression(DEFAULT_FROMSTATUS, ParserContext.TEMPLATE_EXPRESSION);
		this.toStatus = this.parser.parseExpression(DEFAULT_TOSTATUS, ParserContext.TEMPLATE_EXPRESSION);
	}

	@Override
	protected void doNotify(ClientApplicationEvent e) throws Exception {
		if (logger.isInfoEnabled())
			logger.info("Application event. {}", e.getApplication());

		StatusInfo info = e.getApplication().getStatusInfo();
		// Get info.
		List<String> mailTo = Arrays.asList(getMailTo());
		List<String> phoneTo = Arrays.asList(getPhoneTo());
		String appInfo = this.getAppInfoText(e);
		String healthUrl = this.getHealthUrlText(e);
		String fStatus = this.getFromStatusText(e);
		String tStatus = this.getToStatusText(e);
		String msgId = UUID.randomUUID().toString().replaceAll("-", "").substring(16, 24);
		String detailsUrl = getHrefUrl() + UMCDevOpsConstants.URI_ADMIN_HOME + msgId;

		// Save StatusMessage to cache.
		StatusMessage msg = StatusMessage.wrap(appInfo, healthUrl, fStatus, tStatus, info.getTimestamp(), mailTo, phoneTo,
				detailsUrl, msgId, info);
		String msgStr = this.mapper.writeValueAsString(msg);
		this.jedisService.set((INFO_PREFIX + msgId), msgStr, this.getExpireSec());

		if (logger.isInfoEnabled())
			logger.info("Notifier status message. {}", msgStr);

		// Notifier processing.
		this.doNotify(msg);
	}

	protected abstract void doNotify(StatusMessage status);

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public void setPhoneTo(String[] phoneTo) {
		this.phoneTo = phoneTo;
	}

	public String[] getPhoneTo() {
		return this.phoneTo;
	}

	public String[] getMailTo() {
		return mailTo;
	}

	public void setMailTo(String[] mailTo) {
		this.mailTo = mailTo;
	}

	public int getExpireSec() {
		return expireSec;
	}

	public void setExpireSec(int expireSec) {
		this.expireSec = expireSec;
	}

	public String getHrefUrl() {
		return hrefUrl;
	}

	// @Value("http://#{T(java.net.InetAddress).getLocalHost().getHostName()}:${server.port}")
	public void setHrefUrl(String serverUrl) {
		this.hrefUrl = serverUrl;
	}

	private String getAppInfoText(ClientApplicationEvent event) {
		return this.appInfo.getValue(event, String.class);
	}

	private String getHealthUrlText(ClientApplicationEvent event) {
		return this.healthUrl.getValue(event, String.class);
	}

	private String getFromStatusText(ClientApplicationEvent event) {
		return this.fromStatus.getValue(event, String.class);
	}

	private String getToStatusText(ClientApplicationEvent event) {
		return this.toStatus.getValue(event, String.class);
	}

	static {
		// To prevent decompile.
		new HashMap<>().forEach((k, v) -> {
		});
	}

}