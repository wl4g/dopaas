package com.wl4g.devops.umc.fetch;

/**
 * Indicators meta info.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public interface IndicatorsMetaInfo {

	/**
	 * Get collection meta target. </br>
	 * e.g. instanceId(instance node identifier), dtuId(lot device identifier),
	 * etc
	 * 
	 * @return
	 */
	String getTargetIdentifier();

}
