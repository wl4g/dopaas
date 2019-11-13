package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.VcsService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.dao.ci.VcsDao;
import com.wl4g.devops.page.PageModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class VcsServcieImpl implements VcsService {

	@Autowired
	private VcsDao vcsDao;

	@Override
	public PageModel list(PageModel pm) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(vcsDao.list());
		return pm;
	}

	@Override
	public void save(Vcs vcs) {
		if (vcs.getId() == null) {
			vcs.preInsert();
			insert(vcs);
		} else {
			vcs.preUpdate();
			update(vcs);
		}
	}

	private void insert(Vcs vcs) {
		vcsDao.insertSelective(vcs);
	}

	private void update(Vcs vcs) {
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public void del(Integer id) {
		Vcs vcs = new Vcs();
		vcs.setId(id);
		vcs.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public Vcs detail(Integer id) {
		return vcsDao.selectByPrimaryKey(id);
	}

}
