package com.wl4g.devops.common.bean.iam.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.CollectionUtils;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

/**
 * Session validation assertion.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @Long 2018年11月22日
 * @since
 */
public final class SessionValidationAssertion extends BasedModel {

	private static final long serialVersionUID = 5483716885382988025L;

	@NotEmpty
	private List<String> tickets = new ArrayList<>();

	public SessionValidationAssertion() {
	}

	public SessionValidationAssertion(String application) {
		super(application);
	}

	public List<String> getTickets() {
		return tickets;
	}

	public void setTickets(List<String> tickets) {
		if (!CollectionUtils.isEmpty(tickets)) {
			this.tickets.addAll(tickets);
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}
