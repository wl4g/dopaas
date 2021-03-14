## XCloud PaaS Doc Plugin for Swagger

A swagger plug-in based on Maven can automatically generate JSON / XML documents in the compilation phase. This mode is suitable for continuous development and cooperation of medium and large-scale projects. The running package of production environment does not need to rely on swagger/springfox/springdoc library, etc


#### Use Example:
```
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
            <groupId>com.wl4g</groupId>
            <artifactId>xcloud-paas-doc-plugin-swagger</artifactId>
            <version>master</version>
            <executions>
                <execution>
                    <id>gendoc-stage</id>
                    <goals>
                        <!-- <goal>gendoc-springfox-oas3</goal> -->
                        <!-- <goal>gendoc-springfox-jaxrs2</goal> -->
                        <goal>gendoc-springfox-swagger2</goal>
                    </goals>
                    <configuration>
                        <swaggerConfig>
                            <info>
                                <termsOfService>TeamOfWL</termsOfService>
                                <title>${project.name}</title>
                                <version>${project.parent.version}</version>
                                <description>${project.parent.description}</description>
                                <contact>
                                    <name>Wanglsir</name>
                                    <url>http://blog.wl4g.com</url>
                                    <email>wanglsir@gmail.com</email>
                                </contact>
                                <license>
                                    <name>Apache License 2.0</name>
                                    <url>http://www.apache.org/licenses/LICENSE-2.0</url>
                                </license>
                            </info>
                        </swaggerConfig>
                        <!-- default: false -->
                        <skip>false</skip>
                        <!-- default is empty -->
                        <resourcePackages>
                            <resourcePackage>com.wl4g.devops.doc.plugin.sample.article</resourcePackage>
                            <resourcePackage>com.wl4g.devops.doc.plugin.sample.sys</resourcePackage>
                        </resourcePackages>
                        <!-- default: ${project.build.directory}/generated-docs -->
                        <outputDirectory>${project.build.directory}/generated-docs</outputDirectory>
                        <!-- default: swagger -->
                        <outputFilename>swagger-swagger2-by-springfox</outputFilename>
                        <!-- default: JSON -->
                        <outputFormats>JSON,YAML</outputFormats>
                        <!-- default: false -->
                        <prettyPrint>true</prettyPrint>
                        <!-- default: false -->
                        <attachSwaggerArtifact>true</attachSwaggerArtifact>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

> Example codes reference to: [xcloud-paas-doc-plugin-sample/pom.xml](xcloud-paas-doc-plugin-sample/pom.xml)

