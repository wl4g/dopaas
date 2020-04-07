package com.wl4g.devops.umc.timing;

import com.wl4g.devops.common.bean.umc.CustomAlarmEvent;
import com.wl4g.devops.common.bean.umc.CustomDataSource;
import com.wl4g.devops.common.bean.umc.CustomEngine;
import com.wl4g.devops.common.bean.umc.CustomHistory;
import com.wl4g.devops.common.bean.umc.datasouces.BaseDataSource;
import com.wl4g.devops.common.bean.umc.datasouces.MysqlDataSource;
import com.wl4g.devops.dao.umc.CustomAlarmEventDao;
import com.wl4g.devops.dao.umc.CustomDatasourceDao;
import com.wl4g.devops.dao.umc.CustomHistoryDao;
import com.wl4g.devops.tool.common.log.SmartLogger;
import com.wl4g.devops.tool.common.log.SmartLoggerFactory;
import com.wl4g.devops.umc.service.CustomDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Date;

/**
 * @author vjay
 * @date 2020-04-03 16:04:00
 */
public class CodeExecutor {

    SmartLogger log = SmartLoggerFactory.getLogger(getClass());

    @Autowired
    private CustomHistoryDao customHistoryDao;

    @Autowired
    private CustomDatasourceDao customDatasourceDao;

    @Autowired
    private CustomAlarmEventDao customAlarmEventDao;

    @Autowired
    private CustomDataSourceService customDataSourceService;

    @Autowired
    private DemoEngine demoEngine;


    public void executeCode(CustomEngine customEngine) {
        CustomHistory customHistory = beforeStart(customEngine.getId());

        CustomDataSource customDataSource = customDatasourceDao.selectByPrimaryKey(customEngine.getDatasourceId());
        BaseDataSource baseDataSource = customDataSourceService.properties2Model(customDataSource);

        if (baseDataSource instanceof MysqlDataSource) {
            MysqlDataSource mysqlDataSource = (MysqlDataSource) baseDataSource;

            SingleDataSource singleDataSource = new SingleDataSource("com.mysql.jdbc.Driver",mysqlDataSource.getUrl(),
                    mysqlDataSource.getUsername(), mysqlDataSource.getPassword());

            JdbcTemplate jdbcTemplate = new JdbcTemplate(singleDataSource);

            //demoEngine
            demoEngine.executeCode(jdbcTemplate, customEngine);

            try {
                singleDataSource.close();
            } catch (IOException e) {
                log.error("singleDataSource close fail",e);
            }
        }

        afterEnd(customHistory);
    }


    private CustomHistory beforeStart(Integer customEngineId) {
        CustomHistory customHistory = new CustomHistory();
        customHistory.setCustomEngineId(customEngineId);
        customHistory.setState(1);
        customHistory.setStartTime(new Date());
        customHistory.preInsert();
        customHistoryDao.insertSelective(customHistory);


        return customHistory;
    }

    private void afterEnd(CustomHistory customHistory) {
        customHistory.setEndTime(new Date());
        customHistory.setCostTime(customHistory.getEndTime().getTime() - customHistory.getStartTime().getTime());
        customHistory.preUpdate();
        customHistory.setState(5);
        customHistoryDao.updateByPrimaryKeySelective(customHistory);
    }

    public void saveAlarmEvent(CustomEngine customEngine, String message) {
        CustomAlarmEvent customAlarmEvent = new CustomAlarmEvent();
        customAlarmEvent.setCustomEngineId(customEngine.getId());
        customAlarmEvent.setNotifyGroupIds(customEngine.getNotifyGroupIds());
        customAlarmEvent.setMessage(message);
        customAlarmEvent.preInsert();
        customAlarmEventDao.insertSelective(customAlarmEvent);

        //TODO semd message
    }


}
