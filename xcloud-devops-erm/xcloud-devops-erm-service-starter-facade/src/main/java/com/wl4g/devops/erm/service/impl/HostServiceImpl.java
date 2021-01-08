/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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


import com.google.common.base.Charsets;
import com.wl4g.component.common.cli.ssh2.JschHolder;
import com.wl4g.component.common.cli.ssh2.SSH2Holders;
import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.support.cli.DestroableProcessManager;
import com.wl4g.component.support.cli.command.RemoteDestroableCommand;
import com.wl4g.devops.common.bean.erm.Host;
import com.wl4g.devops.common.bean.erm.HostSsh;
import com.wl4g.devops.common.bean.erm.SshBean;
import com.wl4g.devops.erm.config.FsProperties;
import com.wl4g.devops.erm.data.HostDao;
import com.wl4g.devops.erm.data.HostSshDao;
import com.wl4g.devops.erm.data.SshDao;
import com.wl4g.devops.erm.service.HostService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.erm.util.SshkeyUtils.encryptSshkeyToHex;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostServiceImpl implements HostService {

	final protected SmartLogger log = getLogger(getClass());

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

	@Value("${cipher-key}")
	protected String cipherKey;

	final private static String IMPORT_HOST_TEMPLATE = "/import_host_template/";
	final private static String IMPORT_HOST_DATA = "/import_host_data/";

	@Override
	public List<Host> list(String name, String hostname, Long idcId) {
		return appHostDao.list(getRequestOrganizationCodes(), name, hostname, idcId);
	}

	@Override
	public PageHolder<Host> page(PageHolder<Host> pm, String name, String hostname, Long idcId) {
		pm.startPage();
		pm.setRecords(appHostDao.list(getRequestOrganizationCodes(), name, hostname, idcId));
		return pm;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Host host) {
		if (isNull(host.getId())) {
			host.preInsert(getRequestOrganizationCode());
			insert(host);
		} else {
			host.preUpdate();
			update(host);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void insert(Host host) {
		appHostDao.insertSelective(host);
		List<Long> sshIds = host.getSshIds();
		if (!CollectionUtils.isEmpty(sshIds)) {
			List<HostSsh> hostSshs = new ArrayList<>();
			for (Long sshId : sshIds) {
				HostSsh hostSsh = new HostSsh();
				hostSsh.preInsert();
				hostSsh.setHostId(host.getId());
				hostSsh.setSshId(sshId);
				hostSshs.add(hostSsh);
			}
			hostSshDao.insertBatch(hostSshs);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(Host host) {
		appHostDao.updateByPrimaryKeySelective(host);
		hostSshDao.deleteByHostId(host.getId());
		List<Long> sshIds = host.getSshIds();
		if (!CollectionUtils.isEmpty(sshIds)) {
			List<HostSsh> hostSshs = new ArrayList<>();
			for (Long sshId : sshIds) {
				HostSsh hostSsh = new HostSsh();
				hostSsh.preInsert();
				hostSsh.setHostId(host.getId());
				hostSsh.setSshId(sshId);
				hostSshs.add(hostSsh);
			}
			hostSshDao.insertBatch(hostSshs);
		}
	}

	public Host detail(Long id) {
		Assert.notNull(id, "id is null");
		Host host = appHostDao.selectByPrimaryKey(id);
		List<Long> integers = hostSshDao.selectByHostId(id);
		host.setSshIds(integers);
		return host;
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		Host host = new Host();
		host.setId(id);
		host.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		appHostDao.updateByPrimaryKeySelective(host);
	}

	@Override
	public ResponseEntity<FileSystemResource> createAndDownloadTemplate(Long idcId, String organizationCode) throws IOException {
		long now = System.currentTimeMillis();
		File file = new File(fsProperties.getBaseFilePath() + IMPORT_HOST_TEMPLATE + now + ".csv");
		String head = "Hostname,SSH Key,username(manager user), password,idcId=" + idcId + ",organizationCode=" + organizationCode
				+ "\n";
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
		Assert2.isTrue(split.length == 6, "template error");
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

	@Transactional(rollbackFor = Exception.class)
	public void insertHost(String idcId, String organizationCode, String line) throws Exception {

		if (StringUtils.isBlank(line)) {
			return;
		}
		String[] split = line.split(",");
		Assert2.isTrue(split.length > 0, "template error");
		String hostname = split[0];

		String ssh = null;
		if (split.length > 1) {
			ssh = split[1];
		}
		String username = null;
		String password = null;
		if (split.length == 4) {
			username = split[2];
			password = split[3];
		}

		Host host = new Host();
		host.preInsert();
		host.setHostname(hostname);
		host.setName(hostname);
		List<Long> sshIds = null;
		if (StringUtils.isNotBlank(ssh)) {
			sshIds = createOrGetSSH(ssh, hostname, username, password);
		}

		host.setSshIds(sshIds);
		host.setIdcId(Long.valueOf(idcId));
		host.setOrganizationCode(organizationCode);
		insert(host);

	}

	@Transactional(rollbackFor = Exception.class)
	public List<Long> createOrGetSSH(String sshCell, String hostname, String username, String password) throws Exception {
		if (StringUtils.isBlank(sshCell)) {
			return null;
		}
		List<Long> sshIds = new ArrayList<>();
		String[] split = sshCell.split("\\|");
		for (String sshname : split) {
			SshBean ssh = sshDao.selectByName(sshname);
			if (Objects.nonNull(ssh)) {
				sshIds.add(ssh.getId());
			} else {
				if (StringUtils.isNoneBlank(hostname, password)) {
					// TODO create ssh
					ssh = new SshBean();
					ssh.preInsert();
					ssh.setName(sshname);
					ssh.setUsername(sshname);
					ssh.setAuthType("2");
					SSH2Holders.Ssh2KeyPair ssh2KeyPair = SSH2Holders.getInstance(JschHolder.class)
							.generateKeypair(SSH2Holders.AlgorithmType.RSA, "generateBySystem");
					ssh.setSshKey(ssh2KeyPair.getPrivateKey());
					ssh.setSshKeyPub(ssh2KeyPair.getPublicKey());
					if (StringUtils.isNotBlank(ssh.getSshKey())) {
						ssh.setSshKey(encryptSshkeyToHex(cipherKey, ssh.getSshKey()));
					}
					createUserAndAddSSHKey(hostname, sshname, username, password, ssh2KeyPair.getPublicKey());
					sshDao.insertSelective(ssh);
					sshIds.add(ssh.getId());
				}
			}
		}
		return sshIds;
	}

	private void createUserAndAddSSHKey(String hostname, String sshname, String username, String password, String publicKey)
			throws Exception {
		StringBuilder remoteCommand = new StringBuilder();
		remoteCommand.append("useradd " + sshname + " && ");
		remoteCommand.append("su - " + sshname + " -c \"mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '" + publicKey
				+ "' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys\"");
		RemoteDestroableCommand cmd = new RemoteDestroableCommand(remoteCommand.toString(), 10000, username, hostname, password);
		// Execution command.
		String outmsg = pm.execWaitForComplete(cmd);
		log.info("Testing SSH2 connection result: {}", outmsg);
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