package com.wl4g.devops.doc.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.dao.doc.FileChangesDao;
import com.wl4g.devops.dao.doc.FileLabelDao;
import com.wl4g.devops.dao.doc.LabelDao;
import com.wl4g.devops.doc.config.DocProperties;
import com.wl4g.devops.doc.service.FileService;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.support.cli.command.LocalDestroableCommand;
import com.wl4g.devops.tool.common.io.FileIOUtils;
import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.tool.common.lang.DateUtils2;
import com.wl4g.devops.tool.common.lang.TypeConverts;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getPrincipalInfo;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

/**
 * @author vjay
 * @date 2020-01-14 11:49:00
 */
@Service
public class FileServiceImpl implements FileService {
	final protected Logger log = getLogger(getClass());

	@Autowired
	private DocProperties docProperties;

	@Autowired
	private FileChangesDao fileChangesDao;

	@Autowired
	private LabelDao labelDao;

	@Autowired
	private FileLabelDao fileLabelDao;

	@Autowired
	protected DestroableProcessManager pm;

	@Override
	public PageModel list(PageModel pm, String name, String lang, Integer labelId) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		List<FileChanges> list = fileChangesDao.list(name, lang, labelId);
		pm.setRecords(list);
		return pm;
	}

	@Override
	public void save(FileChanges fileChanges) {
		Assert2.notNullOf(fileChanges, "fileChanges");
		Assert2.hasTextOf(fileChanges.getName(), "name");

		if (fileChanges.getId() == null) {
			insert(fileChanges);
		} else {
			update(fileChanges);
		}
	}

	@Override
	public void saveUpload(FileChanges fileChanges) {
		Assert2.notNullOf(fileChanges, "fileChanges");
		Assert2.hasTextOf(fileChanges.getFileCode(), "fileCode");
		Assert2.hasTextOf(fileChanges.getContent(), "content");
		IamPrincipalInfo info = getPrincipalInfo();
		fileChanges.preInsert();
		fileChanges.setCreateBy(TypeConverts.parseIntOrNull(info.getPrincipalId()));
		fileChanges.setUpdateBy(TypeConverts.parseIntOrNull(info.getPrincipalId()));
		fileChanges.setIsLatest(1);
		fileChanges.setAction("add");
		fileChanges.setType("md");
		fileChanges.setShareType(-1);
		fileChangesDao.insertSelective(fileChanges);
		// label
		List<Integer> labelIds = fileChanges.getLabelIds();
		if (Objects.nonNull(labelIds) && labelIds.size() > 0) {
			fileLabelDao.insertBatch(labelIds, fileChanges.getId());
		}
	}

	@Override
	public FileChanges detail(Integer id) {
		FileChanges fileChanges = fileChangesDao.selectByPrimaryKey(id);
		List<Integer> labelIds = labelDao.selectLabelIdsByFileId(fileChanges.getId());
		fileChanges.setLabelIds(labelIds);
		// read content from file
		file2String(fileChanges);
		return fileChanges;
	}

	private void insert(FileChanges fileChanges) {
		IamPrincipalInfo info = getPrincipalInfo();
		fileChanges.preInsert();
		fileChanges.setCreateBy(TypeConverts.parseIntOrNull(info.getPrincipalId()));
		fileChanges.setUpdateBy(TypeConverts.parseIntOrNull(info.getPrincipalId()));
		fileChanges.setIsLatest(1);
		fileChanges.setAction("add");
		fileChanges.setShareType(-1);
		fileChanges.setFileCode(UUID.randomUUID().toString().replaceAll("-", ""));
		String path = writeContentIntoFile(fileChanges);
		fileChanges.setContent(path);
		fileChangesDao.insertSelective(fileChanges);
		// label
		List<Integer> labelIds = fileChanges.getLabelIds();
		if (Objects.nonNull(labelIds) && labelIds.size() > 0) {
			fileLabelDao.insertBatch(labelIds, fileChanges.getId());
		}
	}

	private void update(FileChanges fileChanges) {
		FileChanges oldFileChanges = fileChangesDao.selectLastByFileCode(fileChanges.getFileCode());
		fileChangesDao.updateIsLatest(fileChanges.getFileCode());
		fileChanges.setId(null);
		fileChanges.preInsert();
		fileChanges.setIsLatest(1);
		fileChanges.setAction("edit");
		fileChanges.setPasswd(oldFileChanges.getPasswd());
		fileChanges.setShareType(oldFileChanges.getShareType());
		String path = writeContentIntoFile(fileChanges);
		fileChanges.setContent(path);
		fileChangesDao.insertSelective(fileChanges);
		// label
		List<Integer> labelIds = fileChanges.getLabelIds();
		if (Objects.nonNull(labelIds) && labelIds.size() > 0) {
			fileLabelDao.insertBatch(labelIds, fileChanges.getId());
		}
	}

	private String writeContentIntoFile(FileChanges fileChanges) {
		String subPath = "/" + fileChanges.getFileCode() + "/";
		String fileName = DateUtils2.formatDate(fileChanges.getUpdateDate(), "yyyyMMddHHmmss") + "." + fileChanges.getType();
		File file = new File(docProperties.getFilePath(subPath + fileName));
		FileIOUtils.ensureFile(file);
		FileIOUtils.writeFile(file, fileChanges.getContent(), false);
		return subPath + fileName;
	}

	@Override
	public void del(Integer id) {
		FileChanges file = new FileChanges();
		file.setId(id);
		file.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		fileChangesDao.updateByPrimaryKeySelective(file);
	}

	@Override
	public List<FileChanges> getHistoryByFileCode(String fileCode) {
		return fileChangesDao.selectByFileCode(fileCode);
	}

	@Override
	public FileChanges getLastByFileCode(String fileCode) {
		FileChanges fileChange = fileChangesDao.selectLastByFileCode(fileCode);
		file2String(fileChange);
		return fileChange;
	}

	@Override
	public Map<String, FileChanges> compareWith(Integer oldChangesId, Integer newChangesId) {
		Map<String, FileChanges> result = new HashMap<>();
		FileChanges oldFileChanges = fileChangesDao.selectByPrimaryKey(oldChangesId);
		file2String(oldFileChanges);
		result.put("oldFileChanges", oldFileChanges);
		if (Objects.nonNull(newChangesId)) {
			FileChanges newFileChanges = fileChangesDao.selectByPrimaryKey(newChangesId);
			file2String(newFileChanges);
			result.put("newFileChanges", newFileChanges);
		} else {
			FileChanges newFileChanges = fileChangesDao.selectLastByFileCode(oldFileChanges.getFileCode());
			file2String(newFileChanges);
			result.put("newFileChanges", newFileChanges);
		}
		return result;
	}

	@Override
	public Map<String, Object> upload(MultipartFile file) {
		Map<String, Object> result = new HashMap<>();
		Date now = new Date();
		String fileCode = UUID.randomUUID().toString().replaceAll("-", "");
		String fileName = file.getOriginalFilename();// 文件名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));// 后缀名
		String newFileName = DateUtils2.formatDate(now, "yyyyMMddHHmmss");
		String subPath = "/" + fileCode + "/";
		String path = "";
		if (".md".equalsIgnoreCase(suffixName)) {
			newFileName = newFileName + suffixName;
			path = subPath + newFileName;
			saveFile(file, docProperties.getFilePath(path));
		} else if (".docx".equalsIgnoreCase(suffixName)) {
			String tempFilePath = subPath + newFileName + suffixName;
			path = subPath + newFileName + ".md";
			saveFile(file, docProperties.getFilePath(tempFilePath));
			// pandoc
			String command = "pandoc " + docProperties.getFilePath(tempFilePath) + " -o " + docProperties.getFilePath(path);

			DestroableCommand cmd = new LocalDestroableCommand(command, null, 300000L);
			try {
				pm.execWaitForComplete(cmd);
			} catch (Exception e) {
				throw new UnsupportedOperationException(
						String.format("execute pandoc fail: suffix=%s , fileName=%s", suffixName, fileName));
			}
		} else {
			throw new UnsupportedOperationException("Unsupport file type:" + suffixName);
		}
		result.put("path", path);
		result.put("fileCode", fileCode);
		return result;
	}

	@Override
	public String shareFile(Integer id, boolean isEncrypt) {
		String passwd = generatePasswd();
		FileChanges fileChanges = new FileChanges();
		fileChanges.setId(id);
		if (isEncrypt) {// Encrypt File
			fileChanges.setShareType(1);
			fileChanges.setPasswd(passwd);
		} else {// not Encrypt File
			fileChanges.setShareType(0);
		}
		fileChangesDao.updateByPrimaryKeySelective(fileChanges);
		return passwd;
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

	private void file2String(FileChanges fileChanges) {
		File file1 = new File(docProperties.getFilePath(fileChanges.getContent()));
		if (file1.exists()) {
			try {
				String s = FileIOUtils.readFileToString(file1, "UTF-8");
				fileChanges.setContent(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String generatePasswd() {
		String str = "abcdefghijklmnopqrstuvwxyz0123456789";
		String uuid = new String();
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			uuid += ch;
		}
		return uuid;
	}

}
