package com.wl4g.devops.ci.subject;

import com.wl4g.devops.ci.devtool.ConnectLinuxCommand;
import com.wl4g.devops.ci.devtool.DevConfig;
import com.wl4g.devops.ci.devtool.GitUtil;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
@Component
public abstract class BaseSubject {
	final protected Logger log = LoggerFactory.getLogger(BaseSubject.class);

	//branch name
	protected String branch;
	//branch path
	protected String path;
	//branch url
	protected String url;
	//project alias
	protected String alias;
	// for example:/super-devops-iam-security/target/demo.tar
	protected String tarPath;
	//tarName,for example : demo.tar / demo.jar
	protected String tarName;
	//instances
	protected List<AppInstance> instances;
	//taskDetails
	protected List<TaskDetail> taskDetails;

	//now
	protected Date now = new Date();

	public BaseSubject(){

	}

	/**
	 * hook
	 */
	abstract public void exec() throws Exception;

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
		String command = "scp -r "+path+" "+targetHost+":"+targetPath;
		return run(command);

	}

	/**
	 * unzip
	 */
	public String tar(String targetHost,String userName,String targetPath,String targetName) throws Exception{
		String command = "tar -xvf "+targetPath+"/"+targetName+" -C "+targetPath;
		return ConnectLinuxCommand.execute(targetHost,userName,command);
	}


	/**
	 * bak local + scp + rename
	 */
	public String scpAndTar(String path,String targetHost,String userName,String targetPath) throws Exception{
		String result = scpToTmp(path,userName+"@"+targetHost);
		result += tarToTmp(targetHost,userName,path);
		result += moveToTarPath(targetHost,userName,path,targetPath);
		return result;
	}

	public String reLink(String targetHost,String targetPath,String userName,String path) throws Exception{
		String command = "ln -snf "+targetPath+"/"+replaceMaster(subPacknameWithOutPostfix(path))+getDateTimeStr()+" "+DevConfig.linkPath+"/"+alias+"-current";
		return ConnectLinuxCommand.execute(targetHost,userName,command);
	}


	/**
	 * scpToTmp
	 */
	public String scpToTmp(String path,String targetHost) throws Exception{
		String command = "scp -r "+path+" "+targetHost+":/tmp";
		return run(command);
	}

	/**
	 * unzip in tmp
	 */
	public String tarToTmp(String targetHost,String userName,String path) throws Exception{
		String command = "tar -xvf /tmp"+"/"+subPackname(path)+" -C /tmp";
		return ConnectLinuxCommand.execute(targetHost,userName,command);
	}

	/**
	 * move to tar path
	 */
	public String moveToTarPath(String targetHost,String userName,String path,String targetPath)throws Exception{
		String command = "mv /tmp"+"/"+subPacknameWithOutPostfix(path)+" "+targetPath+"/"+replaceMaster(subPacknameWithOutPostfix(path))+getDateTimeStr();
		return ConnectLinuxCommand.execute(targetHost,userName,command);
	}


	/**
	 * local back up
	 */
	public String bakLocal(String path) throws Exception{
		checkPath(DevConfig.bakPath);
		String command = "cp -Rf "+path +" "+ DevConfig.bakPath+"/"+subPackname(path)+getDateTimeStr();
		return run(command);
	}


	/**
	 * rollback
	 */
	public String rollback() throws Exception{
		//TODO
		return null;
	}

	/**
	 * stop
	 */
	public String stop(String targetHost,String userName,String command) throws Exception{
		return ConnectLinuxCommand.execute(targetHost,userName,command);

	}

	/**
	 * start
	 */
	public String start(String targetHost,String userName,String command) throws Exception{
		return ConnectLinuxCommand.execute(targetHost,userName,command);
	}

	/**
	 * restart
	 */
	public String restart(String targetHost,String userName,String command) throws Exception{
		return ConnectLinuxCommand.execute(targetHost,userName,command);
	}


	public String getDateTimeStr(){
		String str = DateUtils.formatDate(now,DateUtils.YMDHM);
		str  = str.substring(2,str.length());
		return str;
	}


	public String subPackname(String path){
		String[] a = path.split("/");
		return a[a.length-1];
	}

	public String subPacknameWithOutPostfix(String path){
		String a = subPackname(path);
		return a.substring(0,a.lastIndexOf("."));
	}

	public String replaceMaster(String str){
		return str.replaceAll("master-","");
	}

	public void checkPath(String path){
		File file = new File(path);
		if(!file.exists()) {
			file.mkdirs();
		}
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

	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTarName() {
		return tarName;
	}

	public void setTarName(String tarName) {
		this.tarName = tarName;
	}

	public String getTarPath() {
		return tarPath;
	}

	public void setTarPath(String tarPath) {
		this.tarPath = tarPath;
	}
}
