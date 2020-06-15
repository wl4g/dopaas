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
package com.wl4g.devops.dguid.worker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * zk编号分配器{可设置interval-心跳间隔、pidHome-workerId文件存储目录、zkAddress-zk地址、pidPort-使用端口(默认不开,同机多uid应用时区分端口)}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class ZookeeperWorkerIdAssigner extends AbstractIntervalWorkerId {
	public static final String ZK_SPLIT = "/";

	/**
	 * ZK上uid根目录
	 */
	public static final String UID_ROOT = "/dguid-root";

	/**
	 * 持久顺序节点根目录(用于保存节点的顺序编号)
	 */
	public static final String UID_FOREVER = UID_ROOT.concat("/forever");

	/**
	 * 临时节点根目录(用于保存活跃节点及活跃心跳)
	 */
	public static final String UID_TEMPORARY = UID_ROOT.concat("/temporary");

	/**
	 * 本地workid文件跟目录
	 */
	public static final String PID_ROOT = "/data/pids/";

	/**
	 * session失效时间
	 */
	public static final int SESSION_TIMEOUT = 3000;

	/**
	 * connection连接时间
	 */
	public static final int CONNECTION_TIMEOUT = 30000;

	/**
	 * zk客户端
	 */
	private ZooKeeper zkClient;

	/**
	 * zk注册地址
	 */
	private String zkAddress = "127.0.0.1:2181";

	/**
	 * 临时节点名，用于上报时间使用
	 */
	private String temporaryNode;

	@Override
	public long action() {
		try {
			zkClient = new ZooKeeper(zkAddress, SESSION_TIMEOUT, new Watcher() {
				@Override
				public void process(WatchedEvent event) {

				}
			});
			if (null == zkClient.exists(UID_ROOT, false)) {
				zkClient.create(UID_ROOT, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			String workNode = UID_FOREVER + ZK_SPLIT + pidName;

			/**
			 * 2、文件不存在，检查zk上是否存在ip:port的节点
			 */
			if (null == zkClient.exists(UID_FOREVER, false)) {
				zkClient.create(UID_FOREVER, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			if (null == workerId || null != zkClient.exists(workNode, false)) {
				// a、 检查zk上是否存在ip:port的节点,存在，获取节点的顺序编号
				List<String> uidWork = zkClient.getChildren(UID_FOREVER, false);
				for (String nodePath : uidWork) {
					if (nodePath.startsWith(pidName)) {
						workerId = Long.valueOf(nodePath.substring(nodePath.length() - 10));
						break;
					}
				}
				// b、 不存在，创建ip:port节点
				if (null == workerId) {
					String nodePath = zkClient.create(workNode, new byte[0], Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT_SEQUENTIAL);
					workerId = Long.valueOf(nodePath.substring(nodePath.length() - 10));
				}
			}

			/**
			 * 3、创建临时节点
			 */
			if (null == zkClient.exists(UID_TEMPORARY, false)) {
				zkClient.create(UID_TEMPORARY, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}

			temporaryNode = zkClient.create(UID_TEMPORARY + ZK_SPLIT + workerId, longToBytes(System.currentTimeMillis()),
					Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			active.set(true);

			/**
			 * 4、获取本地时间，跟zk临时节点列表的时间平均值做比较(zk临时节点用于存储活跃节点的上报时间，每隔一段时间上报一次临时节点时间)
			 */
			List<String> activeNodes = zkClient.getChildren(UID_TEMPORARY, false);
			Long sumTime = 0L;
			for (String itemNode : activeNodes) {
				Long nodeTime = bytesToLong(zkClient.getData(UID_TEMPORARY + ZK_SPLIT + itemNode, false, new Stat()));
				sumTime += nodeTime;
			}
			return sumTime / activeNodes.size();
		} catch (KeeperException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis();
	}

	@Override
	public boolean where() {
		return null != workerId && null != zkClient;
	}

	@Override
	public void report() {
		try {
			zkClient.setData(temporaryNode, longToBytes(System.currentTimeMillis()), -1);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getZkAddress() {
		return zkAddress;
	}

	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

	public static byte[] longToBytes(long x) {
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getLong();
	}
}