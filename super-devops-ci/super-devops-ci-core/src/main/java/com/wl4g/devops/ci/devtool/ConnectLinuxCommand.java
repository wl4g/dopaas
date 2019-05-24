package com.wl4g.devops.ci.devtool;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.wl4g.devops.shell.processor.ShellConsoles;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * execute command util
 */
public class ConnectLinuxCommand {
    private static final Logger logger = Logger.getLogger(ConnectLinuxCommand.class);
    private static String DEFAULTCHARTSET = "UTF-8";


    /**
     * local run
     */
	/*public static String run(String command) throws Exception {
		logger.info("exce command:"+command);
		ShellConsoles.write("exce command:"+command);
		Process p = Runtime.getRuntime().exec(command);
		InputStream is = p.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		p.waitFor();
		//print
		StringBuffer result = new StringBuffer();
		String s = null;
		while ((s = reader.readLine()) != null) {
			result.append(s).append("\n");
			logger.info(s);
			ShellConsoles.write(s);
		}
		if (p.exitValue() != 0) {
			InputStream er = p.getErrorStream();
			BufferedReader erReader = new BufferedReader(new InputStreamReader(er));
			while ((s = erReader.readLine()) != null) {
				result.append(s).append("\n");
				logger.info(s);
				ShellConsoles.write(s);
			}
			//exce fail
			throw new RuntimeException("exce command fail,command="+command+"\n cause:"+result.toString());
		}
		return result.toString();
	}*/

    /**
     * run in local
     */
    public static String runLocal(String cmd) throws Exception {
        logger.info("command:"+cmd);
        StringBuffer sb = new StringBuffer();
        StringBuffer se = new StringBuffer();

        Process ps = Runtime.getRuntime().exec(cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        BufferedReader be = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
            logger.info(line);
            ShellConsoles.write(line);
        }
        while ((line = be.readLine()) != null) {
            se.append(line).append("\n");
            logger.info(line);
            ShellConsoles.write(line);
        }
        String result = sb.toString();
        String resulterror = se.toString();
        if (StringUtils.isNotBlank(resulterror)) {
            result += resulterror;
            throw new RuntimeException("exce command fail,command=" + cmd + "\n cause:" + result.toString());
        }

        return result;
    }


