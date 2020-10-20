<!-- ${watermark} -->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>${organName}-${projectName}-starter</artifactId>
	<packaging>jar</packaging>
	<name>${organName?cap_first} ${projectName?cap_first} Starter</name>
	<url>http://${projectName?lower_case}.${organName?lower_case}.${organType}</url>
	<parent>
		<groupId>${organType}.${organName}</groupId>
		<artifactId>${organName}-${projectName}</artifactId>
		<version>${version}</version>
	</parent>
	<properties>
		<java.version>1.8</java.version>
		<final.name>${projectName?uncap_first}-server</final.name>
	</properties>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.wl4g</groupId>
				<artifactId>xcloud-bom</artifactId>
				<version>master</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
<#if javaSpecs.isConf(extOpts, "swagger.ui", "bootstrapSwagger2")>
            <dependency>
                <groupId>io.springfox</groupId>
                <artifactId>springfox-swagger2</artifactId>
                <version>2.9.2</version>
            </dependency>
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>swagger-bootstrap-ui</artifactId>
                <version>1.9.6</version>
            </dependency>
</#if>
		</dependencies>
	</dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>${organType?uncap_first}.${organName?uncap_first}</groupId>
			<artifactId>${organName?uncap_first}-${projectName?uncap_first}-service</artifactId>
			<version>${version?uncap_first}</version>
		</dependency>
		<#if javaSpecs.isConf(extOpts, "iam.mode", "cluster")>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-iam-test</artifactId>
			<scope>test</scope>
		</dependency>
		</#if>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<build>
		<finalName>${projectName?uncap_first}-server</finalName>
		<resources>
			<resource>
				<directory>src/main/resources</directory>
				<filtering>true</filtering>
				<excludes>
					<exclude>script/**</exclude>
				</excludes>
			</resource>
			<resource>
				<directory>src/main/resources</directory>
				<includes>
					<include>**/*.*</include>
				</includes>
				<filtering>false</filtering>
			</resource>
		</resources>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					${r'<source>${java.version}</source>'}
					${r'<target>${java.version}</target>'}
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-source-plugin</artifactId>
				<executions>
					<execution>
						<id>attach-sources</id>
						<phase>verify</phase>
						<goals>
							<goal>jar-no-fork</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		<#-- Build artifact package type.(MvnAssTar/SpringExecJar) -->
		<#if javaSpecs.isConf(extOpts, "build.asset-type", "MvnAssTar")>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-assembly-plugin</artifactId>
				<configuration>
					${r'<finalName>${final.name}-${project.version}-bin</finalName>'}
					<!-- not append assembly id in release file name -->
					<appendAssemblyId>false</appendAssemblyId>
					<descriptors>
						<descriptor>src/main/assemble/package.xml</descriptor>
					</descriptors>
				</configuration>
				<executions>
					<execution>
						<id>make-assembly</id>
						<phase>package</phase>
						<goals>
							<goal>single</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		<#elseif javaSpecs.isConf(extOpts, "build.asset-type", "SpringExecJar")>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<mainClass>${organType}.${organName}.${projectName?cap_first}Server</mainClass>
				</configuration>
			</plugin>
		</#if>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-checkstyle-plugin</artifactId>
				<configuration>
					<configLocation>${basedir}/../checkstyle/google_checks.xml</configLocation>
				</configuration>
				<executions>
					<!-- Solutions to maven checkstyle errors in Eclipse: https://gitee.com/wl4g/xcloud-blogs/blob/master/articles/maven.checkstyle-eclipse-error/README_CN.md -->
					<!-- <execution> -->
					<!-- <id>checkstyle</id> -->
					<!-- <phase>validate</phase> -->
					<!-- <goals> -->
					<!-- <goal>check</goal> -->
					<!-- </goals> -->
					<!-- <configuration> -->
					<!-- <failOnViolation>true</failOnViolation> -->
					<!-- </configuration> -->
					<!-- </execution> -->
				</executions>
			</plugin>
		</plugins>
	</build>
</project>