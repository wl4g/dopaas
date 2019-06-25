package com.wl4g.devops.umc.opentsdb.client.bean.request;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/3/9 下午2:45
 * @Version: 1.0
 */
@SuppressWarnings("unused")
public class SuggestQuery {

	private Type type;

	private String q;

	private Integer max;

	public static enum Type {
		/***
		 * 所查询的元数据类型
		 */
		METRICS("metrics"), TAG_KEY("tagk"), TAG_VALUE("tagv");

		private String value;

		Type(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static Builder type(Type type) {
		return new Builder(type);
	}

	public static class Builder {

		private Type type;

		private String q;

		private Integer max;

		public Builder(Type type) {
			Objects.requireNonNull(type);
			this.type = type;
		}

		public SuggestQuery build() {
			SuggestQuery suggestQuery = new SuggestQuery();
			suggestQuery.type = this.type;

			if (StringUtils.isNoneBlank(q)) {
				suggestQuery.q = this.q;
			}
			if (max != null) {
				suggestQuery.max = this.max;
			}
			return suggestQuery;
		}

		public Builder q(String q) {
			this.q = q;
			return this;
		}

		public Builder max(Integer max) {
			this.max = max;
			return this;
		}

	}

}
