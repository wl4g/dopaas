# 跨平台代码生成引擎 (XCloud Cross Platform Code Generate Engine)

> 跨平台代码生成引擎（与开发语言、框架和技术架构无关）


## 开发指南

#### 构建项目

```
# 首先构建前置依赖项目
git clone https://github.com/wl4g/xcloud-parent.git
cd xcloud-parent
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-components.git
cd xcloud-components
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-shell.git
cd xcloud-shell
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-iam.git
cd xcloud-iam
mvn clean install -DskipTests -T 2C

# 构建
git clone https://github.com/wl4g/xcloud-devops.git
cd xcloud-devops
mvn clean install -DskipTests -T 2C
```

#### 项目结构
```
├── main
    ├── java
      └── com
    	└── wl4g
          └── devops
             └── dts
               └── codegen
                    ├── config
                    │   ├── CodegenAutoConfiguration.java // (A)入口配置类
                    ├── engine
                    │   ├── converter
                    │   │   ├── MySQLV5TypeConverter.java // (B)多数据库类型转换器(支持MySQL/Oracle/PostgreSQL)
                    │   ├── generator
                    // 省略无关代码...
                    │   │   ├── SpringCloudMvnGeneratorProvider.java // (C)多种语言、框架组合生成器
                    │   ├── resolver
                    │   │   ├── MySQLV5MetadataResolver.java // (D)多种数据库表元数据解析器(支持MySQL/Oracle/PostgreSQL)
                    // 省略无关代码...
                    │   ├── specs
                    │   │   ├── JavaSpecs.java // (E)多种语言项目生成，需要有特定不同的规范处理工具类(支持Java/Golang/Python/Csharp/Vue)
                    │   └── template
                    │       ├── ClassPathGenTemplateLocator.java // (F)多种方式的模版工程加载器
                    // 省略无关代码...
    └── resources
        ├── generate-templates
            ├── springCloudMvnProvider // (G) 多种不同框架组合生成器对应的模版工程
                └── #{javaSpecs.lCase(organName)}-#{javaSpecs.lCase(projectName)}
                    // 省略无关代码...

```


#### 开发步骤
step1: 新建模版工程，[参考(G) springCloudMvnProvider](src/main/resources/generate-templates/springCloudMvnProvider)

step2: 新建生成处理器，[参考( C) SpringCloudMvnGeneratorProvider](src/main/java/com/wl4g/devops/dts/codegen/engine/generator/SpringCloudMvnGeneratorProvider.java)

step3: 新建源码规范工具类 (可选)，[参考(E) SpringCloudMvnGeneratorProvider](src/main/java/com/wl4g/devops/dts/codegen/engine/naming/SpringCloudMvnGeneratorProvider.java)

step4: 使新建处理器生效，[参考(A) CodegenAutoConfiguration#springMvcGeneratorProvider](src/main/java/com/wl4g/devops/dts/codegen/config/CodegenAutoConfiguration.java#springMvcGeneratorProvider)


#### 模版开发规则说明：

|模板用法|会生成多个{moduleName}目录或文件|会生成多个{entityName}目录或文件|
|-|-|-|
|#{moduleName}.ftl|√||
|/#{moduleName}/|√||
|#{entityName}.ftl||√|
|/#{entityName}/||√|
|/#{moduleName}/#{entityName}.ftl|√|√|
|/#{entityName}/#{entityName}.ftl||√|
|/#{entityName}/#{moduleName}.ftl|√|√|
|/#{moduleName}/#{moduleName}.ftl|√||