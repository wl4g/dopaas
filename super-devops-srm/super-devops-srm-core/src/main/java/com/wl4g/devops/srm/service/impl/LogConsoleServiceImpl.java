package com.wl4g.devops.srm.service.impl;

import com.wl4g.devops.common.bean.srm.Log;
import com.wl4g.devops.common.bean.srm.Querycriteria;
import com.wl4g.devops.common.bean.srm.RequestBean;
import com.wl4g.devops.common.bean.srm.Storage;
import com.wl4g.devops.common.constants.SRMDevOpsConstants;
import com.wl4g.devops.srm.dao.LogDao;
import com.wl4g.devops.srm.service.LogConsoleService;
import com.wl4g.devops.common.utils.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@Service
public class LogConsoleServiceImpl implements LogConsoleService {

	@Resource
	private LogDao logDao;

	@Override
	public Object consoleLog(RequestBean requestBean) throws Exception {
		String index = requestBean.getIndex();
		String date;
		Integer level = requestBean.getLevel();
		Integer interval = requestBean.getInterval();
		String startDate = requestBean.getStartDate();
		String endDate = requestBean.getEndDate();
		boolean flag = requestBean.isFlag();
		List<Querycriteria> queryList = requestBean.getQueryList();
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			date = DateUtils.getNowTime(DateUtils.YMD);
			endDate = DateUtils.getCurrentTimeBySecound(-5);
			if (interval == null) {
				if (StringUtils.isEmpty(startDate)) {
					startDate = DateUtils.getCurrentTimeBySecound(-15);
				}
			} else {
				switch (interval) {
				case 1:
					startDate = DateUtils.getCurrentTime(-1);
					break;
				case 2:
					startDate = DateUtils.getCurrentTime(-15);
					break;
				case 3:
					startDate = DateUtils.getCurrentTimeByHour(-1);
					break;
				case 4:
					startDate = DateUtils.getCurrentTimeByHour(-4);
					break;
				}
			}
		} else {
			date = DateUtils.ymdhmsToymd(startDate);
		}
		index = index + "-" + date;
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types("doc");
		BoolQueryBuilder boolQueryBuilder = boolQuery();
		List<Querycriteria> list1 = new ArrayList<>();// 查询包含条件
		List<Querycriteria> list2 = new ArrayList<>();// 查询不包含条件
		queryList.forEach(u -> {
			if (u.isEnable()) {
				list1.add(u);
			} else {
				list2.add(u);
			}
		});
		for (Querycriteria qt : list1) {
			String con = qt.getValue().trim();
			if (!StringUtils.isEmpty(con)) {
				boolQueryBuilder.must(matchQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, con));
			}
		}
		BoolQueryBuilder boolQueryBuilder1 = boolQuery();
		for (int i = level - 1; i < SRMDevOpsConstants.LOG_LEVEL.size(); i++) {
			boolQueryBuilder1.should(matchQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, SRMDevOpsConstants.LOG_LEVEL.get(i)));
		}
		boolQueryBuilder.must(boolQueryBuilder1);
		RangeQueryBuilder lt = rangeQuery("@timestamp").timeZone(DateTimeZone.UTC.toString()).gt(DateUtils.toUtc(startDate))
				.lt(DateUtils.toUtc(endDate));
		boolQueryBuilder.filter(lt);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(boolQueryBuilder);
		sourceBuilder.from(0);
		sourceBuilder.size(10000);
		sourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));
		searchRequest.source(sourceBuilder);
		System.out.println(searchRequest.toString());
		List<String> list = new ArrayList<>();
		List<Log> logList = logDao.findAll(searchRequest);
		for (Log log : logList) {
			list.add(log.getMessage());
		}
		if (!list2.isEmpty()) {
			list = list.stream().filter(u -> {
				for (Querycriteria qt : list2) {
					String val = qt.getValue().trim();
					if (!StringUtils.isEmpty(val)) {
						if (u.contains(val)) {
							return false;
						}
					}
				}
				return true;

			}).collect(Collectors.toList());
		}
		if (flag) {
			Storage storage = new Storage();
			storage.setKey(endDate);
			storage.setValue(list);
			// return storage;
			return storage;
		} else {
			StringBuilder sb = new StringBuilder();
			list.forEach(u -> sb.append(u).append(System.lineSeparator()));
			return sb.toString();
		}
	}

}
