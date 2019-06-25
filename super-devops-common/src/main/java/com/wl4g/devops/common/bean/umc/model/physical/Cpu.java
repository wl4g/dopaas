package com.wl4g.devops.common.bean.umc.model.physical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wl4g.devops.common.bean.umc.model.Base;

/**
 * @author vjay
 * @date 2019-06-12 09:41:00
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cpu extends Base {

	private static final long serialVersionUID = 457088159628513585L;

	private Double[] cpu;

	public Double[] getCpu() {
		return cpu;
	}

	public void setCpu(Double[] cpu) {
		this.cpu = cpu;
	}
}
