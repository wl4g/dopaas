/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
import com.wl4g.devops.common.bean.srm.QueryLogModel;
import com.wl4g.devops.common.bean.srm.Querycriteria;
import com.wl4g.devops.common.constants.SRMDevOpsConstants;
import com.wl4g.devops.srm.handler.LogHandler;
import com.wl4g.devops.srm.service.LogConsoleService;
import com.wl4g.devops.tool.common.lang.DateUtils2;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class LogConsoleServiceImpl implements LogConsoleService {

    @Resource
    private LogHandler logHandler;

    @Override
    public List<String> console(QueryLogModel model) throws Exception {
        Assert.notNull(model, "params is error");
        Assert.hasText(model.getIndex(), "index is null");

        //set default
        if (model.getLimit() == null || model.getLimit() == 0) {
            model.setLimit(100);
        }

        //build index
        String index = model.getIndex();
        Long startTime = model.getStartTime();
        Date startTimeD = null;
        if (null == startTime || startTime == 0) {
            startTimeD = new Date();
        } else {
            startTimeD = new Date(startTime);
        }
        index = index + "-" + DateUtils2.formatDate(startTimeD);

        List<Log> console = console(index, model.getStartTime(), model.getEndTime(), model.getFrom(), model.getLimit(), model.getQueryList(), model.getLevel());
        List<String> result = new ArrayList();
        for (Log log : console) {
            result.add(log.getMessage());
        }
        return result;
    }

    public List<Log> console(String index, Long startTime, Long endTime, Integer from, Integer limit, List<Querycriteria> queryList, Integer level) throws Exception {

        //create bool query
        BoolQueryBuilder boolQueryBuilder = boolQuery();

        //fix key match
        if (!CollectionUtils.isEmpty(queryList)) {
            queryList.forEach(u -> {
                String query = u.getValue().trim();
                if (StringUtils.isEmpty(query)) {
                    return;
                }
                //con = QueryParser.escape(con);
                query = "\"" + query + "\"";// for Special characters
                if (u.isEnable()) {// enable? must match
                    //boolQueryBuilder.must(fuzzyQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, con));
                    boolQueryBuilder.must(queryStringQuery(query).field(SRMDevOpsConstants.KEY_DEFAULT_MSG));
                } else {//not enbale ? must not match
                    //boolQueryBuilder.mustNot(fuzzyQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, con));
                    boolQueryBuilder.mustNot(queryStringQuery(query).field(SRMDevOpsConstants.KEY_DEFAULT_MSG));
                }
            });
        }

        //fix log level match
        if (!Objects.isNull(level) && level > 0) {
            BoolQueryBuilder boolQueryBuilder1 = boolQuery();
            for (int i = level - 1; i < SRMDevOpsConstants.LOG_LEVEL.size(); i++) {
                boolQueryBuilder1.should(matchQuery(SRMDevOpsConstants.KEY_DEFAULT_MSG, SRMDevOpsConstants.LOG_LEVEL.get(i)));
            }
            boolQueryBuilder.must(boolQueryBuilder1);
        }

        //fix time range
        if (null != startTime && null != endTime && (endTime != 0 || startTime != 0)) {
            RangeQueryBuilder rqb = rangeQuery("@timestamp").timeZone(DateTimeZone.UTC.toString());
            if (null != startTime) {
                rqb.gte(DateUtils2.timeStampToUTC(startTime));
            }
            if (null != endTime) {
                rqb.lt(DateUtils2.timeStampToUTC(endTime));
            }
            boolQueryBuilder.filter(rqb);
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(Objects.isNull(from) ? 0 : from);//from
        sourceBuilder.size(Objects.isNull(limit) ? 100 : limit);//limit
        sourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));//order by timestamp desc

        SearchRequest searchRequest = new SearchRequest(index);
        //searchRequest.types("doc");//useful
        searchRequest.source(sourceBuilder);
        List<Log> logList = logHandler.findAll(searchRequest);
        return logList;
    }


}