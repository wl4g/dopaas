# 跨平台代码生成引擎 (XCloud Cross Platform Code Generate Engine)

> 跨平台代码生成引擎（与开发语言、框架和技术架构无关）


## 开发指南

#### 构建项目

```
# 首先构建前置依赖项目
git clone https://github.com/wl4g/xcloud-parent.git # 上游最新
#git clone https://gitee.com/wl4g/xcloud-parent.git # 大陆较快
cd xcloud-parent
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-components.git # 上游最新
#git clone https://gitee.com/wl4g/xcloud-components.git # 大陆较快
cd xcloud-components
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-shell.git # 上游最新
#git clone https://gitee.com/wl4g/xcloud-shell.git # 大陆较快
cd xcloud-shell
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/xcloud-iam.git # 上游最新
#git clone https://gitee.com/wl4g/xcloud-iam.git # 大陆较快
cd xcloud-iam
mvn clean install -DskipTests -T 2C

# 构建
git clone https://github.com/wl4g/xcloud-paas.git # 上游最新
#git clone https://gitee.com/wl4g/xcloud-paas.git # 大陆较快
cd xcloud-paas
mvn clean install -DskipTests -T 2C
```
> 若出现  ```error xx/xxx: Filename too long```  错误，这是由于devops dts codegen子项目的模板路径过长导致，建议设置 ```git config --global core.longpaths true``` 后重新拉。

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
                    │   │   ├── DbTypeConverter.java // (B)多数据库类型转换器(支持MySQL/Oracle/PostgreSQL)
                    │   ├── generator
                    │   │   ├── render.java
                    │   │   │   ├── ModelAttributeConstants.java // (G)内置的渲染模型变量名定义
                    // ...
                    │   │   ├── IamSpringCloudMvnGeneratorProvider.java // (C)多种语言、框架组合生成器
                    │   ├── resolver
                    │   │   ├── MySQLV5MetadataResolver.java // (D)多种数据库表元数据解析器(支持MySQL/Oracle/PostgreSQL)         │   │
                    // ...
                    │   ├── specs
                    │   │   ├── JavaSpecs.java // (E)多种语言项目生成，需要有特定不同的规范处理工具类(支持Java/Golang/Python/Csharp/Vue)
                    │   └── template
                    │       ├── ClassPathGenTemplateLocator.java // (F)多种方式的模版工程加载器
                    // ...
    └── resources
        ├── generate-templates
            ├── iamSpringCloudMvnProvider // (G) 多种不同框架组合生成器对应的模版工程
                └── #{javaSpecs.lCase(organName)}-#{javaSpecs.lCase(projectName)}
                    // 省略无关代码...

```


#### 开发步骤
step1: 新建模版工程，[参考(G) iamSpringCloudMvnProvider](src/main/resources/generate-templates/iamSpringCloudMvnProvider)

step2: 新建生成处理器，[参考( C) IamSpringCloudMvnGeneratorProvider](src/main/java/com/wl4g/devops/dts/codegen/engine/generator/IamSpringCloudMvnGeneratorProvider.java)

step3: 新建模版渲染工具类 (建议)，[参考(E) JavaSpecs](src/main/java/com/wl4g/devops/dts/codegen/engine/specs/JavaSpecs.java)

step4: 使新建处理器生效，[参考(A) CodegenAutoConfiguration#iamSpringMvcGeneratorProvider](src/main/java/com/wl4g/devops/dts/codegen/config/CodegenAutoConfiguration.java#iamSpringMvcGeneratorProvider)

step5: 准备在您的IDE上启动服务，入口类：  xcloud-paas-dts-starter/src/main/java/com/wl4g/DtsManager.java

> 提示：默认debugger模式启动时，模板目录和mybatis mapper文件更新不需重启，系统会自动热加载。

#### 模版开发规则说明：

万不得已时推荐以下优化措施：
a. 若目录名过长，可拆分为两级目录；
b. 若整体路径过长，可在路径上调用 XxxSpecs 相关的工具方法进行处理；
</font>


- 有遍历性质的模板使用：
> 例如，模板路径上存在 #{entityName}这个变量，渲染时会生成多个文件

|示例用法|会生成多个{moduleName}目录或文件|会生成多个{entityName}目录或文件|
|-|-|-|
|#{moduleName}.ftl|√||
|/#{moduleName}/|√||
|#{entityName}.ftl||√|
|/#{entityName}/||√|
|/#{moduleName}/#{entityName}.ftl|√|√|
|/#{entityName}/#{entityName}.ftl||√|
|/#{entityName}/#{moduleName}.ftl|√|√|
|/#{moduleName}/#{moduleName}.ftl|√||


- 带 'if' 指令的模板使用：
> 1. 用法1（简单变量）：模板路径上存在 @if-entityName!，当渲染model中存在 entityName 这个变量时此模板才会生成对应的文件。
```
例如：com/myproject/example/@if-entityName!#{entityName}Controller.java.ftl
```

> 2. 用法2（SPEL表达式）：模板路径上存在 @if-#{javaSpecs.isConf(extraOption,'gen.build.assets-type','MvnAssTar')}，当表达式返回true时此模板才会生成对应的文件。

```
例如：com/myproject/example/@if-#{javaSpecs.isConf(extraOption,'gen.build.assets-type','MvnAssTar')}!#{entityName}Controller.java.ftl
```


##### <font color=red>注意：</font>
> <font color=red>在模板路径写代码尽量不要太长，因为操作系统对文件目录和路径有限制，如linux最大目录名长度为 255个字符、文件路径最大长度为 4096个字符通常一个模板路径上只会存在1到2个表达式，一般是够用的。</font>

> <font color=red>万一过长也可使用以下措施优化：</font>

- <font color=red>a. 当目录名表达式过长时，建议拆分为多级目录；</font>
- <font color=red>b. 当路径整体过长时，建议使用如 javaSpecs 相关工具类处理；</font>
