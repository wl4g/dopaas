package com.wl4g.devops.ci.devtool;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * connect linux
 */
public class ConnectLinuxCommand {
	private static final Logger logger = Logger.getLogger(ConnectLinuxCommand.class);
	private static String DEFAULTCHARTSET = "UTF-8";


	/**
	 *
	 * @param ip
	 * @param command
	 * @return
	 */
	public static String execute(String ip,String userName,String command) throws Exception{
		Connection conn = null;
		try {
			boolean flag = false;
			conn = new Connection(ip);
			conn.connect();// connect
			String userHome = System.getProperties().getProperty("user.home");
			flag = conn.authenticateWithPublicKey(userName,new File(userHome+"/.ssh/id_rsa"),null);
			if (flag) {
				logger.debug("login success！");
				return execute(conn,command);
			} else {
				logger.error("login fail!");
				throw new RuntimeException("login fail");
			}
		}catch (Exception e){
			throw e;
		}finally {
			if(null!=conn){
				conn.close();
			}
		}
	}

	/**
	 *
	 * @Title: login
	 * @return: Boolean
	 */
	private static Boolean login(Connection conn,String ip,String userName) throws Exception {
		boolean flag = false;
		conn = new Connection(ip);
		conn.connect();// connect
		String userHome = System.getProperties().getProperty("user.home");
		flag = conn.authenticateWithPublicKey(userName,new File(userHome+"/.ssh/id_rsa"),null);
		if (flag) {
			logger.debug("login success！");
		} else {
			logger.error("login fail!");
		}
		return flag;
	}



	/**
	 *
	 * @Title: execute
	 * @throws
	 */
	private static String execute(Connection conn,String cmd){
		logger.info("command="+cmd);
		String result = "";
		Session session = null;
		try {
			session = conn.openSession();// open session
			session.execCommand(cmd);// execute
			result = processStdout(session.getStdout(), DEFAULTCHARTSET);
			// if result blank, get error msg
			if (StringUtils.isBlank(result)) {
				result = processStdout(session.getStderr(), DEFAULTCHARTSET);
				//throw new RuntimeException("execute fail result="+result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null!=session){
				session.close();
			}
		}
		logger.info(result);
		return result;
	}

	/**
	 * @Title: processStdout
	 */
	private static String processStdout(InputStream in, String charset){
		InputStream stdout = new StreamGobbler(in);
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\n");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}



}

