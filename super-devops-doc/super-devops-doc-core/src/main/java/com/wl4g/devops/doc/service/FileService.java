package com.wl4g.devops.doc.service;

import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.bean.doc.Label;
import com.wl4g.devops.page.PageModel;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
public interface FileService {

    PageModel list(PageModel pm, String name, String lang,Integer labelId);

    void save(FileChanges fileChanges);

    FileChanges detail(Integer id);

    void del(Integer id);

    List<FileChanges> getHistoryByFileCode(String fileCode);

    Map<String,FileChanges> compareWith(Integer oldChangesId, Integer newChangesId);

}
