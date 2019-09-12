package com.wl4g.devops.srm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2019-09-12 11:01:00
 */
@RestController
public class LogPrintfExampleController implements ApplicationRunner  {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> f = args.getOptionValues("f");
        System.out.println("fq="+f);
        long fq = Integer.valueOf(f.get(0));
        int i = 0;
        while (true) {
            logger.warn("Processing. addr=11111119, order={}", i);
            logger.debug("Processing. addr=11111119, order={}", i);
            logger.info("Processing. addr=11111119, order={}", i);
            logger.error("Processing. addr=11111119, order={}", i);
            try {
                Thread.sleep(fq);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
