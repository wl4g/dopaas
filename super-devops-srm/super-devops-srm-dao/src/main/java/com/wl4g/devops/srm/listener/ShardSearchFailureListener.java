package com.wl4g.devops.srm.listener;

import org.elasticsearch.action.search.ShardSearchFailure;
import org.springframework.stereotype.Component;

@Component
public class ShardSearchFailureListener implements Listener {

	@Override
	public void onFailure(ShardSearchFailure failure) {

	}

}
