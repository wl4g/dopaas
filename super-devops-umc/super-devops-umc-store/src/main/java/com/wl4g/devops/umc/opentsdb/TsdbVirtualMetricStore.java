package com.wl4g.devops.umc.opentsdb;

import com.wl4g.devops.common.bean.umc.model.Base;
import com.wl4g.devops.common.bean.umc.model.virtual.Docker;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.VirtualMetricStore;
import org.springframework.util.Assert;

import java.text.NumberFormat;
import java.text.ParseException;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * Virtual(docker) openTSDB store
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class TsdbVirtualMetricStore implements VirtualMetricStore {

	final protected OpenTSDBClient client;

	public TsdbVirtualMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(Base baseTemple) {
		return false;
	}

	@Override
	public boolean save(Docker docker){
		long timestamp = System.currentTimeMillis() / 1000;
		Assert.notNull(docker, "docker is null");
		Assert.notEmpty(docker.getDockerInfo(), "dockerInfo is empty");

		NumberFormat nf= NumberFormat.getPercentInstance();//NumberFormat是一个工厂，可以直接getXXX创建，而getPercentInstance()是返回当前默认语言环境的百分比格式。
		for(Docker.DockerInfo dockerInfo : docker.getDockerInfo()){
			try {
				Point cpuPerc = Point.metric(METRIC_DOCKER_CPU).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, nf.parse(dockerInfo.getCpuPerc())).build();

				Point memUsage = Point.metric(METRIC_DOCKER_MEM_USAGE).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, toB(split(dockerInfo.getMemUsage())[0])).build();

				Point memPerc = Point.metric(METRIC_DOCKER_MEM_PERC).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, nf.parse(dockerInfo.getMemPerc())).build();


				String net[] = split(dockerInfo.getNetIO());
				Point netIn = Point.metric(METRIC_DOCKER_NET_IN).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, toB(net[0])).build();
				Point netOut = Point.metric(METRIC_DOCKER_NET_OUT).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, toB(net[1])).build();

				String block[] = split(dockerInfo.getBlockIO());
				Point blockIn = Point.metric(METRIC_DOCKER_BLOCK_IN).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, toB(block[0])).build();
				Point blockOut = Point.metric(METRIC_DOCKER_BLOCK_OUT).tag(TAG_ID, docker.getPhysicalId()).tag(TAG_DOCKER_CONTAINER_ID,dockerInfo.getContainerId()).tag(TAG_DOCKER_NAME,dockerInfo.getName())
						.value(timestamp, toB(block[1])).build();

				client.put(cpuPerc);
				client.put(memUsage);
				client.put(memPerc);
				client.put(netIn);
				client.put(netOut);
				client.put(blockIn);
				client.put(blockOut);

			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		return false;
	}




	private String[] split(String str){
		Assert.hasText(str, "str is null");
		String[] strs= str.split("/");
		if(strs.length!=2){
			throw new RuntimeException("Format mismatch");
		}
		strs[0] = strs[0].trim();
		strs[1] = strs[1].trim();
		return strs;
	}


	private long toB(String str){
		Assert.hasText(str, "str is null");
		long result = 0;
		if(str.contains("TB")||str.contains("TiB")){
			str = str.replaceAll("TB","");
			str = str.replaceAll("TiB","");
			result = (long)(Double.parseDouble(str)*1024*1024*1024*1024);
		}else if(str.contains("GB")||str.contains("GiB")){
			str = str.replaceAll("GB","");
			str = str.replaceAll("GiB","");
			result = (long)(Double.parseDouble(str)*1024*1024*1024);
		}else if(str.contains("MB")||str.contains("MiB")){
			str = str.replaceAll("MB","");
			str = str.replaceAll("MiB","");
			result = (long)(Double.parseDouble(str)*1024*1024);
		}else if(str.contains("KB")||str.contains("KiB")||str.contains("kB")){
			str = str.replaceAll("kB","");
			str = str.replaceAll("KB","");
			str = str.replaceAll("KiB","");
			result = (long)(Double.parseDouble(str)*1024);
		}else {
			str = str.replaceAll("B","");
			str = str.replaceAll("B","");
			result = (Long.parseLong(str));
		}
		return result;
	}

}
