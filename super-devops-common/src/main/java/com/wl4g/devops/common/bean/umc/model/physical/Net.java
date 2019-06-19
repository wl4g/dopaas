package com.wl4g.devops.common.bean.umc.model.physical;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;

/**
 * @author vjay
 * @date 2019-06-12 09:41:00
 */
public class Net extends PhysicalInfo {

	private static final long serialVersionUID = 7624894410555358785L;

	private NetInfo[] netInfos;

	public NetInfo[] getNetInfos() {
		return netInfos;
	}

	public void setNetInfos(NetInfo[] netInfos) {
		this.netInfos = netInfos;
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class NetInfo {
		private Integer port;
		private Integer up;
		private Integer down;
		private Integer count;
		private Integer estab;
		private Integer closeWait;
		private Integer timeWait;
		private Integer close;
		private Integer listen;
		private Integer closing;

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public Integer getUp() {
			return up;
		}

		public void setUp(Integer up) {
			this.up = up;
		}

		public Integer getDown() {
			return down;
		}

		public void setDown(Integer down) {
			this.down = down;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Integer getEstab() {
			return estab;
		}

		public void setEstab(Integer estab) {
			this.estab = estab;
		}

		public Integer getCloseWait() {
			return closeWait;
		}

		public void setCloseWait(Integer closeWait) {
			this.closeWait = closeWait;
		}

		public Integer getTimeWait() {
			return timeWait;
		}

		public void setTimeWait(Integer timeWait) {
			this.timeWait = timeWait;
		}

		public Integer getClose() {
			return close;
		}

		public void setClose(Integer close) {
			this.close = close;
		}

		public Integer getListen() {
			return listen;
		}

		public void setListen(Integer listen) {
			this.listen = listen;
		}

		public Integer getClosing() {
			return closing;
		}

		public void setClosing(Integer closing) {
			this.closing = closing;
		}
	}

}
