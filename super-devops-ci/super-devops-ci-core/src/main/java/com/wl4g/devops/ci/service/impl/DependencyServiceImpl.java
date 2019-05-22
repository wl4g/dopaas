package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.devtool.ConnectLinuxCommand;
import com.wl4g.devops.ci.devtool.DevConfig;
import com.wl4g.devops.ci.devtool.GitUtil;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-22 11:39:00
 */
@Service
public class DependencyServiceImpl implements DependencyService{

    @Autowired
    private DependencyDao dependencyDao;

    @Autowired
    private DevConfig devConfig;

    @Autowired
    private ProjectDao projectDao;


    @Override
    public void build(Dependency dependency,String branch) throws Exception{

        Integer projectId = dependency.getProjectId();

        List<Dependency> parents =  dependencyDao.getParentsByProjectId(projectId);
        if(parents!=null||parents.size()>0){
            for(Dependency parent : parents){
                String br = parent.getParentBranch();
                build(parent, StringUtils.isBlank(br)?branch:br);
            }
        }

        //TODO build
        Project project = projectDao.selectByPrimaryKey(projectId);
        String path = devConfig.getGitBasePath()+"/"+project.getProjectName();
        if(checkGitPahtExist(path)){
            GitUtil.checkout(path,branch);
        }else{
            GitUtil.clone(project.getGitUrl(),path,branch);
        }
        //install
        install(path);

    }

    public boolean checkGitPahtExist(String path) throws Exception{
        File file = new File(path+"/.git");
        if(file.exists()){
            return true;
        }else {
            return false;
        }
    }


    /**
     * build (maven)
     */
    public String install(String path) throws Exception{
        String command = "mvn -f "+path+"/pom.xml clean install -Dmaven.test.skip=true";
        return ConnectLinuxCommand.run(command);
    }


}
