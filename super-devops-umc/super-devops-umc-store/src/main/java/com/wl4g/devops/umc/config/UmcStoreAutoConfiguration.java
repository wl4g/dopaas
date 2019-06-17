package com.wl4g.devops.umc.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.umc.opentsdb.TsdbVirtualMetricStore;
import com.wl4g.devops.umc.derby.DerbyPhysicalMetricStore;
import com.wl4g.devops.umc.derby.DerbyVirtualMetricStore;
import com.wl4g.devops.umc.opentsdb.TsdbPhysicalMetricStore;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.store.VirtualMetricStore;
import com.wl4g.devops.umc.store.PhysicalMetricStore;
import com.wl4g.devops.umc.store.adapter.VirtualMetricStoreAdapter;
import com.wl4g.devops.umc.store.adapter.PhysicalMetricStoreAdapter;

/**
 * UMC store auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@Configuration
public class UmcStoreAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.umc.store")
	public StoreProperties storeProperties() {
		return new StoreProperties();
	}

	@Bean
	public OpenTsdbFactoryBean openTsdbFactoryBean() {
		return new OpenTsdbFactoryBean(storeProperties());
	}

	//
	// TSDB metric store's
	//

	@Bean
	public TsdbVirtualMetricStore tsdbVirtualMetricStore(OpenTSDBClient client) {
		return new TsdbVirtualMetricStore(client);
	}

	@Bean
	public TsdbPhysicalMetricStore tsdbPhysicalMetricStore(OpenTSDBClient client) {
		return new TsdbPhysicalMetricStore(client);
	}

	//
	// Derby metric store's
	//

	@Bean
	public DerbyVirtualMetricStore derbyVirtualMetricStore() {
		return new DerbyVirtualMetricStore(); // TODO
	}

	@Bean
	public DerbyPhysicalMetricStore derbyPhysicalMetricStore() {
		return new DerbyPhysicalMetricStore();// TODO
	}

	//
	// Metric store adapter's
	//

	@Bean
	public PhysicalMetricStoreAdapter physicalMetricStoreAdapter(List<PhysicalMetricStore> metricStores) {
		return new PhysicalMetricStoreAdapter(metricStores);
	}

	@Bean
	public VirtualMetricStoreAdapter virtualMetricStoreAdapter(List<VirtualMetricStore> metricStores) {
		return new VirtualMetricStoreAdapter(metricStores);
	}

}
