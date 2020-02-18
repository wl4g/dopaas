package com.wl4g.devops.doc.service;

import com.wl4g.devops.common.bean.doc.Label;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
public interface LabelService {

	PageModel list(PageModel pm, String name);

	void save(Label label);

	Label detail(Integer id);

	void del(Integer id);

	List<Label> allLabel();

}
