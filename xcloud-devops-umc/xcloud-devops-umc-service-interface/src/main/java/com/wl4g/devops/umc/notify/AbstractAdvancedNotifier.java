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
package com.wl4g.devops.umc.notify;

import static com.wl4g.component.common.log.SmartLoggerFactory.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.core.constants.UMCDevOpsConstants;
import com.wl4g.component.support.redis.jedis.JedisService;
import com.wl4g.devops.umc.model.StatusMessage;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import reactor.core.publisher.Mono;

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

	final protected SmartLogger log = getLogger(getClass());

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

	public AbstractAdvancedNotifier(InstanceRepository repository) {
		super(repository);
		this.appInfo = parser.parseExpression(DEFAULT_APPINFO, ParserContext.TEMPLATE_EXPRESSION);
		this.healthUrl = parser.parseExpression(DEFAULT_APPHEALTHURL, ParserContext.TEMPLATE_EXPRESSION);
		this.fromStatus = parser.parseExpression(DEFAULT_FROMSTATUS, ParserContext.TEMPLATE_EXPRESSION);
		this.toStatus = parser.parseExpression(DEFAULT_TOSTATUS, ParserContext.TEMPLATE_EXPRESSION);
	}

	@Override
	protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
		if (log.isInfoEnabled())
			log.info("Application event. {}", instance.getId());

		StatusInfo info = instance.getStatusInfo();
		// Get info.
		List<String> mailTo = Arrays.asList(getMailTo());
		List<String> phoneTo = Arrays.asList(getPhoneTo());
		String appInfo = getAppInfoText(event, instance);
		String healthUrl = getHealthUrlText(event, instance);
		String fStatus = getFromStatusText(event, instance);
		String tStatus = getToStatusText(event, instance);
		String msgId = UUID.randomUUID().toString().replaceAll("-", "").substring(16, 24);
		String detailsUrl = getHrefUrl() + UMCDevOpsConstants.URI_ADMIN_HOME + msgId;

		// Save StatusMessage to cache.
		StatusMessage msg = StatusMessage.wrap(appInfo, healthUrl, fStatus, tStatus, event.getTimestamp().toEpochMilli(), mailTo,
				phoneTo, detailsUrl, msgId, info);
		String msgStr = JacksonUtils.toJSONString(msg);
		jedisService.set((INFO_PREFIX + msgId), msgStr, getExpireSec());
		log.info("Notifier status message. {}", msgStr);

		// Notifier processing.
		doNotify(msg);

		return null;
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

	private String getAppInfoText(InstanceEvent event, Instance instance) {
		return this.appInfo.getValue(event, String.class);
	}

	private String getHealthUrlText(InstanceEvent event, Instance instance) {
		return this.healthUrl.getValue(event, String.class);
	}

	private String getFromStatusText(InstanceEvent event, Instance instance) {
		return this.fromStatus.getValue(event, String.class);
	}

	private String getToStatusText(InstanceEvent event, Instance instance) {
		return this.toStatus.getValue(event, String.class);
	}

	final private static String DEFAULT_APPINFO = "#{application.name}-#{application.id}";
	final private static String DEFAULT_APPHEALTHURL = "#{application.healthUrl}";
	final private static String DEFAULT_FROMSTATUS = "#{from.status}";
	final private static String DEFAULT_TOSTATUS = "#{to.status}";
	final public static String INFO_PREFIX = "sba_event_";
	final private static SpelExpressionParser parser = new SpelExpressionParser();

}