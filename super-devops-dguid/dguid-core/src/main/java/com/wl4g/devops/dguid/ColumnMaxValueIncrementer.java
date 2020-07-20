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
package com.wl4g.devops.dguid;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.wl4g.devops.dguid.leaf.DefaultLeafIdSegmentHandler;

/**
 * Spring 增量id实现(基于Segment策略)
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class ColumnMaxValueIncrementer extends DefaultLeafIdSegmentHandler implements DataFieldMaxValueIncrementer {

	/**
	 * 填充长度
	 */
	protected int paddingLength = 8;

	public ColumnMaxValueIncrementer(JdbcTemplate jdbcTemplate, String bizTag) {
		super(jdbcTemplate, bizTag);
	}

	@Override
	public int nextIntValue() throws DataAccessException {
		return getId().intValue();
	}

	@Override
	public long nextLongValue() throws DataAccessException {
		return getId();
	}

	@Override
	public String nextStringValue() throws DataAccessException {
		String s = Long.toString(getId());
		int len = s.length();
		if (len < this.paddingLength) {
			StringBuilder sb = new StringBuilder(this.paddingLength);
			for (int i = 0; i < this.paddingLength - len; i++) {
				sb.append('0');
			}
			sb.append(s);
			s = sb.toString();
		}
		return s;
	}

	public void setPaddingLength(int paddingLength) {
		this.paddingLength = paddingLength;
	}

	public int getPaddingLength() {
		return this.paddingLength;
	}
}