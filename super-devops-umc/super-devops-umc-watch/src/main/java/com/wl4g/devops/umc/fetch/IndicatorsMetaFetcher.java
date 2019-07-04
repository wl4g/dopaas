package com.wl4g.devops.umc.fetch;

import java.util.List;

import com.dangdang.ddframe.job.api.ShardingContext;

/**
 * Watch indicators target fetcher.
 * 
 */
public interface IndicatorsMetaFetcher {

	List<IndicatorsMetaInfo> fetch(ShardingContext sctx);

}
