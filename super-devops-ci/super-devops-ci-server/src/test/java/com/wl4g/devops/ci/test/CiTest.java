package com.wl4g.devops.ci.test;

import com.wl4g.devops.CiServer;
import com.wl4g.devops.ci.core.Pipeline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author vjay
 * @date 2019-09-29 10:51:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CiServer.class)
public class CiTest {

	@Autowired
	private Pipeline pipelineProcessor;

	@Test
	public void createTask() {
		pipelineProcessor.createTask(152);
	}

}
