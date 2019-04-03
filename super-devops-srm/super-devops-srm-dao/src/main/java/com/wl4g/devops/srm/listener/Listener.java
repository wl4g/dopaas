package com.wl4g.devops.srm.listener;

import org.elasticsearch.action.search.ShardSearchFailure;

public interface Listener {

	void onFailure(ShardSearchFailure failure);

}
