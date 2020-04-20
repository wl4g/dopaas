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
package com.wl4g.devops.support.mybatis.loader;

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.lang.Assert2.state;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.Thread.sleep;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.replace;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.wl4g.devops.tool.common.log.SmartLogger;

import static com.wl4g.devops.tool.common.reflect.ReflectionUtils2.*;

/**
 * Mybatis {@link SqlSessionFactory} developments hotspot mapper re-loader.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月14日
 * @since
 */
public final class SqlSessionMapperHotspotLoader implements ApplicationRunner {
	final public static String TARGET_PART_PATH = "target" + File.separator + "classes";
	final public static String SRC_PART_PATH = "src" + File.separator + "main" + File.separator + "resources";

	final protected SmartLogger log = getLogger(getClass());

	/** Refresh configuration properties. */
	final protected HotspotLoadProperties config;
	/** Monitor objectives for {@link SqlSessionFactory} */
	final protected SqlSessionFactoryBean sessionFactory;

	/** Refresher of last timestamp. */
	final private AtomicLong lastRefreshTime = new AtomicLong(0L);
	/** Runner thread boss. */
	private Thread boss;

	private Configuration configuration;
	private Resource[] mapperLocations;

	public SqlSessionMapperHotspotLoader(SqlSessionFactoryBean sessionFactory, HotspotLoadProperties config) {
		notNull(sessionFactory, "SqlSessionFactory can't is null.");
		notNull(config, "MapperHotspotLoader properties config can't is null.");
		this.sessionFactory = sessionFactory;
		this.config = config;
		try {
			// Init configuration.
			init();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Note: Start and run the refresh processing method (note that if web.xml
	 * configures spring and springmvc as two containers, it will be called
	 * twice, resulting in clear mybatis Sqlelements error.)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		boss = new Thread(() -> {
			boolean stopped = false;
			while (!stopped && !boss.isInterrupted()) {
				try {
					sleep(config.getMonitorLoaderIntervalMs());
					if (isChanged()) {
						refresh(configuration);
						stopped = false;
					}
				} catch (Exception e) {
					if (config.isFastFail()) {
						stopped = true;
						log.error("", e);
					}
				}
			}
			log.warn("Stopped SqlSession mappers hotspot loader monitor!");
		});
		boss.start();

		log.info("Started SqlSession mappers hotspot loader of {}", sessionFactory.toString());
	}

	/**
	 * Initializing configuration、mapperLocations etc.
	 * 
	 * @throws Exception
	 */
	private synchronized void init() throws Exception {
		state(isNull(configuration) && isNull(mapperLocations),
				String.format("Already initialized mappers hotspot loader. configuration for: %s", configuration));
		// Obtain configuration.
		configuration = sessionFactory.getObject().getConfiguration();
		// Obtain mapperLocations.
		Field mapperLocaionsField = findField(SqlSessionFactoryBean.class, "mapperLocations", Resource[].class);
		makeAccessible(mapperLocaionsField);
		mapperLocations = (Resource[]) getField(mapperLocaionsField, sessionFactory);
		// Convert to origin resources.
		mapperLocations = getOriginResources(mapperLocations);

		notNull(configuration, "SqlSessionFactory configuration can't is null.");
		notNull(mapperLocations, "SqlSessionFactory mapperLocations can't is null.");
	}

	/**
	 * Refresh the contents of mybatis mapping files.
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	private synchronized void refresh(Configuration configuration) throws Exception {
		// 清理Mybatis的所有映射文件缓存, 目前由于未找到清空被修改文件的缓存的key值, 暂时仅支持全部清理, 然后全部加载
		doCleanupOlderCacheConfig(configuration);

		long begin = currentTimeMillis();
		for (Resource rs : mapperLocations) {
			try {
				XMLMapperBuilder builder = new XMLMapperBuilder(rs.getInputStream(), configuration, rs.toString(),
						configuration.getSqlFragments()); // Reload.
				builder.parse();
				log.debug("Refreshed for: {}", rs);
			} catch (IOException e) {
				log.error(format("Failed to refresh mapper for: %s", rs), e);
			}
		}
		long now = currentTimeMillis();
		out.println(format("%s - Refreshed mappers: %s, cost: %sms", new Date(), mapperLocations.length, (now - begin)));

		// Update refresh time.
		lastRefreshTime.set(now);
	}

	/**
	 * Detect if at least one mapper file has been updated.
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean isChanged() throws IOException {
		if (lastRefreshTime.get() <= 0) { // Just initialized?
			lastRefreshTime.set(currentTimeMillis());
			return false;
		}
		for (Resource rs : mapperLocations) {
			if (nonNull(rs) && rs.getFile().lastModified() > lastRefreshTime.get()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clear several importants caches in configuration
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	private synchronized void doCleanupOlderCacheConfig(Configuration configuration) throws Exception {
		Class<?> classConfig = configuration.getClass();

		clearMap(classConfig, configuration, "mappedStatements", null);
		clearMap(classConfig, configuration, "caches", null);
		clearMap(classConfig, configuration, "resultMaps", null);
		clearMap(classConfig, configuration, "parameterMaps", null);
		clearMap(classConfig, configuration, "keyGenerators", null);
		clearMap(classConfig, configuration, "sqlFragments", null);

		clearSet(classConfig, configuration, "loadedResources", null);
	}

	/**
	 * Clear map cache in configuration.
	 * 
	 * @param classConfig
	 * @param configuration
	 * @param fieldName
	 * @param clearKey
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private synchronized void clearMap(Class<?> classConfig, Configuration configuration, String fieldName, Object clearKey)
			throws Exception {
		Field field = classConfig.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map mapConfig = (Map) field.get(configuration);
		// (此用于实现只重新加载单个mapper文件的热部署, 但是目前由于未找到清空被修改文件的缓存的key值,
		// 暂无法实现单个mapper热部署)
		// mapConfig.remove(clearKey);
		mapConfig.clear();
	}

	/**
	 * Clear set cache in configuration.
	 * 
	 * @param classConfig
	 * @param configuration
	 * @param fieldName
	 * @param clearKey
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private synchronized void clearSet(Class<?> classConfig, Configuration configuration, String fieldName, Object clearKey)
			throws Exception {
		Field field = classConfig.getDeclaredField(fieldName);
		field.setAccessible(true);
		Set setConfig = (Set) field.get(configuration);
		// (此用于实现只重新加载单个mapper文件的热部署, 但是目前由于未找到清空被修改文件的缓存的key值,
		// 暂无法实现单个mapper热部署)
		// setConfig.remove(clearKey);
		setConfig.clear();
	}

	/**
	 * Because idea does not hot update the mapper.xml file in the target
	 * directory by default, it can only be converted to the source directory
	 * (original file)
	 * 
	 * @param mapperLocations
	 * @return
	 * @throws IOException
	 */
	private Resource[] getOriginResources(Resource[] mapperLocations) throws IOException {
		List<Resource> res = new ArrayList<>(mapperLocations.length);
		if (nonNull(mapperLocations)) {
			for (Resource r : mapperLocations) {
				String path = r.getFile().getAbsolutePath();
				path = replace(path, TARGET_PART_PATH, SRC_PART_PATH);
				res.add(new FileSystemResource(path));
			}
		}
		return res.toArray(new Resource[] {});
	}

	// /**
	// * 获取需要刷新的文件列表.(此用于实现只重新加载单个mapper文件的热部署, 但是目前由于未找到清空被修改文件的缓存的key值,
	// * 暂无法实现单个mapper热部署)
	// *
	// * @param beforeTime
	// * 上次刷新时间
	// * @return 刷新文件列表
	// * @throws IOException
	// */
	// private List<Resource> getRefreshResource(Long beforeTime) throws
	// IOException {
	// List<Resource> refreshResourcelist = new ArrayList<Resource>();
	// for (Resource resource : mapperLocations) {
	// if (resource != null && resource.getFile().lastModified() > beforeTime) {
	// refreshResourcelist.add(resource);
	// }
	// }
	// return refreshResourcelist;
	// }

	/**
	 * Mybatis mappers hotspot loader properties configuration.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月14日
	 * @since
	 */
	public static class HotspotLoadProperties implements Serializable {
		private static final long serialVersionUID = -2662416556401160389L;

		/** {@link SqlSessionFactory} watching intervalMs. */
		private long monitorLoaderIntervalMs = 1000L;

		/** Refresh failed processing policy. */
		private boolean fastFail = false;

		public long getMonitorLoaderIntervalMs() {
			return monitorLoaderIntervalMs;
		}

		public void setMonitorLoaderIntervalMs(long monitorIntervalMs) {
			isTrue(monitorIntervalMs >= 200, "Monitor intervalMs must >=200");
			this.monitorLoaderIntervalMs = monitorIntervalMs;
		}

		public boolean isFastFail() {
			return fastFail;
		}

		public void setFastFail(boolean fastFail) {
			this.fastFail = fastFail;
		}

	}

}