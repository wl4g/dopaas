/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.erm.service.impl;

import com.wl4g.components.common.lang.DateUtils2;
import com.wl4g.devops.common.bean.erm.Log;
import com.wl4g.devops.common.bean.erm.QueryLogModel;
import com.wl4g.devops.common.bean.erm.Querycriteria;
import com.wl4g.devops.erm.es.handler.LogHandler;
import com.wl4g.devops.erm.service.LogConsoleService;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wl4g.components.common.lang.Assert2.hasText;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.core.constants.ERMDevOpsConstants.KEY_DEFAULT_MSG;
import static com.wl4g.components.core.constants.ERMDevOpsConstants.LOG_LEVEL;
import static java.util.Objects.isNull;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class LogConsoleServiceImpl implements LogConsoleService {

	@Resource
	private LogHandler logHandler;

	@Override
	public List<Log> console(QueryLogModel model) throws Exception {
		notNull(model, "params");
		hasText(model.getIndex(), "index");

		// Sets defaults
		if (isNull(model.getLimit()) || model.getLimit() == 0) {
			model.setLimit(100);
		}

		// build index
		String index = model.getIndex();
		Long startTime = model.getStartTime();
		Date startTimeD = null;
		if (isNull(startTime) || startTime == 0) {
			startTimeD = new Date();
		} else {
			startTimeD = new Date(startTime);
		}
		index = index + "-" + DateUtils2.formatDate(startTimeD);

		List<Log> logs = queryLogFromESDocuments(index, model.getStartTime(), model.getEndTime(), model.getFrom(),
				model.getLimit(), model.getQueryList(), model.getLevel());
		/*
		 * List<String> result = new ArrayList<>(); for (Log log : logs) {
		 * result.add(log.getMessage()); }
		 */

		return logs;
	}

	/**
	 * Query log from ES documents
	 * 
	 * @param index
	 * @param startTime
	 * @param endTime
	 * @param from
	 * @param limit
	 * @param queryList
	 * @param level
	 * @return
	 * @throws Exception
	 */
	protected List<Log> queryLogFromESDocuments(String index, Long startTime, Long endTime, Integer from, Integer limit,
			List<Querycriteria> queryList, Integer level) throws Exception {
		// create bool query
		BoolQueryBuilder boolQueryBuilder = boolQuery();

		// fix key match
		if (!CollectionUtils.isEmpty(queryList)) {
			queryList.forEach(u -> {
				String query = u.getValue().trim();
				if (StringUtils.isEmpty(query)) {
					return;
				}
				// con = QueryParser.escape(con);
				query = "\"" + query + "\"";// for Special characters
				if (u.isEnable()) {// enable? must match
					// boolQueryBuilder.must(fuzzyQuery(KEY_DEFAULT_MSG,
					// con));
					boolQueryBuilder.must(queryStringQuery(query).field(KEY_DEFAULT_MSG));
				} else {// not enbale ? must not match
					// boolQueryBuilder.mustNot(fuzzyQuery(KEY_DEFAULT_MSG,
					// con));
					boolQueryBuilder.mustNot(queryStringQuery(query).field(KEY_DEFAULT_MSG));
				}
			});
		}

		// fix log level match
		if (!Objects.isNull(level) && level > 0) {
			BoolQueryBuilder boolQueryBuilder1 = boolQuery();
			for (int i = level - 1; i < LOG_LEVEL.size(); i++) {
				// boolQueryBuilder1.should(matchQuery(KEY_DEFAULT_MSG,
				// LOG_LEVEL.get(i)));
				boolQueryBuilder1.must(queryStringQuery(LOG_LEVEL.get(i)).field(KEY_DEFAULT_MSG));
			}
			boolQueryBuilder.must(boolQueryBuilder1);
		}

		// fix time range
		if (null != startTime && null != endTime && (endTime != 0 || startTime != 0)) {
			RangeQueryBuilder rqb = rangeQuery("@timestamp").timeZone(DateTimeZone.UTC.toString());
			if (null != startTime) {
				rqb.gte(DateUtils2.timeToUTC(startTime));
			}
			if (null != endTime) {
				rqb.lt(DateUtils2.timeToUTC(endTime));
			}
			boolQueryBuilder.filter(rqb);
		}

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(boolQueryBuilder);
		sourceBuilder.from(Objects.isNull(from) ? 0 : from);// from
		sourceBuilder.size(Objects.isNull(limit) ? 100 : limit);// limit
		// order by timestamp desc
		sourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));

		SearchRequest searchRequest = new SearchRequest(index);
		// searchRequest.types("doc"); // useful
		searchRequest.source(sourceBuilder);
		List<Log> logList = logHandler.findAll(searchRequest);
		return logList;
	}

}