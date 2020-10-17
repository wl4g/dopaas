<!-- ${watermark} -->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>${organName}-${projectName}-common</artifactId>
	<packaging>jar</packaging>
	<name>${organName?cap_first} ${projectName?cap_first} Common</name>
	<url>http://${projectName?lower_case}.${organName?lower_case}.${organType}</url>
	<parent>
		<groupId>${organType}.${organName}</groupId>
		<artifactId>${organName}-${projectName}</artifactId>
		<version>${version}</version>
	</parent>
	<properties>
		<java.version>1.8</java.version>
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
<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
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
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-components-core</artifactId>
		</dependency>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-iam-common</artifactId>
		</dependency>
		<!-- https://github.com/springfox/springfox-demos -->
<#if javaSpecs.isConf(extraOptions, "gen.swagger.ui", "bootstrapSwagger2")>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>swagger-bootstrap-ui</artifactId>
        </dependency>
<#elseif javaSpecs.isConf(extraOptions, "gen.swagger.ui", "officialOas")>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
        </dependency>
</#if>
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>easyexcel</artifactId>
		</dependency>
	</dependencies>
	<build>
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