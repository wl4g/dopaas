package com.wl4g.devops.ci.cron;

import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.service.CiService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.ci.TriggerDetail;
import com.wl4g.devops.common.constants.CiDevOpsConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2019-07-19 10:41:00
 */
public class CronRunnable implements Runnable{

    final protected Logger log = LoggerFactory.getLogger(getClass());

    private Trigger trigger;

    private List<TriggerDetail> triggerDetails;

    private Project project;

    private DeployProperties config;

    private CiService ciService;


    public CronRunnable(Trigger trigger,Project project,DeployProperties config,CiService ciService){
        this.trigger  = trigger;
        this.triggerDetails = trigger.getTriggerDetails();
        this.project = project;
        this.config = config;
        this.ciService = ciService;
    }

    @Override
    public void run() {
        log.info("Timing tasks start");
        if(check()){
            //TODO need build
            List<String> instanceStrs = new ArrayList<>();
            for(TriggerDetail triggerDetail : triggerDetails){
                instanceStrs.add(String.valueOf(triggerDetail.getInstanceId()));
            }
            ciService.createTask(project.getAppGroupId(), trigger.getBranchName(), instanceStrs, CiDevOpsConstants.TASK_TYPE_TIMMING);
        }
    }

    /**
     * check need or not build
     */
    private boolean check() {
        String sha = trigger.getSha();
        String path = config.getGitBasePath() + "/" + project.getProjectName();
        try {
            if (GitUtils.checkGitPahtExist(path)) {
                GitUtils.checkout(config.getCredentials(), path, trigger.getBranchName());
            } else {
                GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, trigger.getBranchName());
            }
            String oldestSha = GitUtils.getOldestCommitSha(path);
            return !StringUtils.equals(sha,oldestSha);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
