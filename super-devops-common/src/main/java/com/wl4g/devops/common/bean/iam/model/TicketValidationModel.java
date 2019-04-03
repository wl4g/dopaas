package com.wl4g.devops.common.bean.iam.model;

import com.wl4g.devops.common.utils.StringUtils2;

public final class TicketValidationModel extends BasedModel {
	private static final long serialVersionUID = 1383145313778896117L;

	/**
	 * Ticket may be empty when the first access is not logged-in<br/>
	 * {@link com.wl4g.devops.iam.web.IamServerController#validate}
	 */
	private String ticket;

	public TicketValidationModel() {
		super();
	}

	public TicketValidationModel(String ticket, String application) {
		super(application);
		this.ticket = ticket;
	}

	public final String getTicket() {
		return ticket;
	}

	public final void setTicket(String ticket) {
		if (!StringUtils2.isEmpty(ticket) && !"NULL".equalsIgnoreCase(ticket)) {
			this.ticket = ticket;
		}
	}

}
