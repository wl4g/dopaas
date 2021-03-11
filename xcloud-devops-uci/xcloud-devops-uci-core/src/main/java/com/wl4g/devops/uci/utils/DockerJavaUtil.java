/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.uci.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import org.springframework.util.CollectionUtils;

import static java.util.Collections.singletonList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;
import static com.wl4g.devops.uci.utils.DockerFileBuilder.makeDockerFile;

/**
 * @author vjay
 * @date 2020-04-23 11:14:00
 */
public class DockerJavaUtil {

	public static DockerClient sampleConnect(String serverUrl) {
		return DockerClientBuilder.getInstance(serverUrl).build();
	}

	/**
	 * 更高级的连接方式，后续使用证书需要用到
	 *
	 * @return
	 */
	public static DockerClient advanceConnect() {
		// TODO
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://localhost:2376")
				// TODO 后续支持证书
				.withDockerTlsVerify(true).withDockerCertPath("/home/user/.docker/certs")// 证书？
				.withDockerConfig("/Users/vjay/.docker").withApiVersion("1.30") // optional
				.withRegistryUrl("https://index.docker.io/v1/")// 填私库地址
				.withRegistryUsername("username")// 填私库用户名
				.withRegistryPassword("123456")// 填私库密码
				.withRegistryEmail("username@github.com")// 填私库注册邮箱
				.build();
		return DockerClientBuilder.getInstance(config).build();
	}

	/**
	 * @param client
	 * @param tarPath
	 * @param dockerTemplate
	 * @param appBinName
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public static String buildImage(DockerClient client, Set<String> tags, File workSpace, Map<String, String> args)
			throws IOException, InterruptedException {
		// copyFile2WorkSpace(workSpace, dockerTemplate);
		for (String tag : tags) {
			removeImage(client, tag);
		}

		makeDockerFile(new File(workSpace.getCanonicalPath() + "/Dockerfile"));

		BuildImageResultCallback callback = new BuildImageResultCallback() {
			@Override
			public void onNext(BuildResponseItem item) {
				System.out.println("" + item);
				super.onNext(item);
			}
		};
		BuildImageCmd buildImageCmd = client.buildImageCmd(workSpace).withTags(tags);
		for (Map.Entry<String, String> entry : args.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			buildImageCmd.withBuildArg(key, value);
		}

		return buildImageCmd.exec(callback).awaitImageId();
	}

	public static void pushImage(DockerClient client, String pushTag, String registryAddress, String username, String password)
			throws InterruptedException {
		AuthConfig authConfig = new AuthConfig().withRegistryAddress(registryAddress).withUsername(username)
				.withPassword(password);

		PushImageResultCallback pushImageResultCallback = new PushImageResultCallback();
		client.pushImageCmd(pushTag).withAuthConfig(authConfig).exec(pushImageResultCallback).awaitCompletion(600,
				TimeUnit.SECONDS);
		;

	}

	/**
	 * Docker remove Image
	 * 
	 * @param client
	 * @param imageName
	 */
	public static void removeImage(DockerClient client, String imageName) {
		List<String> filterName = new ArrayList<>();
		filterName.add(imageName);
		List<Container> containers = client.listContainersCmd().withNameFilter(filterName).exec();
		for (Container container : containers) {
			client.removeContainerCmd(container.getId()).exec();
		}

		List<Image> images = client.listImagesCmd().withImageNameFilter(imageName).exec();
		for (Image image : images) {
			client.removeImageCmd(image.getId()).exec();
		}
	}

	/**
	 * pullImage
	 * 
	 * @param client
	 * @param repository
	 */
	public static void pullImage(DockerClient client, String repository) {// create
																			// service
																			// will
																			// auto
																			// pull
																			// image,
																			// so
																			// this
																			// metho
																			// may
																			// be
																			// unnecessary
		client.pullImageCmd(repository);
	}

	public static void createService(DockerClient client, String imageName, String name) {
		ServiceModeConfig serviceModeConfig = new ServiceModeConfig();
		ServiceReplicatedModeOptions serviceReplicatedModeOptions = new ServiceReplicatedModeOptions();
		serviceReplicatedModeOptions.withReplicas(1);
		serviceModeConfig.withReplicated(serviceReplicatedModeOptions);

		client.createServiceCmd(new ServiceSpec().withMode(serviceModeConfig).withName(name)
				.withTaskTemplate(new TaskSpec().withContainerSpec(new ContainerSpec().withImage(imageName)))).exec();
	}

	public static void removeService(DockerClient client, String serviceId) {
		List<Service> exec = client.listServicesCmd().withNameFilter(singletonList(serviceId)).exec();
		// if not found but del, it will throw exception
		if (!isEmpty(exec)) {
			client.removeServiceCmd(serviceId).exec();
		}
	}

	/**
	 * 创建容器
	 *
	 * @param client
	 * @return
	 */
	public static CreateContainerResponse createContainers(DockerClient client, String containerName, String imageName,
			Map<Integer, Integer> ports) {// TODO 优化
		CreateContainerCmd createContainerCmd = client.createContainerCmd(imageName).withName(containerName);
		// TODO 处理端口映射
		if (!CollectionUtils.isEmpty(ports)) {
			List<ExposedPort> exposedPorts = new ArrayList<>();
			Ports portBindings = new Ports();
			for (Map.Entry<Integer, Integer> entry : ports.entrySet()) {
				Integer key = entry.getKey();
				Integer value = entry.getValue();
				ExposedPort exposedPort = ExposedPort.tcp(key);
				exposedPorts.add(exposedPort);
				portBindings.bind(exposedPort, Ports.Binding.bindPort(value));
			}
			HostConfig hostConfig = newHostConfig().withPortBindings(portBindings);
			createContainerCmd.withHostConfig(hostConfig).withExposedPorts(exposedPorts);
		}
		return createContainerCmd.exec();
	}

	/**
	 * Start container.
	 *
	 * @param client
	 * @param containerId
	 */
	public static void startContainer(DockerClient client, String containerId) {
		client.startContainerCmd(containerId).exec();
	}

	/**
	 * Stop container.
	 *
	 * @param client
	 * @param containerId
	 */
	public static void stopContainer(DockerClient client, String containerId) {
		client.stopContainerCmd(containerId).exec();
	}

	/**
	 * Destroy container
	 *
	 * @param client
	 * @param containerId
	 */
	public static void removeContainer(DockerClient client, String containerId) {
		client.removeContainerCmd(containerId).exec();
	}

}