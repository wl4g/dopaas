package com.wl4g.devops.ci.devtool;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-08 09:51:00
 */

//@Component
//@ConfigurationProperties(prefix="devconf")
public class BaseConfig {

	public static String gitBasePath;

	public static String gitAccount;

	public static String gitPassword;

	public static List<EnvConfig> env;

	public static CredentialsProvider cp;


	static{
		String json = ReadFile("/env-config.json");//read from json file
		BaseConfig baseConfig  = JSON.parseObject(json,BaseConfig.class);

		BaseConfig.gitBasePath = baseConfig.getGitBasePath();
		BaseConfig.gitAccount = baseConfig.getGitAccount();
		BaseConfig.gitPassword = baseConfig.getGitPassword();
		BaseConfig.env = baseConfig.getEnv();
		BaseConfig.cp = new UsernamePasswordCredentialsProvider( baseConfig.getGitAccount(), baseConfig.getGitPassword() );
	}

	public static EnvConfig getEnvConfig(String code){
		for(EnvConfig envConfig : env){
			if(StringUtils.equals(envConfig.getName(),code)){
				return envConfig;
			}
		}
		return null;
	}

	public String getGitBasePath() {
		return gitBasePath;
	}

	public void setGitBasePath(String gitBasePath) {
		this.gitBasePath = gitBasePath;
	}

	public String getGitAccount() {
		return gitAccount;
	}

	public void setGitAccount(String gitAccount) {
		this.gitAccount = gitAccount;
		if(this.gitAccount!=null&&this.gitPassword!=null){
			this.cp = new UsernamePasswordCredentialsProvider( gitAccount, gitPassword );
		}
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		this.gitPassword = gitPassword;
		if(this.gitAccount!=null&&this.gitPassword!=null){
			this.cp = new UsernamePasswordCredentialsProvider( gitAccount, gitPassword );
		}
	}

	public List<EnvConfig> getEnv() {
		return env;
	}

	public void setEnv(List<EnvConfig> env) {
		this.env = env;
	}

	public CredentialsProvider getCp() {
		return cp;
	}

	public void setCp(CredentialsProvider cp) {
		this.cp = cp;
	}

	public static String ReadFile(String Path) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			InputStream inputStream = BaseConfig.class.getResourceAsStream(Path);
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}

}
