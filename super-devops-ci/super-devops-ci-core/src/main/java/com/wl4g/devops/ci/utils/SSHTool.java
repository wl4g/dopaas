/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.ci.utils;

import ch.ethz.ssh2.*;
import com.google.common.base.Charsets;
import com.wl4g.devops.shell.utils.ShellContextHolder;
import org.apache.log4j.Logger;

import java.io.*;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * SSH connection utility tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class SSHTool extends CommandUtils {
    final private static Logger log = Logger.getLogger(SSHTool.class);

    /**
     * execute with local rsa file
     *
     * @param host
     * @param command
     * @return
     */
    public static String execute(String host, String userName, String command) throws Exception {
        Connection conn = null;
        try {
            boolean flag = false;
            conn = new Connection(host);
            conn.connect();// connect
            String userHome = System.getProperties().getProperty("user.home");
            flag = conn.authenticateWithPublicKey(userName, new File(userHome + "/.ssh/id_rsa"), null);
            if (flag) {
                log.debug("login success！");
                return execute(conn, command);
            } else {
                log.error("login fail!");
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

    public static String execute(String host, String userName, String command, char[] rsa) throws Exception {
        log.info("exce command:" + command);
        ShellContextHolder.printfQuietly("exce command:" + command);
        Connection conn = null;
        try {
            boolean flag = false;
            conn = new Connection(host);
            conn.connect();// connect
            flag = conn.authenticateWithPublicKey(userName, rsa, null);
            if (flag) {
                log.debug("login success！");
                String result = execute(conn, command);
                // logger.info(result);
                // ShellConsoleHolder.printfQuietly(result);
                return result;
            } else {
                log.error("login fail!");
                throw new RuntimeException("login fail");
            }
        } catch (Exception e) {
            ShellContextHolder.printfQuietly("exce fail:" + command + "\n" + e.getMessage());
            throw e;
        } finally {
            if (null != conn) {
                conn.close();
            }
        }
    }

    /**
     * Upload execuable package to server
     *
     * @param f                     文件对象
     * @param length                文件大小
     * @param remoteTargetDirectory 上传路径
     * @param mode                  默认为null
     */
    public static String uploadFile(String host, String userName, char[] rsa, File f, String remoteTargetDirectory) {
        log.info("upload file begin: " + f.getAbsolutePath() + " to " + remoteTargetDirectory);

        ShellContextHolder.printfQuietly("upload file: " + f.getAbsolutePath() + " to " + remoteTargetDirectory);
        Connection conn = null;
        SCPOutputStream os = null;
        FileInputStream fis = null;
        try {
            boolean flag = false;
            conn = new Connection(host);
            conn.connect();
            flag = conn.authenticateWithPublicKey(userName, rsa, null);
            if (flag) {
                log.debug("login success！");
                SCPClient scpClient = new SCPClient(conn);
                os = scpClient.put(f.getName(), f.length(), remoteTargetDirectory, "0744");
                byte[] b = new byte[4096];
                fis = new FileInputStream(f);
                int i;
                while ((i = fis.read(b)) != -1) {
                    os.write(b, 0, i);
                }
                os.flush();
                log.info("upload file success: " + f.getAbsolutePath() + " to " + remoteTargetDirectory);
                ShellContextHolder.printfQuietly("upload file success: " + f.getAbsolutePath() + " to " + remoteTargetDirectory);
            } else {
                log.error("login fail!");
                throw new RuntimeException("login fail");
            }
        } catch (IOException e) {
            log.error("upload file fail: " + f.getAbsolutePath() + " to " + remoteTargetDirectory);
            ShellContextHolder.printfQuietly("upload file fail: " + f.getAbsolutePath() + " to " + remoteTargetDirectory);
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != os) {
                    os.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch (Exception e) {
            }
        }
        return "upload file success";
    }

    private static String execute(Connection conn, String cmd) throws IOException {
        // logger.info("command=" + cmd);

        String result = EMPTY;
        Session session = null;
        try {
            session = conn.openSession();
            session.execCommand(cmd); // execute

            result = printf(session.getStdout());

            // if result blank, get error msg
            if (isBlank(result)) {
                result = printf(session.getStderr());
                if (isNotBlank(result)) {
                    throw new RuntimeException(result);
                }
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (null != session) {
                session.close();
            }
        }

        // logger.info(result);
        return result;
    }

    private static String printf(InputStream in) {
        StringBuffer buffer = new StringBuffer();
        InputStream stdout = new StreamGobbler(in);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stdout, Charsets.UTF_8))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (log.isInfoEnabled()) {
                    log.info(line);
                }
                buffer.append(line);
                buffer.append("\n");

                ShellContextHolder.printfQuietly(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

}