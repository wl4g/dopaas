package com.wl4g.devops.umc.derby;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.model.Base;
import com.wl4g.devops.common.bean.umc.model.virtual.Docker;
import com.wl4g.devops.umc.store.VirtualMetricStore;

/**
 * Derby Virtual(docker) store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class DerbyVirtualMetricStore implements VirtualMetricStore {

	final JdbcTemplate jdbcTemplate;

	public DerbyVirtualMetricStore(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "JdbcTemplate must not be null");
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean save(Base baseTemple) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean save(Docker docker) {
		throw new UnsupportedOperationException();
	}

}
