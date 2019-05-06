package com.wl4g.devops.shell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wl4g.devops.shell.bean.AdditionArgument;
import com.wl4g.devops.shell.bean.AdditionResult;

@Service
public class ExampleServiceImpl implements ExampleService {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public AdditionResult add(AdditionArgument add) {
		int sum = add.getAdd1() + add.getAdd2();
		log.info("计算结果>>>... {}", sum);
		return new AdditionResult(sum);
	}

}
