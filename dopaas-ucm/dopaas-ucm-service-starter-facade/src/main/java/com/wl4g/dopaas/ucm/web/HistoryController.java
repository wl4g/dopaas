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
package com.wl4g.dopaas.ucm.web;

import com.github.pagehelper.Page;

import com.wl4g.dopaas.common.bean.ucm.*;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.infra.core.web.BaseController;
import com.wl4g.dopaas.ucm.service.HistoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 历史版本
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月2日
 * @since
 */
@RestController
@RequestMapping("/history")
public class HistoryController extends BaseController {

    private @Autowired HistoryService historyService;

    /**
     * 查询流水集合
     */
    @RequestMapping(value = "version-list.json", method = { RequestMethod.POST, RequestMethod.GET })
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> versionlist(String startDate, String endDate, PageHolder<?> pm) {
        log.info("VersionList request ... {}, {}, {}", startDate, endDate, pm);

        RespBase<Object> resp = new RespBase<>();
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put("startDate", startDate);
            param.put("endDate", endDate);
            resp.setData(historyService.versionList(pm, param));
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("获取应用组列表失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("VersionList response. {}", resp);
        }
        return resp;
    }

    /**
     * 发布 新增一条历史数据
     * 
     * @return
     */
    @RequestMapping(value = "history-set.json", method = RequestMethod.POST)
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> insert(@RequestBody HistoryOfDetail historyOfDetail) {
        if (log.isInfoEnabled()) {
            log.info("HistorySet request ... {}", historyOfDetail);
        }
        RespBase<?> resp = new RespBase<>();
        try {
            historyService.insert(historyOfDetail);
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("发布版本失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("HistorySet response. {}", resp);
        }
        return resp;
    }

    /**
     * 版本回滚 , 发布 添加流水
     */
    @RequestMapping(value = "reledetail-set.json", method = RequestMethod.POST)
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> rollback(@RequestBody ReleaseDetail detail) {
        if (log.isInfoEnabled()) {
            log.info("ReledetailSet request ... {}", detail);
        }
        RespBase<?> resp = new RespBase<>();
        try {
            historyService.insertDetail(detail);
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("添加流水失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("ReledetailSet response. {}", resp);
        }
        return resp;
    }

    /**
     * 回滚--修改时间
     */
    public RespBase<?> updateHistory(ReleaseDetail detail) {
        if (log.isInfoEnabled()) {
            log.info("ReleaseDetailUpdate request ... {}", detail);
        }
        RespBase<?> resp = new RespBase<>();
        try {
            boolean flag = historyService.updateHistory(detail);
            if (flag) {

            } else {
                resp.setCode(RetCode.SYS_ERR);
            }
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("修改失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("ReleaseDetailUpdate response. {}", resp);
        }
        return resp;
    }

    /**
     * 删除版本
     * 
     * @param history
     * @return
     */
    @RequestMapping(value = "history-delete.json", method = RequestMethod.POST)
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> delete(@RequestBody ReleaseHistory history) {
        if (log.isInfoEnabled()) {
            log.info("HistoryDel request ... {}", history);
        }
        RespBase<?> resp = new RespBase<>();
        try {
            boolean flag = historyService.delete(history);
            if (flag) {
            } else {
                resp.setCode(RetCode.SYS_ERR);
                log.error("请求失败，请确认请求参数！");
            }
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("删除历史版本失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("HistoryDel response. {}", resp);
        }
        return resp;
    }

    /**
     * 删除版本
     * 
     * @param history
     * @return
     */
    @RequestMapping(value = "version-delete.json", method = RequestMethod.POST)
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> deleteVersion(@RequestBody Version history) {
        if (log.isInfoEnabled()) {
            log.info("VersionDel request ... {}", history);
        }
        RespBase<?> resp = new RespBase<>();
        try {
            boolean flag = historyService.versionDelete(history);
            if (flag) {

            } else {
                resp.setCode(RetCode.SYS_ERR);
                log.error("请求失败，请确认请求参数！");
            }
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("删除版本失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("VersionDel response ... {}", history);
        }
        return resp;
    }

    /**
     * 修改版本
     * 
     * @param history
     * @return
     */
    @RequestMapping(value = "version-update.json", method = RequestMethod.POST)
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> updateVersion(@RequestBody Version history) {
        RespBase<?> resp = new RespBase<>();
        if (log.isInfoEnabled()) {
            log.info("VersionUpdate request ... {}", history);
        }
        try {
            boolean flag = historyService.versionUpdate(history);
            if (flag) {
            } else {
                resp.setCode(RetCode.SYS_ERR);
                log.error("请求失败，请确认请求参数！");
            }
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("修改版本失败", e);
        }
        if (log.isInfoEnabled()) {
            log.info("VersionUpdate response ... {}", history);
        }
        return resp;
    }

    /**
     * 查询流水集合
     */
    @RequestMapping(value = "release-list.json", method = { RequestMethod.POST, RequestMethod.GET })
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> list(ConfigVersionList versions, PageHolder<?> pm) {
        if (log.isInfoEnabled()) {
            log.info("ReleaseList request... {}, {}", versions, pm);
        }
        RespBase<Object> resp = new RespBase<>();
        try {
            Page<ConfigVersionList> page = PageHolder.startPage(pm.getPageNum(), pm.getPageSize(), true);
            List<ConfigVersionList> list = historyService.list(versions);

            pm.setTotal(page.getTotal());
            resp.forMap().put("page", pm);
            resp.forMap().put("list", list);
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("获取流水明细列表失败", e);
        }

        log.info("ReleaseList response. {}", resp);
        return resp;
    }

    /**
     * 回滚版本
     */
    @RequestMapping(value = "release_rollback", method = { RequestMethod.POST, RequestMethod.GET })
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> releaseRollback(ConfigVersionList agl) {
        RespBase<Object> resp = new RespBase<>();
        try {
            historyService.releaseRollback(agl);
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            resp.setMessage(e.getMessage());
            log.error("回滚版本失败", e);
        }
        return resp;
    }

    /**
     * 查询历史版本集合
     */
    @RequestMapping(value = "history_list", method = { RequestMethod.POST, RequestMethod.GET })
    @RequiresPermissions(value = { "ucm" })
    public RespBase<?> historylist(ReleaseHistoryList agl, PageHolder<?> pm) {
        log.info("HistoryVersionList request ... {}, {}", agl, pm);
        RespBase<Object> resp = new RespBase<>();
        try {
            Page<ReleaseHistoryList> page = PageHolder.startPage(pm.getPageNum(), pm.getPageSize(), true);
            List<ReleaseHistoryList> list = historyService.historylist(agl);
            pm.setTotal(page.getTotal());

            resp.forMap().put("page", pm);
            resp.forMap().put("list", list);
        } catch (Exception e) {
            resp.setCode(RetCode.SYS_ERR);
            log.error("获取历史版本列表失败", e);
        }
        log.info("HistoryVersionList response. {}", resp);
        return resp;
    }

}