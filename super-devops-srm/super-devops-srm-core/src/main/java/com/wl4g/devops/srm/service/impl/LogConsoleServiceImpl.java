/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.srm.service.impl;

import com.wl4g.devops.common.bean.srm.Log;
import com.wl4g.devops.common.bean.srm.Querycriteria;
import com.wl4g.devops.common.bean.srm.QueryLogModel;
import com.wl4g.devops.common.constants.SRMDevOpsConstants;
import com.wl4g.devops.common.utils.DateUtils;
import com.wl4g.devops.srm.handler.LogHandler;
import com.wl4g.devops.srm.service.LogConsoleService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class LogConsoleServiceImpl implements LogConsoleService {

	@Resource
	private LogHandler logHandler;

	/*@Override
	public Object consoleLog(QueryLogModel requestBean) throws Exception {
		String index = requestBean.getIndex();
		String date;
		Integer level = requestBean.getLevel();
		//Integer interval = requestBean.getInterval();
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
		//TODO just for test
		index = "filebeat-6.6.2-2019.09.10";
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
		List<String> list = new ArrayList<>();
		List<Log> logList = logHandler.findAll(searchRequest);
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
	}*/

	@Override
	public List<Log> console(QueryLogModel model) throws Exception {
		Assert.notNull(model,"params is error");
		Assert.hasText(model.getIndex(),"index is null");
		if(model.getLimit()==null||model.getLimit()==0){
			model.setLimit(10);
		}


		return console(model.getIndex(),model.getStartTime(),model.getEndTIme(),model.getFrom(),model.getLimit(),model.getQueryList(),model.getLevel());
	}


	public List<Log> console(String index,Long startTime,Long endTime,Integer from,Integer limit,List<Querycriteria> queryList,Integer level) throws Exception {

		//create bool query
		BoolQueryBuilder boolQueryBuilder = boolQuery();

		//fix key match
		if(!CollectionUtils.isEmpty(queryList)){
			queryList.forEach(u -> {
				String con = u.getValue().trim();
				if(StringUtils.isEmpty(con)){
					return;
				}
				if (u.isEnable()) {// enable? must match
					boolQueryBuilder.must(matchQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, con));
				} else {//not enbale ? must not match
					boolQueryBuilder.mustNot(matchQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, con));
				}
			});
		}


		//fix log level match
		if(!Objects.isNull(level)&&level>0){
			BoolQueryBuilder boolQueryBuilder1 = boolQuery();
			for (int i = level - 1; i < SRMDevOpsConstants.LOG_LEVEL.size(); i++) {
				boolQueryBuilder1.should(matchQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, SRMDevOpsConstants.LOG_LEVEL.get(i)));
			}
			boolQueryBuilder.must(boolQueryBuilder1);
		}

		//fix time range
		if(null!=startTime&&null!=endTime){
			RangeQueryBuilder rqb = rangeQuery("@timestamp").timeZone(DateTimeZone.UTC.toString());
			if(null!=startTime){
				rqb.gte(DateUtils.timeStampToUTC(startTime));
			}
			if(null!=endTime){
				rqb.lt(DateUtils.timeStampToUTC(endTime));
			}
			boolQueryBuilder.filter(rqb);
		}

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(boolQueryBuilder);
		sourceBuilder.from(Objects.isNull(from)?0:from);//from
		sourceBuilder.size(limit);//limit
		sourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));//order by timestamp desc

		SearchRequest searchRequest = new SearchRequest(index);
		//searchRequest.types("doc");//useful
		searchRequest.source(sourceBuilder);
		List<Log> logList = logHandler.findAll(searchRequest);
		return logList;
	}




}