    /**
     * execute with local rsa file
     *
     * @param ip
     * @param command
     * @return
     */
    public static String execute(String ip, String userName, String command) throws Exception {
        Connection conn = null;
        try {
            boolean flag = false;
            conn = new Connection(ip);
            conn.connect();// connect
            String userHome = System.getProperties().getProperty("user.home");
            flag = conn.authenticateWithPublicKey(userName, new File(userHome + "/.ssh/id_rsa"), null);
            if (flag) {
                logger.debug("login success！");
                return execute(conn, command);
            } else {
                logger.error("login fail!");
                throw new RuntimeException("login fail");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }


    /**
     * @param ip
     * @param command
     * @return
     */
    public static String execute(String ip, String userName, String command, char[] rsa) throws Exception {
        logger.info("exce command:" + command);
        ShellConsoles.write("exce command:" + command);
        Connection conn = null;
        try {
            boolean flag = false;
            conn = new Connection(ip);
            conn.connect();// connect
            flag = conn.authenticateWithPublicKey(userName, rsa, null);
            if (flag) {
                logger.debug("login success！");
                String result = execute(conn, command);
                //logger.info(result);
                //ShellConsoles.write(result);
                return result;
            } else {
                logger.error("login fail!");
                throw new RuntimeException("login fail");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }


    public static void uploadFile(String ip, String userName,char[] rsa,String localFile,String remoteTargetDirectory) throws IOException {
        Connection conn = null;
        try {
            boolean flag = false;
            conn = new Connection(ip);
            conn.connect();// connect
            flag = conn.authenticateWithPublicKey(userName, rsa, null);
            if (flag) {
                logger.debug("login success！");
                File file = new File(localFile);
                SCPClient scpClient = conn.createSCPClient();
                //scpClient.put(localFile,file.length()-1, remoteTargetDirectory,"0744");
                
            } else {
                logger.error("login fail!");
                throw new RuntimeException("login fail");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }




    /**
     * @Title: login
     * @return: Boolean
     */
    private static Boolean login(Connection conn, String ip, String userName) throws Exception {
        boolean flag = false;
        conn = new Connection(ip);
        conn.connect();// connect
        String userHome = System.getProperties().getProperty("user.home");
        flag = conn.authenticateWithPublicKey(userName, new File(userHome + "/.ssh/id_rsa"), null);
        if (flag) {
            logger.debug("login success！");
        } else {
            logger.error("login fail!");
        }
        return flag;
    }


    /**
     * @throws
     * @Title: execute
     */
    private static String execute(Connection conn, String cmd) {
        //logger.info("command=" + cmd);
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
        } finally {
            if (null != session) {
                session.close();
            }
        }
        logger.info(result);
        return result;
    }

    /**
     * @Title: processStdout
     */
    private static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
                logger.info(line);
                ShellConsoles.write(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static void main(String[] args) {

		/*try {
			AES aes = new AES("03DE18C2FC4E605F");
			String s = "-----BEGIN RSA PRIVATE KEY-----\n" +
					"MIIEpQIBAAKCAQEAwawifYZlHNdmkdMmXdi6wslkfvvAVjGo4cBPtrOFonD0Paex\n" +
					"tVRckfkj6rCu4IkKOq6HFBBf1peYVHojLFUm4FGC+YatxoLcdExBj8A/oMVsWN8a\n" +
					"ZWv5RH0lqUPZyuefqIrD+pos0R1hJtEDh5cKKT+Ae7kOP2+pX0QeGu0F/z9jozPo\n" +
					"PiM5DaoM0xaDqhmn1dnY03X3TAY8/V9Oy1zSRslXoiF2EmfTiaHBlvCeK5WhCiMd\n" +
					"5Xfn26Eiw3RBePh/eGiXjgv2ILZA7pnoINCa+PXI6VW6mthHQ8GJ7w+2afCGZBun\n" +
					"hxpGPEKih8YGNHBlGKUPale0pPMqI703iENZrQIDAQABAoIBAQCD2/qvk+0Lsev3\n" +
					"pNceVgzxycROYIEXLkBZU2Hydk+pxVXFFIN9fa55BDNb+mdWIHeCdIkrM+rMY/Im\n" +
					"sfF4oZEScOzHjtaJrVcDJ1gL00x+3WtjJqMGInlYFAysLbH+36xoR/IekRGqXmJi\n" +
					"1zOcAU29v6pukhQNRK0AW5RTqMTIfuQeR7Utpq4QNk8BSfDKJcyVoiTpaJ3Pv4AG\n" +
					"rPkIgtIqtmDJmp6MbMVtJA8AyxYrAiJbHntVeRddj2NkrScK/J4RG+5U/9Jj5cpB\n" +
					"VA21C7pt4lvUVuAPQ+esFLiwxBlIfW+sS5jaKCbJaeBL78xOiaLBJNWfrGR4USdu\n" +
					"k0P8o/sBAoGBAO6YeX2TDyVH/8XsSq9nPs+hu85wMqWohswG9R6oxcurVJLLKjdd\n" +
					"vqwpFaOUaQHcZr7iN2jGWqxxjFRW5qnwevk8uyXs9joSKV2C7KUdxuP9dtuqJNyl\n" +
					"+p1qN57+J69WXQNJoMKmiICK43fW89CoNucqJqrmsLzBkQiFmtR83ORxAoGBAM/M\n" +
					"xBTaVPrJYXxx10TZo+NcN5dv4FfDmK/Uz6v2F4y8xoRxpde8yeo0xj9CM8J2SX8w\n" +
					"712GLWI03m8RsYg/g2qRueK34lrA0eJ8GGUV2SJOqOQqlv1FJJVDp3658Cm0IyDT\n" +
					"CvyHJyrGmJljUhTgnwMuuzcp1wAfenWFp6OaAfb9AoGAXZMxOr29V+rH9nD4zZgZ\n" +
					"e0c8J/e69VuGGmi0I+UfRgSY88V4diRvDohCc1hWYqN1LHH+Nzpr/2u9FKrMZmPp\n" +
					"ZuyZnYM1Aoty67jYZN2rzmju/7HYKS1zf99TlyiomcyuSAbNZOn5aSiPk8Wa8/+1\n" +
					"IK5YYfh94lmsLwJvOd0KqRECgYEAxUa22L02dCh/Pm+tWRXt+0lvFXwG1gtBh5xX\n" +
					"0/97+Aa3yMFEGv6GCq0zkJa/INy/hdrlRDrAFz3t9jAsBReXIbNbcBv27wWjvIrn\n" +
					"dgA59dILkSHF2oir5HEoMK1BjbYQq3bwNTHyQy/ra6PZJyzgiVryLbqw/NLlpXDP\n" +
					"6Aer2dkCgYEAwKka1EYm5/N4krwsVvNBWD4Xgt4dtkGkQYkhyXZIqGTLFntIdVig\n" +
					"jKoQ6kaFTPaSST4kWNoXxNWvBDjarOriPa//St+l5fsEjfhjF1CfCS5aKvKfIwmP\n" +
					"jZss7kAharoCjXmxdyqPBEjJPHMts7d93olfGDGCvFrZnEfuD+zUcmU=\n" +
					"-----END RSA PRIVATE KEY-----";
			System.out.println("cipherText: " + (s = aes.encrypt(s)));
			System.out.println("plainText: " + aes.decrypt(s));
		} catch (Exception e) {
			e.printStackTrace();
		}*/


        String ase = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEpQIBAAKCAQEAwawifYZlHNdmkdMmXdi6wslkfvvAVjGo4cBPtrOFonD0Paex\n" +
                "tVRckfkj6rCu4IkKOq6HFBBf1peYVHojLFUm4FGC+YatxoLcdExBj8A/oMVsWN8a\n" +
                "ZWv5RH0lqUPZyuefqIrD+pos0R1hJtEDh5cKKT+Ae7kOP2+pX0QeGu0F/z9jozPo\n" +
                "PiM5DaoM0xaDqhmn1dnY03X3TAY8/V9Oy1zSRslXoiF2EmfTiaHBlvCeK5WhCiMd\n" +
                "5Xfn26Eiw3RBePh/eGiXjgv2ILZA7pnoINCa+PXI6VW6mthHQ8GJ7w+2afCGZBun\n" +
                "hxpGPEKih8YGNHBlGKUPale0pPMqI703iENZrQIDAQABAoIBAQCD2/qvk+0Lsev3\n" +
                "pNceVgzxycROYIEXLkBZU2Hydk+pxVXFFIN9fa55BDNb+mdWIHeCdIkrM+rMY/Im\n" +
                "sfF4oZEScOzHjtaJrVcDJ1gL00x+3WtjJqMGInlYFAysLbH+36xoR/IekRGqXmJi\n" +
                "1zOcAU29v6pukhQNRK0AW5RTqMTIfuQeR7Utpq4QNk8BSfDKJcyVoiTpaJ3Pv4AG\n" +
                "rPkIgtIqtmDJmp6MbMVtJA8AyxYrAiJbHntVeRddj2NkrScK/J4RG+5U/9Jj5cpB\n" +
                "VA21C7pt4lvUVuAPQ+esFLiwxBlIfW+sS5jaKCbJaeBL78xOiaLBJNWfrGR4USdu\n" +
                "k0P8o/sBAoGBAO6YeX2TDyVH/8XsSq9nPs+hu85wMqWohswG9R6oxcurVJLLKjdd\n" +
                "vqwpFaOUaQHcZr7iN2jGWqxxjFRW5qnwevk8uyXs9joSKV2C7KUdxuP9dtuqJNyl\n" +
                "+p1qN57+J69WXQNJoMKmiICK43fW89CoNucqJqrmsLzBkQiFmtR83ORxAoGBAM/M\n" +
                "xBTaVPrJYXxx10TZo+NcN5dv4FfDmK/Uz6v2F4y8xoRxpde8yeo0xj9CM8J2SX8w\n" +
                "712GLWI03m8RsYg/g2qRueK34lrA0eJ8GGUV2SJOqOQqlv1FJJVDp3658Cm0IyDT\n" +
                "CvyHJyrGmJljUhTgnwMuuzcp1wAfenWFp6OaAfb9AoGAXZMxOr29V+rH9nD4zZgZ\n" +
                "e0c8J/e69VuGGmi0I+UfRgSY88V4diRvDohCc1hWYqN1LHH+Nzpr/2u9FKrMZmPp\n" +
                "ZuyZnYM1Aoty67jYZN2rzmju/7HYKS1zf99TlyiomcyuSAbNZOn5aSiPk8Wa8/+1\n" +
                "IK5YYfh94lmsLwJvOd0KqRECgYEAxUa22L02dCh/Pm+tWRXt+0lvFXwG1gtBh5xX\n" +
                "0/97+Aa3yMFEGv6GCq0zkJa/INy/hdrlRDrAFz3t9jAsBReXIbNbcBv27wWjvIrn\n" +
                "dgA59dILkSHF2oir5HEoMK1BjbYQq3bwNTHyQy/ra6PZJyzgiVryLbqw/NLlpXDP\n" +
                "6Aer2dkCgYEAwKka1EYm5/N4krwsVvNBWD4Xgt4dtkGkQYkhyXZIqGTLFntIdVig\n" +
                "jKoQ6kaFTPaSST4kWNoXxNWvBDjarOriPa//St+l5fsEjfhjF1CfCS5aKvKfIwmP\n" +
                "jZss7kAharoCjXmxdyqPBEjJPHMts7d93olfGDGCvFrZnEfuD+zUcmU=\n" +
                "-----END RSA PRIVATE KEY-----";
        Connection conn = new Connection("10.0.0.161");
        try {
            conn.connect();// connect
            boolean flag = conn.authenticateWithPublicKey("datachecker", ase.toCharArray(), null);
            execute(conn, ". /etc/profile && . /etc/bashrc && . ~/.bash_profile && . ~/.bashrc && sc datachecker restart");
            if (flag) {
                logger.debug("login success！");
            } else {
                logger.error("login fail!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

