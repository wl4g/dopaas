/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.dguid.worker.registory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.wl4g.devops.dguid.worker.entity.WorkerNode;

/**
 * {@link WorkerNodeRegistory}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class WorkerNodeRegistory {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Get {@link WorkerNode} by node host
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	public WorkerNode getWorkerNodeByHostPort(String host, String port) {
		final WorkerNode workerNode = new WorkerNode();
		String querySql = "select * from WORKER_NODE where HOST_NAME = ? AND PORT = ? ";
		jdbcTemplate.query(querySql, new String[] { host, port }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				workerNode.setId(rs.getLong("ID"));
				workerNode.setHostName(rs.getString("HOST_NAME"));
				workerNode.setPort(rs.getString("PORT"));
				workerNode.setType(rs.getInt("TYPE"));
				workerNode.setLaunchDateDate(rs.getDate("LAUNCH_DATE"));
				workerNode.setModified(rs.getDate("MODIFIED"));
				workerNode.setCreated(rs.getDate("CREATED"));
			}
		});
		return workerNode;
	}

	/**
	 * Add {@link WorkerNode}
	 * 
	 * @param workerNode
	 */
	public void addWorkerNode(WorkerNode workerNode) {
		String sql = "INSERT INTO WORKER_NODE(HOST_NAME, PORT, TYPE, LAUNCH_DATE, MODIFIED, CREATED) "
				+ " VALUES (?, ?, ?, ?, NOW(), NOW())";
		jdbcTemplate.update(sql, new Object[] { workerNode.getHostName(), workerNode.getPort(), workerNode.getType(),
				workerNode.getLaunchDate() });
	}

}