package com.wl4g.devops.ci.pmplatform;

import com.wl4g.devops.ci.pmplatform.anno.PmPlatform;
import com.wl4g.devops.ci.pmplatform.constant.PlatformEnum;
import com.wl4g.devops.ci.pmplatform.model.dto.SelectInfo;
import com.wl4g.devops.ci.pmplatform.handle.PmPlatformInterface;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.dao.ci.TaskDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author vjay
 * @date 2020-01-03 14:47:00
 */
@Component
public class PcmPlatformHandle {

    @Autowired
    private TaskDao taskDao;

	private final Map<PlatformEnum, PmPlatformInterface> pmPlatformInterfaceMap;

	public PmPlatformInterface getPlatform(PlatformEnum platformEnum) {
		return pmPlatformInterfaceMap.get(platformEnum);
	}

	@Autowired
	public PcmPlatformHandle(List<PmPlatformInterface> pmPlatformInterfaces) {
		this.pmPlatformInterfaceMap = Collections.unmodifiableMap(buildPayBizStrategies(pmPlatformInterfaces));
	}

	private Map<PlatformEnum, PmPlatformInterface> buildPayBizStrategies(List<PmPlatformInterface> pmPlatformInterfaces) {
		return pmPlatformInterfaces.stream()
				.filter(pmPlatformInterface -> pmPlatformInterface.getClass().isAnnotationPresent(PmPlatform.class))
				.collect(Collectors.toMap(o -> o.getClass().getAnnotation(PmPlatform.class).value(), o -> o));
	}

    /**
     *
     * @param taskId
     */
	public PmPlatformInterface getHandle(Integer taskId){
        Assert.notNull(taskId,"taskId is null");
        Task task = taskDao.selectByPrimaryKey(taskId);
        Assert.notNull(task,"task not found");
        String pmPlatform = task.getPmPlatform();
        //TODO 假如没有配置，是报错？还是使用默认Redmine
        if(StringUtils.isBlank(pmPlatform)){
            pmPlatform = "Redmine";
        }
        PlatformEnum platformEnum = PlatformEnum.safeOf(pmPlatform);
        if(Objects.isNull(platformEnum)){
            platformEnum = PlatformEnum.Redmine;
        }
        PmPlatformInterface platform = getPlatform(platformEnum);
        Assert.notNull(platform,"not suppost this platform: "+ pmPlatform);
        return platform;
    }

	/**
	 * 获取用户列表
	 */
	public List<SelectInfo> getUsers(Integer taskId){
        PmPlatformInterface serviceImpl = getHandle(taskId);
        return serviceImpl.getUsers();
	}

    /**
     * 获取项目列表
     */
    public List<SelectInfo> getProjects(Integer taskId){
        PmPlatformInterface serviceImpl = getHandle(taskId);
        return serviceImpl.getProjects();
    }

    /**
     * 获取问题列表
     */
    public List<SelectInfo> getIssues(Integer taskId,String userId, String projectId,String search){
        PmPlatformInterface serviceImpl = getHandle(taskId);
        return serviceImpl.getIssues(userId,projectId,search);
    }

}
