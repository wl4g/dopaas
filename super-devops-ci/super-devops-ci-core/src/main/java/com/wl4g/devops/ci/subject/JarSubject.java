package com.wl4g.devops.ci.subject;

import com.wl4g.devops.ci.devtool.ConnectLinuxCommand;
import com.wl4g.devops.common.bean.scm.AppInstance;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
@Component
public class JarSubject extends BaseSubject {

	public JarSubject(){

	}

	public JarSubject(String path, String url, String branch, String alias, String tarPath, List<AppInstance> instances){
		super.path = path;
		super.url = url;
		super.branch = branch;
		super.alias = alias;
		super.tarPath = tarPath;
		super.instances = instances;
		String[] a = tarPath.split("/");
		super.tarName = a[a.length-1];
	}

	@Override
	public void excu() throws Exception{
		//chekcout
		if(checkGitPahtExist()){
			checkOut(path,branch);
		}else{
			clone(path,url,branch);
		}
		//build
		build(path);

		//scp to server
		for(AppInstance instance : instances){

			//scp to server
			scp(path+"/"+tarPath,instance.getServerAccount()+"@"+instance.getHost(),instance.getWebappsPath());

			//stop server
			stop(instance.getHost(),instance.getServerAccount(),alias);

			//restart server
			start(instance.getHost(),instance.getServerAccount(),alias,tarName);
		}
		log.info("Done");
	}

	public String stop(String host,String userName,String module) throws Exception{
		String command = "for i in `jps|grep "+module+" |awk '{print $1}' `; do kill -9 $i ; done;";
		try {
			ConnectLinuxCommand.execute(host,userName,command);
		}catch (Exception e){

		}
		return null;

	}

	public String start(String host,String userName,String module,String targetName) throws Exception{
		String command = "nohup java -Djava.ext.dirs=/root/webapps/dataflux-oper-master-bin/libs  -cp /root/webapps/dataflux-oper-master-bin/libs/datafluxOper.jar com.cn7782.devops.DatafluxOper >/dev/null  &   ";
		//String command = "sc "+module+" start";
		return ConnectLinuxCommand.execute(host,userName,command);
	}

}
