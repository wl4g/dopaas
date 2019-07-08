package com.wl4g.devops.umc.alarm;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_RULE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;

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
	public List<AlarmConfig> selectByTemplateId(Integer templateId) {
		return alarmConfigDao.selectByTemplateId(templateId);
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}

	@Override
	public List<AlarmConfig> selectAll() {
		return alarmConfigDao.selectAll();
	}

	@Override
	public List<AlarmTemplate> selectAllWithRule() {
		return alarmTemplateDao.selectAllWithRule();
	}

	@Override
	public AlarmRuleInfo getRule(String collectId) {
		return jedisService.getObjectT(getRuleCacheKey(collectId), AlarmRuleInfo.class);
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
