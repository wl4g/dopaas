package com.wl4g.devops.ci.pmplatform.handle;

import org.slf4j.Logger;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;

import com.wl4g.devops.tool.common.log.SmartLoggerFactory;

/**
 * @author vjay
 * @date 2020-01-03 15:26:00
 */
public class BasePlatform {

    final protected Logger log = SmartLoggerFactory.getLogger(getClass());

    Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();

}
