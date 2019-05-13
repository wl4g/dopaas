package com.wl4g.devops.ci.service.devtool;

import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
@Component
public class DefaultSubject extends DevTool {

	public DefaultSubject(){

	}


	public DefaultSubject(String path,String url,String branch,EnvConfig envConfig){
		super.path = path;
		super.url = url;
		super.branch = branch;
		super.envConfig = envConfig;
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

		//TODO get tar package
		//String tar = getTar(path+"/target");
		//tar = getJar(path+"/jzclient/target");

		//scp to server
		ChildrenSubjectConfig[] childrenSubjectConfigs = envConfig.getChild();
		for(ChildrenSubjectConfig childrenSubjectConfig : childrenSubjectConfigs){
			if(envConfig.getIncludeChild()!=null&& !Arrays.asList(envConfig.getIncludeChild()).contains(childrenSubjectConfig.getAlias())){
				continue;
			}
			//scp to server
			scp(path+childrenSubjectConfig.getPath()+"/target/"+childrenSubjectConfig.getTarName(),envConfig.getTargetHost(),envConfig.getTargetPath());

			//stop server
			stop(childrenSubjectConfig.getAlias());

			//decompression the	tar package
			tar(envConfig.getTargetHost(),envConfig.getTargetPath(),childrenSubjectConfig.getTarName());

			//restart server
			start(childrenSubjectConfig.getAlias(),childrenSubjectConfig.getTarName());
		}
		log.info("Done");
	}



	public String stop(String module) throws Exception{
		String command = "for i in `jps|grep "+module+" |awk '{print $1}' `; do kill -9 $i ; done;";
		try {
			ConnectLinuxCommand.execute(envConfig.getTargetHost(),command);
		}catch (Exception e){

		}
		return null;

	}

	public String start(String module,String targetName) throws Exception{
		String command = "nohup java -Djava.ext.dirs=/root/webapps/dataflux-oper-master-bin/libs  -cp /root/webapps/dataflux-oper-master-bin/libs/datafluxOper.jar com.cn7782.devops.DatafluxOper >/dev/null  &   ";
		//String command = "sc "+module+" start";
		return ConnectLinuxCommand.execute(envConfig.getTargetHost(),command);
	}



	/*private static String getTar(String path){
		File file = new File(path);
		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override public boolean accept(File dir, String name) {
				String[] strArray = name.split("\\.");
				int suffixIndex = strArray.length -1;
				if(strArray[suffixIndex].equalsIgnoreCase("tar")){
					return true;
				}
				return false;
			}
		};
		File[] files = file.listFiles(filenameFilter);
		if(files!=null&&files.length>0){
			return files[0].getAbsolutePath();
		}
		return null;
	}*/


}
