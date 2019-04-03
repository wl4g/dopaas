package com.wl4g.devops.srm.service;

import com.wl4g.devops.common.bean.srm.RequestBean;

public interface LogConsoleService {
	Object consoleLog(RequestBean requestBean) throws Exception;
}
