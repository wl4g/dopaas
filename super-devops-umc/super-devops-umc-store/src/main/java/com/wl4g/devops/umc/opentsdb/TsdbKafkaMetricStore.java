package com.wl4g.devops.umc.opentsdb;

import com.wl4g.devops.common.bean.umc.model.third.Kafka;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.store.KafkaMetricStore;

/**
 * @author vjay
 * @date 2019-06-20 16:12:00
 */
public class TsdbKafkaMetricStore implements KafkaMetricStore {

    final protected OpenTSDBClient client;

    public TsdbKafkaMetricStore(OpenTSDBClient client) {
        this.client = client;
    }

    @Override
    public boolean save(Kafka kafka) {

        return true;
    }
}
