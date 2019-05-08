package com.wl4g.devops.rest;

import com.alibaba.fastjson.JSONObject;
import com.wl4g.devops.ci.service.devtool.BaseConfig;
import com.wl4g.devops.ci.service.devtool.DefaultSubject;
import com.wl4g.devops.ci.service.devtool.DevTool;
import com.wl4g.devops.ci.service.devtool.EnvConfig;
import com.wl4g.devops.common.web.BaseController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vjay
 * @date 2019-05-05 15:37:00
 */

@RestController
public class DevController extends BaseController {




	/**
	 * for gitlab webkook
	 */
	@RequestMapping("hook")
	public void hook(@RequestBody JSONObject jsonObject) throws Exception{
		log.info(jsonObject.toString());

		//Analysis json
		String ref = jsonObject.getString("ref");
		String branchName = ref.substring(11);

		JSONObject repository = jsonObject.getJSONObject("repository");
		if(null==repository){
			return;
		}
		String url = repository.getString("git_http_url");
		String projectName = repository.getString("name");
		log.info("activity hook,projectName="+projectName+" branchName="+branchName+" url="+url);


		DevTool devTool = getDevTool(projectName,branchName,url);
		if(null==devTool){
			log.error("not suppost now");
			return;
		}

		//excu
		devTool.excu();

	}

	/**
	 * for web
	 */
	@RequestMapping("pkg")
	public void pkg(String projectName,String url,String branch,String targetHost,String targetPath) throws Exception{
		EnvConfig envConfig = BaseConfig.getEnvConfig(projectName+"_"+branch);
		DevTool devTool = new DefaultSubject(BaseConfig.gitBasePath+"/"+projectName,url,branch,envConfig);
		//excu
		devTool.excu();
	}


	private DevTool getDevTool(String projectName,String branchName,String url){
		//TODO for test
		projectName = "safecloud-devops";
		branchName = "master";
		url = "http://code.anjiancloud.owner:8443/devops-team/safecloud-devops.git";


		DevTool devTool = null;
		EnvConfig envConfig = BaseConfig.getEnvConfig(projectName+"_"+branchName);
		if (envConfig==null){
			return null;
		}
		//TODO filter-- different params ,different devtool
		if(false){//TODO

		}else{//default devtool
			devTool = new DefaultSubject(BaseConfig.gitBasePath+"/"+projectName,url,branchName,envConfig);
		}

		//TODO just for test
		//devTool = new TestSubject("/Users/vjay/gittest/super-devops","https://github.com/xburnerair00/super-devops.git","master","root@safecloud-test","/root/hwjtest/super-devops");

		return devTool;
	}



}
