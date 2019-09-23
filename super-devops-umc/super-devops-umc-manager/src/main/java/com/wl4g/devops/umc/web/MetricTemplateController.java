package com.wl4g.devops.umc.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.bean.umc.MetricTemplate;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.umc.MetricTemplateDao;
import com.wl4g.devops.umc.service.MetricTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 11:44:00
 */
@RestController
@RequestMapping("/metric")
public class MetricTemplateController extends BaseController {

	@Autowired
	private MetricTemplateDao metricTemplateDao;

	@Autowired
	private MetricTemplateService metricTemplateService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(String metric, String classify, CustomPage customPage) {
		RespBase<Object> resp = RespBase.create();
		Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
		Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
		Page<CustomPage> page = PageHelper.startPage(pageNum, pageSize, true);
		List<MetricTemplate> list = metricTemplateDao.list(metric, classify);

		customPage.setPageNum(pageNum);
		customPage.setPageSize(pageSize);
		customPage.setTotal(page.getTotal());
		resp.getData().put("page", customPage);
		resp.getData().put("list", list);
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody MetricTemplate metricTemplate) {
		log.info("into MetricTemplateController.save prarms::" + "metricTemplate = {} ", metricTemplate);
		Assert.notNull(metricTemplate, "metricTemplate is null");
		Assert.hasText(metricTemplate.getClassify(), "classify is null");
		Assert.hasText(metricTemplate.getMetric(), "metric is null");
		RespBase<Object> resp = RespBase.create();
		metricTemplateService.save(metricTemplate);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		MetricTemplate metricTemplate = metricTemplateDao.selectByPrimaryKey(id);
		resp.getData().put("metricTemplate", metricTemplate);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		log.info("into MetricTemplateController.del prarms::" + "id = {} ", id);
		RespBase<Object> resp = RespBase.create();
		metricTemplateService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getByClassify")
	public RespBase<?> getByClassify(String classify) {
		RespBase<Object> resp = RespBase.create();
		List<MetricTemplate> metricTemplate = metricTemplateService.getByClassify(classify);
		resp.getData().put("list", metricTemplate);
		return resp;
	}

}
