package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_RULE;

/**
 * Service metric indicators rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class ServiceRuleConfigHandler implements RuleConfigHandler {

	@Autowired
	protected JedisService jedisService;

	@Autowired
	private AppGroupDao appGroupDao;

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AlarmConfigDao alarmConfigDao;



	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}


	@Override
	public List<AlarmTemplate> getByCollectId(Integer collectId) {
		return alarmTemplateDao.getByCollectId(collectId);
	}

	@Override
	public List<AlarmConfig> getByCollectIdAndTemplateId(Integer templateId, Integer collectId) {
		return alarmConfigDao.getByCollectIdAndTemplateId(templateId,collectId);
	}

	/**
	 * Get rule cache key by collectId.
	 * 
	 * @param collectId
	 * @return
	 */
	protected String getRuleCacheKey(String collectId) {
		return KEY_CACHE_ALARM_RULE + collectId;
	}

}
