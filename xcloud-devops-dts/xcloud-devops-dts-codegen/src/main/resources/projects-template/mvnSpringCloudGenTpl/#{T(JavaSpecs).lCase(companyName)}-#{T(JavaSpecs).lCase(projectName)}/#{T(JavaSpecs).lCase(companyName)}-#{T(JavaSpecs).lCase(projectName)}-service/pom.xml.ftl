<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>xcloud-devops-ci-core</artifactId>
	<packaging>jar</packaging>
	<name>#{T(JavaSpecs).fistUCase(companyName)}-#{projectName} Service</name>
	<url>http://xcloud.wl4g.com</url>
	<parent>
		<groupId>com.wl4g</groupId>
		<artifactId>xcloud-devops-ci</artifactId>
		<version>master</version>
		<relativePath>../pom.xml</relativePath>
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
		</dependencies>
	</dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-components-support</artifactId>
		</dependency>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-dao</artifactId>
		</dependency>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-shell-cli</artifactId>
		</dependency>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-shell-springboot</artifactId>
		</dependency>
		<dependency>
			<groupId>com.wl4g</groupId>
			<artifactId>xcloud-iam-client</artifactId>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<source>${java.version}</source>
					<target>${java.version}</target>
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
		</plugins>
	</build>
</project>