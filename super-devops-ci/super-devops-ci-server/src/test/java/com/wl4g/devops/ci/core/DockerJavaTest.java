package com.wl4g.devops.ci.core;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.SearchItem;
import com.wl4g.devops.ci.utils.DockerJavaUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author vjay
 * @date 2020-04-23 10:59:00
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DockerJavaTest {

    //connect
    DockerClient dockerClient;

    @Before
    public void before() {
        //init
        dockerClient = DockerJavaUtil.sampleConnect("tcp://localhost:2376");//"tcp://10.0.0.161:2375"
    }

    @Test
    public void test_1_info() {
        Info info = dockerClient.infoCmd().exec();
        //TODO 检查服务端版本
        System.out.print(info.getServerVersion());
    }

    @Test
    public void test_2_serachImage() {
        // 搜索镜像
        List<SearchItem> dockerSearch = dockerClient.searchImagesCmd("busybox").exec();
        System.out.println("Search returned" + dockerSearch.toString());
    }


    @Test
    public void test_3_buildImage() throws IOException {

        Map<String, String> args = new HashMap<>();
        args.put("APP_BIN_NAME", "iam-server-master-bin");
        args.put("APP_PORT", "14040");
        args.put("MAIN_CLASS", "com.wl4g.devops.IamServer");
        args.put("ACTIVE", "dev");

        Set<String> tags = new HashSet<>();//如果多个会创建多个image，tag名字不相同，但IMAGE ID一样的image
        tags.add("mytag:v0.1");//冒号前面为名字，冒号后面为版本，版本为空则为latest

        String containerId = DockerJavaUtil.buildImage(dockerClient, tags,
                new File("../../super-devops-iam/super-devops-iam-server/target/iam-server-master-bin.tar"),
                new File("./Dockerfile"),
                "iam-server-master-bin",
                args);

        System.out.println("create container success. containerId = " + containerId);

    }

    @Test
    public void test_4_run() throws InterruptedException {

        //create container
        Map<Integer, Integer> ports = new HashMap<>();
        ports.put(14040, 14040);
        CreateContainerResponse containerResponse = DockerJavaUtil.createContainers(dockerClient, "mytag", "mytag:v0.1", ports);
        String containerId = containerResponse.getId();
        System.out.println(containerId);

        //start container
        DockerJavaUtil.startContainer(dockerClient, containerId);

        Thread.sleep(10_000);

        //stop container
        DockerJavaUtil.stopContainer(dockerClient, containerId);

        //remove container
        DockerJavaUtil.removeContainer(dockerClient, containerId);

    }


    //@Test
    public void stop() {
        DockerJavaUtil.stopContainer(dockerClient, "b80f575b375ca7c824f7866bb9c23a8b81196043bb46ad84664a200495c0e053");
    }

    //@Test
    public void remmoveContainer() {
        DockerJavaUtil.removeContainer(dockerClient, "b80f575b375ca7c824f7866bb9c23a8b81196043bb46ad84664a200495c0e053");
    }


    @After
    public void after() {
        try {
            if (Objects.nonNull(dockerClient)) {
                dockerClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
