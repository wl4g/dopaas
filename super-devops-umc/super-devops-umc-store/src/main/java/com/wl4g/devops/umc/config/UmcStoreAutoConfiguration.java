package com.wl4g.devops.umc.config;

import static org.apache.commons.lang3.SystemUtils.USER_HOME;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.wl4g.devops.umc.annotation.EnableOpenTsdbStore;
import com.wl4g.devops.umc.derby.DerbyPhysicalMetricStore;
import com.wl4g.devops.umc.derby.DerbyVirtualMetricStore;
import com.wl4g.devops.umc.opentsdb.TsdbKafkaMetricStore;
import com.wl4g.devops.umc.opentsdb.TsdbPhysicalMetricStore;
import com.wl4g.devops.umc.opentsdb.TsdbRedisMetricStore;
import com.wl4g.devops.umc.opentsdb.TsdbVirtualMetricStore;
import com.wl4g.devops.umc.opentsdb.TsdbZookeeperMetricStore;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;

/**
 * UMC store auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@Configuration
public class UmcStoreAutoConfiguration {

	final public static String KEY_STORE_PREFIX = "spring.cloud.devops.umc.store";
	final public static String KEY_STORE_OPENTSDB_PREFIX = KEY_STORE_PREFIX + ".opentsdb";

	@Bean
	@ConfigurationProperties(prefix = KEY_STORE_PREFIX)
	public StoreProperties storeProperties() {
		return new StoreProperties();
	}

	@Bean
	@EnableOpenTsdbStore
	public OpenTsdbFactoryBean openTsdbFactoryBean() {
		return new OpenTsdbFactoryBean(storeProperties());
	}

	//
	// TSDB metric store's
	//

	@Bean
	@EnableOpenTsdbStore
	public TsdbPhysicalMetricStore tsdbPhysicalMetricStore(OpenTSDBClient client) {
		return new TsdbPhysicalMetricStore(client);
	}

	@Bean
	@EnableOpenTsdbStore
	public TsdbVirtualMetricStore tsdbVirtualMetricStore(OpenTSDBClient client) {
		return new TsdbVirtualMetricStore(client);
	}

	@Bean
	@EnableOpenTsdbStore
	public TsdbRedisMetricStore tsdbRedisMetricStore(OpenTSDBClient client) {
		return new TsdbRedisMetricStore(client);
	}

	@Bean
	@EnableOpenTsdbStore
	public TsdbZookeeperMetricStore tsdbZookeeperMetricStore(OpenTSDBClient client) {
		return new TsdbZookeeperMetricStore(client);
	}

	@Bean
	@EnableOpenTsdbStore
	public TsdbKafkaMetricStore tsdbKafkaMetricStore(OpenTSDBClient client) {
		return new TsdbKafkaMetricStore(client);
	}

	//
	// Derby metric store's
	//

	/**
	 * <b>Quick Start:</b><a href=
	 * "https://db.apache.org/derby/papers/DerbyTut/ij_intro.html#ij_start">https://db.apache.org/derby/papers/DerbyTut/ij_intro.html#ij_start</a>
	 * </br>
	 * <b>Environment Configure:</b><a href=
	 * "https://db.apache.org/derby/papers/DerbyTut/install_software.html#derby_configure">https://db.apache.org/derby/papers/DerbyTut/install_software.html#derby_configure</a>
	 * 
	 * <pre>
	 * ij> java org.apache.derby.tools.ij
	 * ij> connect 'jdbc:derby:MyDbTest;create=true';
	 * 
	 * ij> create table derbyDB(num int, addr varchar(40));
	 * ij> insert into derbyDB values (1956,'Webster St.');
	 * ij> insert into derbyDB values (1910,'Union St.');
	 * ij> update derbyDB set num=180, addr='Grand Ave.' where num=1956;
	 * 
	 * ij> select * from derbyDb;
	 * NUM        |ADDR
	 * ----------------------------------------------------
	 * 180        |Grand Ave.                              
	 * 1910       |Union St.                               
	 *  
	 * 2 rows selected
	 * ij>
	 * </pre>
	 * 
	 * @return
	 */
	@Bean(name = "derbyJdbcTemplate")
	@ConditionalOnMissingBean(TsdbPhysicalMetricStore.class)
	public JdbcTemplate derbyJdbcTemplate() {
		final DruidDataSource datasource = new DruidDataSource();
		try {
			datasource.setUrl("jdbc:derby:" + USER_HOME + "/.umc/derby/metric.db;create=true");
			// datasource.setUsername("");
			// datasource.setPassword("");
			datasource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
			datasource.setInitialSize(10);
			datasource.setMinIdle(5);
			datasource.setMaxActive(200);
			datasource.setMaxWait(60_000);
			datasource.setTimeBetweenEvictionRunsMillis(60_000);
			datasource.setMinEvictableIdleTimeMillis(300_000);
			datasource.setValidationQuery("select 1 from sysibm.sysdummy1");
			datasource.setTestWhileIdle(true);
			datasource.setTestOnBorrow(false);
			datasource.setTestOnReturn(false);
			datasource.setPoolPreparedStatements(true);
			datasource.setMaxOpenPreparedStatements(50);
			datasource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000");
			return new JdbcTemplate(datasource);
		} finally {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					datasource.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
		}
	}

	@Bean
	@ConditionalOnMissingBean(TsdbPhysicalMetricStore.class)
	public DerbyPhysicalMetricStore derbyPhysicalMetricStore() {
		return new DerbyPhysicalMetricStore(derbyJdbcTemplate());
	}

	@Bean
	@ConditionalOnMissingBean(TsdbPhysicalMetricStore.class)
	public DerbyVirtualMetricStore derbyVirtualMetricStore() {
		return new DerbyVirtualMetricStore(derbyJdbcTemplate());
	}

}
