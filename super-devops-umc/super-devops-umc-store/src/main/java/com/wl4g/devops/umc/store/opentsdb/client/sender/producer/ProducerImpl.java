package com.wl4g.devops.umc.store.opentsdb.client.sender.producer;

import com.wl4g.devops.umc.store.opentsdb.client.bean.request.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午4:20
 * @Version: 1.0
 */

public class ProducerImpl implements Producer {

    final private Logger log = LoggerFactory.getLogger(getClass());

    private final BlockingQueue<Point> queue;

    private final AtomicBoolean forbiddenWrite = new AtomicBoolean(false);

    public ProducerImpl(BlockingQueue<Point> queue) {
        this.queue = queue;
        log.debug("the producer has started");
    }

    @Override
    public void send(Point point) {
        if (forbiddenWrite.get()) {
            throw new IllegalStateException("client has been closed.");
        }
        try {
            // 队列满时，put方法会被阻塞
            queue.put(point);
        } catch (InterruptedException e) {
            log.error("Client Thread been Interrupted.", e);
            e.printStackTrace();
        }
    }

    @Override
    public void forbiddenSend() {
        forbiddenWrite.compareAndSet(false, true);
    }

}
