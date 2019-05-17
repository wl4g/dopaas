package com.wl4g.devops.ci.devtool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
@Component
public abstract class DevTool {
	final protected Logger log = LoggerFactory.getLogger(DevTool.class);

	//branch name
	protected String branch;
	//branch path
	protected String path;
	//branch url
	protected String url;
	//target server host
	//protected String targetHost;
	//target server path
	//protected String targetPath;

	protected EnvConfig envConfig;


	public DevTool(){

	}

	/**
	 * hook
	 */
	abstract public void excu() throws Exception;

	/**
	 * exce command
	 */
	public String run(String command) throws Exception {
		log.info("exce command:"+command);
		Process p = Runtime.getRuntime().exec(command);
		InputStream is = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		p.waitFor();
		//print
		StringBuffer result = new StringBuffer();
		String s = null;
		while ((s = reader.readLine()) != null) {
			result.append(s).append("\n");
		}
		log.info(result.toString());
		if (p.exitValue() != 0) {

			InputStream er = p.getErrorStream();
			BufferedReader erReader = new BufferedReader(new InputStreamReader(er));
			while ((s = erReader.readLine()) != null) {
				result.append(s).append("\n");
			}
			//exce fail
			throw new RuntimeException("exce command fail,command="+command+"\n cause:"+result.toString());
		}
		return result.toString();
	}

	/**
	 * check git path exist
	 */
	public boolean checkGitPahtExist() throws Exception{
		File file = new File(path+"/.git");
		if(file.exists()){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * clone
	 */
	public void clone(String path,String url,String branch) throws Exception{
		GitUtil.clone(url,path,branch);
	}

	/**
	 * checkOut
	 */
	public void checkOut(String path,String branch) throws Exception {
		GitUtil.checkout(path,branch);
	}


	/**
	 * build (maven)
	 */
	public String build(String path) throws Exception{
		String command = "mvn -f "+path+"/pom.xml clean install -Dmaven.test.skip=true";
		//String command = "mvn -f /Users/vjay/gittest/jianzutest clean install -Dmaven.test.skip=true";
		return run(command);
	}

	/**
	 *
	 * @param path -- /fingerproject/finger-auth/target/fingerauth.tar
	 * @param targetHost -- webapps@10.100.0.253
	 * @param targetPath -- /data/webapps/web-auth/webapps/
	 * @return
	 * @throws Exception
	 */
	public String scp(String path,String targetHost,String targetPath) throws Exception{
		//String checkPath = "ssh "+targetHost+" \"mkdir -p "+targetPath+"\"";
		//run(checkPath);
		String command = "scp -r "+path+" "+targetHost+":"+targetPath;
		return run(command);

	}

	/**
	 * unzip
	 */
	public String tar(String targetHost,String targetPath,String targetName) throws Exception{
		String command = "tar -xvf "+targetPath+"/"+targetName+" -C "+targetPath;
		return ConnectLinuxCommand.execute(targetHost,command);
	}

	/**
	 * bakcUp
	 */
	public String backUp() throws Exception{
		String command = "ssh "+envConfig.getTargetHost()+" \"cp "+envConfig.getTargetPath()+" "+envConfig.getTargetPath()+"_bak"+"\"";
		return run(command);
	}

	/**
	 * rollback
	 */
	public String rollback() throws Exception{
		String command = "ssh "+envConfig.getTargetHost()+" \"mv "+envConfig.getTargetPath()+"_bak"+" "+envConfig.getTargetPath()+"\"";
		return run(command);
	}

	/**
	 * stop
	 */
	public String stop(String command) throws Exception{
		command = "ssh "+envConfig.getTargetHost()+" \""+command+"\"";
		return run(command);

	}

	/**
	 * start
	 */
	public String start(String command) throws Exception{
		command = "ssh "+envConfig.getTargetHost()+" \""+command+"\"";
		return run(command);
	}

	/**
	 * restart
	 */
	public String restart(String command) throws Exception{
		command = "ssh "+envConfig.getTargetHost()+" \""+command+"\"";
		return run(command);
	}


	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
