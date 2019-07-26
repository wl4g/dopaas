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
package com.wl4g.devops.ci.provider;

// import java.util.List;
//
// import com.wl4g.devops.ci.config.DeployProperties;
// import com.wl4g.devops.ci.service.DependencyService;
// import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
// import com.wl4g.devops.common.bean.scm.AppInstance;

/**
 * Native jar deployments provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:28:00
 */
// public class NativeJarDeployProvider extends BasedDeployProvider {
//
// public NativeJarDeployProvider(DependencyService dependencyService,
// DeployProperties config, Integer projectId, String path,
// String url, String branch, String alias, String tarPath, List<AppInstance>
// instances, List<TaskHistoryDetail> taskDetails) {
// super(dependencyService, config, projectId, path, url, branch, alias,
// tarPath, instances, taskDetails);
// }
//
// @Override
// public void execute() throws Exception { // chekcout
// if (checkGitPahtExist()) {
// checkOut(path, branch);
// } else {
// clone(path, url, branch);
// } // build build(path);
//
// // scp to server for(AppInstance instance : instances){
//
// // scp to server
// scp(path + "/" + tarPath, instance.getServerAccount() + "@" +
// instance.getHost(), instance.getBasePath());
//
// // stop server
// // stop(instance.getHost(),instance.getServerAccount(),alias);
//
// // restart server
// start(instance.getHost(), instance.getServerAccount(), alias, tarName);
// }log.info("Done");
//
// }
//
// public String stop(String host, String userName, String module) throws
// Exception {
// String command = "for i in `jps|grep " + module + " |awk '{print $1}' `;
// do kill -9 $i ; done;";
// try {
// ConnectLinuxCommand.execute(host, userName, command);
// } catch (Exception e) {
//
// }
// return null;
// }
//
// public String start(String host, String userName, String module, String
// targetName) throws Exception {
// String command = "nohup java
// -Djava.ext.dirs=/root/webapps/dataflux-oper-master-bin/libs -cp
// /root/webapps/dataflux-oper-master-bin/libs/datafluxOper.jar
// com.cn7782.devops.DatafluxOper >/dev/null & ";
// ConnectLinuxCommand.execute(host, userName, command);
// }
//
// }