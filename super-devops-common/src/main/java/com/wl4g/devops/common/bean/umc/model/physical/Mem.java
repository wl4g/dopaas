package com.wl4g.devops.common.bean.umc.model.physical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;

/**
 * @author vjay
 * @date 2019-06-11 17:23:00
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Mem extends PhysicalInfo {

	private static final long serialVersionUID = 4764434535991839564L;

	private MemInfo memInfo;

	public MemInfo getMemInfo() {
		return memInfo;
	}

	public void setMemInfo(MemInfo memInfo) {
		this.memInfo = memInfo;
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class MemInfo {
		private Long total;
		private Long free;
		private Double usedPercent;
		private Long used;
		private Long cached;
		private Long buffers;

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public Long getFree() {
			return free;
		}

		public void setFree(Long free) {
			this.free = free;
		}

		public Double getUsedPercent() {
			return usedPercent;
		}

		public void setUsedPercent(Double usedPercent) {
			this.usedPercent = usedPercent;
		}

		public Long getUsed() {
			return used;
		}

		public void setUsed(Long used) {
			this.used = used;
		}

		public Long getCached() {
			return cached;
		}

		public void setCached(Long cached) {
			this.cached = cached;
		}

		public Long getBuffers() {
			return buffers;
		}

		public void setBuffers(Long buffers) {
			this.buffers = buffers;
		}
	}

}
