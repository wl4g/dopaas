package com.wl4g.devops.umc.derby;

import com.wl4g.devops.common.bean.umc.model.StatMetrics;
import com.wl4g.devops.umc.store.MetricStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * Derby foundation store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class DerbyMetricStore implements MetricStore {

	final JdbcTemplate jdbcTemplate;

	public DerbyMetricStore(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "JdbcTemplate must not be null");
		this.jdbcTemplate = jdbcTemplate;
	}


	@Override
	public boolean save(StatMetrics statMetrics) {
		return false;
	}
}
