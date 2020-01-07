package com.wl4g.devops.ci.pcm;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.ci.config.CiCdAutoConfiguration;

/**
 * Abstract PCM operator.
 * 
 * @param <K>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
 * @see
 */
public abstract class AbstractPcmOperator implements PcmOperator, InitializingBean {
	final protected Logger log = getLogger(getClass());

	/**
	 * CICD PCM configuration.
	 */
	@Autowired
	protected CiCdAutoConfiguration config;

	/**
	 * {@link RestTemplate} of PCM API provider.
	 */
	protected RestTemplate restTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		this.restTemplate = new RestTemplate(factory);
	}

}
