package com.wl4g.devops.common.bean.ci;

import com.wl4g.components.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClusterExtension extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Long clusterId;
	private String defaultEnv;
	private String defaultBranch;

	// --- Temporary. ---

	private String clusterName;

}