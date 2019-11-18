package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
public interface VcsService {

	PageModel list(PageModel pm);

	void save(Vcs vcs);

	void del(Integer id);

	Vcs detail(Integer id);

	List<Vcs> all();

}
