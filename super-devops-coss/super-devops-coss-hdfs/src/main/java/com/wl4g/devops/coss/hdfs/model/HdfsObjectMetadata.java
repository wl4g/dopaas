package com.wl4g.devops.coss.hdfs.model;

import com.wl4g.devops.coss.model.ObjectMetadata;

/**
 * Hdfs storage object metadata wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月2日
 * @since
 */
public class HdfsObjectMetadata extends ObjectMetadata {

	private short blockReplication;
	private long blocksize;

	public short getBlockReplication() {
		return blockReplication;
	}

	public void setBlockReplication(short blockReplication) {
		this.blockReplication = blockReplication;
	}

	public long getBlocksize() {
		return blocksize;
	}

	public void setBlocksize(long blocksize) {
		this.blocksize = blocksize;
	}

}
