package com.wl4g.devops.doc.service;

import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.bean.doc.Share;
import com.wl4g.devops.page.PageModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
public interface DocService {

	PageModel list(PageModel pm, String name, String lang, Integer labelId);

	void save(FileChanges fileChanges);

	void saveUpload(FileChanges fileChanges);

	FileChanges detail(Integer id);

	void del(Integer id);

	List<FileChanges> getHistoryByDocCode(String docCode);

	Map<String, FileChanges> compareWith(Integer oldChangesId, Integer newChangesId);

	Map<String, Object> upload(MultipartFile file);

	Share shareDoc(Integer id, boolean isEncrypt, boolean isForever, Integer day, Date expireTime);

	FileChanges getLastByDocCode(String docCode);

}
