package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author vjay
 * @date 2019-10-11 10:13:00
 */
public class PipelineTaskRunner extends GenericTaskRunner<RunProperties>{

    public PipelineTaskRunner(CiCdProperties.ExecutorProperties executor) {
        super(executor);
    }


    @Override
    public void run() {

    }

    public ThreadPoolExecutor getWorker(){
        return super.getWorker();
    }







}
