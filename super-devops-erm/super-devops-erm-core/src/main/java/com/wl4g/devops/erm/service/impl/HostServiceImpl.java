/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.erm.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Charsets;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.erm.Host;
import com.wl4g.devops.common.bean.erm.HostSsh;
import com.wl4g.devops.common.bean.erm.Ssh;
import com.wl4g.devops.dao.erm.HostDao;
import com.wl4g.devops.dao.erm.HostSshDao;
import com.wl4g.devops.dao.erm.SshDao;
import com.wl4g.devops.erm.config.FsProperties;
import com.wl4g.devops.erm.service.HostService;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.command.RemoteDestroableCommand;
import com.wl4g.devops.tool.common.cli.ssh2.JschHolder;
import com.wl4g.devops.tool.common.cli.ssh2.SSH2Holders;
import com.wl4g.devops.tool.common.io.FileIOUtils;
import com.wl4g.devops.tool.common.lang.Assert2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostServiceImpl implements HostService {

    @Autowired
    private HostDao appHostDao;

    @Autowired
    private HostSshDao hostSshDao;

    @Autowired
    private FsProperties fsProperties;

    @Autowired
    private SshDao sshDao;

    @Autowired
    protected DestroableProcessManager pm;

    final private static String IMPORT_HOST_TEMPLATE = "/import_host_template/";
    final private static String IMPORT_HOST_DATA = "/import_host_data/";

    @Override
    public List<Host> list(String name, String hostname, Integer idcId) {
        return appHostDao.list(getRequestOrganizationCodes(), name, hostname, idcId);
    }

    @Override
    public PageModel page(PageModel pm, String name, String hostname, Integer idcId) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(appHostDao.list(getRequestOrganizationCodes(), name, hostname, idcId));
        return pm;
    }

    @Override
    public void save(Host host) {
        if (isNull(host.getId())) {
            host.preInsert(getRequestOrganizationCode());
            insert(host);
        } else {
            host.preUpdate();
            update(host);
        }
    }

    private void insert(Host host) {
        appHostDao.insertSelective(host);
        List<Integer> sshIds = host.getSshIds();
        if (!CollectionUtils.isEmpty(sshIds)) {
            List<HostSsh> hostSshs = new ArrayList<>();
            for (Integer sshId : sshIds) {
                HostSsh hostSsh = new HostSsh();
                hostSsh.preInsert();
                hostSsh.setHostId(host.getId());
                hostSsh.setSshId(sshId);
                hostSshs.add(hostSsh);
            }
            hostSshDao.insertBatch(hostSshs);
        }
    }

    private void update(Host host) {
        appHostDao.updateByPrimaryKeySelective(host);
        hostSshDao.deleteByHostId(host.getId());
        List<Integer> sshIds = host.getSshIds();
        if (!CollectionUtils.isEmpty(sshIds)) {
            List<HostSsh> hostSshs = new ArrayList<>();
            for (Integer sshId : sshIds) {
                HostSsh hostSsh = new HostSsh();
                hostSsh.preInsert();
                hostSsh.setHostId(host.getId());
                hostSsh.setSshId(sshId);
                hostSshs.add(hostSsh);
            }
            hostSshDao.insertBatch(hostSshs);
        }
    }


    public Host detail(Integer id) {
        Assert.notNull(id, "id is null");
        Host host = appHostDao.selectByPrimaryKey(id);
        List<Integer> integers = hostSshDao.selectByHostId(id);
        host.setSshIds(integers);
        return host;
    }

    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        Host host = new Host();
        host.setId(id);
        host.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        appHostDao.updateByPrimaryKeySelective(host);
    }


    @Override
    public ResponseEntity<FileSystemResource> createAndDownloadTemplate(Integer idcId, String organizationCode) throws IOException {
        long now = System.currentTimeMillis();
        File file = new File(fsProperties.getBaseFilePath() + IMPORT_HOST_TEMPLATE + now + ".csv");
        String head = "Hostname,SSH Key,username(manager user), password,idcId=" + idcId + ",organizationCode=" + organizationCode + "\n";
        String body = "owner-nodeq,sso,root,123456";
        FileIOUtils.writeFile(file, head + body, Charsets.UTF_8, false);
        return downloadFile(file);
    }

    private ResponseEntity<FileSystemResource> downloadFile(File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        String suffixName = file.getName().substring(file.getName().lastIndexOf("."));// 后缀名
        headers.add("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + suffixName);
        headers.add("Pragma", "no-cache");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }

    @Override
    public Map<String, Object> importHost(MultipartFile file, Integer force, Integer sshAutoCreate) throws IOException {
        Map<String, Object> result = new HashMap<>();
        long now = System.currentTimeMillis();
        String fileName = file.getOriginalFilename();// 文件名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));// 后缀名
        fileName = IMPORT_HOST_DATA + now + suffixName;// 新文件名
        String path = fsProperties.getBaseFilePath() + IMPORT_HOST_DATA + fileName;
        saveFile(file, path);

        List<String> lines = FileIOUtils.readLines(new File(path), "UTF-8");
        Assert2.notEmptyOf(lines, "lines");
        String[] split = lines.get(0).split(",");
        Assert2.isTrue(split.length == 4, "template error");
        String[] idcStr = split[4].split("=");
        Assert2.isTrue(idcStr.length == 2, "template error");
        String idcId = idcStr[1];
        String[] organizationCodeStr = split[5].split("=");
        Assert2.isTrue(organizationCodeStr.length == 2, "template error");
        String organizationCode = organizationCodeStr[1];

        int success = 0;
        int fail = 0;
        for (int i = 1; i < lines.size(); i++) {
            try {
                insertHost(idcId, organizationCode, lines.get(i));
                success++;
            } catch (Exception e) {
                fail++;
                if (Objects.isNull(force) || 1 != force) {
                    break;
                }
            }
        }

        result.put("success", success);
        result.put("fail", fail);

        return result;
    }

    private void insertHost(String idcId, String organizationCode, String line) throws Exception {

        if (StringUtils.isBlank(line)) {
            return;
        }
        String[] split = line.split(",");
        String hostname = split[0];
        String ssh = split[1];
        String username = split[2];
        String password = split[3];

        Host host = new Host();
        host.setHostname(hostname);
        host.setName(hostname);
        List<Integer> sshIds = createOrGetSSH(ssh, hostname, username, password);
        host.setSshIds(sshIds);
        host.setId(Integer.valueOf(idcId));
        host.setOrganizationCode(organizationCode);
        insert(host);


    }

    private List<Integer> createOrGetSSH(String sshCell, String hostname, String username, String password) throws Exception {
        if (StringUtils.isBlank(sshCell)) {
            return null;
        }
        List<Integer> sshIds = new ArrayList<>();
        String[] split = sshCell.split("|");
        for (String sshname : split) {
            Ssh ssh = sshDao.selectByName(sshname);
            if (Objects.nonNull(ssh)) {
                sshIds.add(ssh.getId());
            } else {
                //TODO create ssh
                ssh = new Ssh();
                ssh.preInsert();
                ssh.setName(username);
                ssh.setUsername(username);
                ssh.setAuthType("2");
                SSH2Holders.Ssh2KeyPair ssh2KeyPair = SSH2Holders.getInstance(JschHolder.class).generateKeypair(SSH2Holders.AlgorithmType.RSA, "generateBySystem");
                ssh.setSshKey(ssh2KeyPair.getPrivateKey());
                ssh.setSshKeyPub(ssh2KeyPair.getPublicKey());
                sshDao.insertSelective(ssh);
                createUserAndAddSSHKey(hostname, username, password,ssh2KeyPair.getPrivateKey());
                sshIds.add(ssh.getId());
            }
        }
        return sshIds;
    }

    private void createUserAndAddSSHKey(String hostname, String username, String password,String sshKey) throws Exception {
        StringBuilder remoteCommand = new StringBuilder();
        remoteCommand.append("useradd username\n");
        remoteCommand.append("echo \""+sshKey+"\" >> /home/"+ username+"/.ssh/authorized_keys");
        RemoteDestroableCommand cmd = new RemoteDestroableCommand(remoteCommand.toString(), 10000, username, hostname,
                password);
        // Execution command.
        String outmsg = pm.execWaitForComplete(cmd);
    }


    private void saveFile(MultipartFile file, String localPath) {
        Assert.notNull(file, "文件为空");
        File dest = new File(localPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}