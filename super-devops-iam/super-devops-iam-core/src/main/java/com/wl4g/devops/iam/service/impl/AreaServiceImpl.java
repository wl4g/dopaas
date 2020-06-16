package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.iam.Area;
import com.wl4g.devops.dao.iam.AreaDao;
import com.wl4g.devops.iam.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author vjay
 * @date 2020-05-25 18:06:00
 */
@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaDao areaDao;

	@Override
	public List<Area> getAreaTree() {
		List<Area> total = getTotal();
		List<Area> tops = getTops(total);
		for (Area top : tops) {
			getChildren(total, top);
		}
		return tops;
	}

	public void getChildren(List<Area> total, Area top) {
		for (Area area : total) {
			if (top.getId().equals(area.getParentId())) {
				getChildrenOrCreate(top).add(area);
				getChildren(total, area);
			}
		}
	}

	private List<Area> getTotal() {
		return areaDao.getTotal();
	}

	private List<Area> getTops(List<Area> areas) {
		List<Area> list = new ArrayList<>();
		for (Area area : areas) {
			if (Objects.nonNull(area.getLevel()) && area.getLevel() == 1) {
				list.add(area);
			}
		}
		return list;
	}

	private List<Area> getChildrenOrCreate(Area area) {
		if (Objects.isNull(area.getChildren())) {
			List<Area> children = new ArrayList<>();
			area.setChildren(children);
			return children;
		} else {
			return area.getChildren();
		}
	}

}
