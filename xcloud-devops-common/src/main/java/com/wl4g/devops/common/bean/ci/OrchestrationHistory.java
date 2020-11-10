package com.wl4g.devops.common.bean.ci;

import com.wl4g.components.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * {@link OrchestrationHistory}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2019-11-06
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
public class OrchestrationHistory extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private String runId;
	private Integer status;
	private String info;
	private Long costTime;
	private List<PipelineHistory> pipeHistories;

}