package com.wl4g.devops.umc.watch;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.config.WatchProperties;
import com.wl4g.devops.umc.fetch.IndicatorsMetaFetcher;
import com.wl4g.devops.umc.fetch.IndicatorsMetaInfo;

/**
 * Metrics state indicators watcher of based.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public abstract class IndicatorsStateWatcher implements SimpleJob {

	final public static String KEY_FETCH_CACHE = "umc_meta_fetch_cache";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected JedisService jedisService;

	@Autowired
	protected WatchProperties config;

	@Autowired
	protected IndicatorsMetaFetcher fetcher;

	@Override
	public void execute(ShardingContext sctx) {
		if (log.isInfoEnabled()) {
			log.info("Exec sharding for - {}", toJSONString(sctx));
		}

		try {
			List<IndicatorsMetaInfo> dataset = null;

			// Fetch indicators meta info.
			if (config.getFetchCacheTime() > 0) {
				// Fetch from cache
				dataset = fetchCache();
				if (isEmpty(dataset)) {
					dataset = fetcher.fetch(sctx); // From DB
				}
				// Store to cache.
				storeToCacheIfNecessary(dataset);
			} else {
				dataset = fetcher.fetch(sctx); // From DB
			}

			if (log.isInfoEnabled()) {
				log.info("Fetch indicators metas for - size({})", dataset.size());
			}
			if (log.isDebugEnabled()) {
				log.debug("Fetch indicators metas for - {}", dataset);
			}

			doWatching(dataset);
		} catch (Exception e) {
			log.error("Failed to fetch indicators targets", e);
		}

	}

	/**
	 * Fetch meta info from cache.
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected List<IndicatorsMetaInfo> fetchCache() {
		return jedisService.getObjectT(KEY_FETCH_CACHE, ArrayList.class);
	}

	/**
	 * Store fetch meta info to cache.
	 * 
	 * @param dataset
	 */
	protected void storeToCacheIfNecessary(List<IndicatorsMetaInfo> dataset) {
		if (isEmpty(dataset)) {
			return;
		}

		jedisService.setObjectT(KEY_FETCH_CACHE, toJSONString(dataset), config.getFetchCacheTime());
	}

	/**
	 * Data-set processing.
	 * 
	 * @param dataset
	 */
	protected abstract void doWatching(List<IndicatorsMetaInfo> dataset);

}
