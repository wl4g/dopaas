# Cross Platform Code Generate Engine

> Cross platform code generation engine (independent of development language, framework and technical architecture)


## Developer guide

#### Building

```
# First, build the pre dependency project
git clone https://github.com/wl4g/parent.git # Upstream repo latest
#git clone https://gitee.com/wl4g/parent.git # Friends in China suggest faster
cd parent
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/components.git # Upstream repo latest
#git clone https://gitee.com/wl4g/components.git # Friends in China suggest faster
cd components
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/shell.git # Upstream repo latest
#git clone https://gitee.com/wl4g/shell.git # Friends in China suggest faster
cd shell
mvn clean install -DskipTests -T 2C

git clone https://github.com/wl4g/iam.git # Upstream repo latest
#git clone https://gitee.com/wl4g/iam.git # Friends in China suggest faster
cd iam
mvn clean install -DskipTests -T 2C

# Building
git clone https://github.com/wl4g/dopaas.git # Upstream repo latest
#git clone https://gitee.com/wl4g/dopaas.git # Friends in China suggest faster
cd dopaas
mvn clean install -DskipTests -T 2C
```
> If the ```error xx/xxx: Filename too long``` error, this is due to the too long template path of the Devops DTS CodeGen sub project. It is recommended to set ```git config --global core.longpaths true``` , Then pull it again.

#### Project structure
```
├── main
    ├── java
      └── com
    	└── wl4g
          └── dopaas
             └── dts
               └── codegen
                    ├── config
                    │   ├── CodegenAutoConfiguration.java // (A)Entry configuration
                    ├── engine
                    │   ├── converter
                    │   │   ├── DbTypeConverter.java // (B)Multi database type converter(support MySQL/Oracle/PostgreSQL/...)
                    │   ├── generator
                    │   │   ├── render.java
                    │   │   │   ├── ModelAttributeConstants.java // (G)Built in rendering model variable name definition
                    // ...
                    │   │   ├── IamSpringCloudMvnGeneratorProvider.java // (C)Multi language and framework combination generator
                    │   ├── resolver
                    │   │   ├── MySQLV5MetadataResolver.java // (D)Multiple database table metadata parser(support MySQL/Oracle/PostgreSQL/...)
                    // ...
                    │   ├── specs
                    │   │   ├── JavaSpecs.java // (E)Multi language project generation requires specific and different specification processing tools(support Java/Golang/Python/CSharp/Vue)
                    │   └── template
                    │       ├── ClassPathGenTemplateLocator.java // (F)Multi mode template loader
                    // ...
    └── resources
        ├── generate-templates
            ├── iamSpringCloudMvnProvider // (G) Template project corresponding to different frame combination generators
                └── #{javaSpecs.lCase(organName)}-#{javaSpecs.lCase(projectName)}
                    // Ignore irrelevant code...

```


#### Development steps
step1: New template project, [refer(G) iamSpringCloudMvnProvider](src/main/resources/generate-templates/iamSpringCloudMvnProvider)

step2: New generator, [refer( C) IamSpringCloudMvnGeneratorProvider](src/main/java/com/wl4g/dopaas/dts/codegen/engine/generator/IamSpringCloudMvnGeneratorProvider.java)

step3: New rendering template tools (Recommend)，[refer(E) JavaSpecs](src/main/java/com/wl4g/dopaas/dts/codegen/engine/specs/JavaSpecs.java)

step4: Configure to create a new generator[refer(A) CodegenAutoConfiguration#iamSpringMvcGeneratorProvider](src/main/java/com/wl4g/dopaas/dts/codegen/config/CodegenAutoConfiguration.java#iamSpringMvcGeneratorProvider)

step5: Startup the service on your IDE, the entry class: dopaas-dts-starter/src/main/java/com/wl4g/DtsManager.java

> Tip: when the default debugger mode is started, the template directory and mybatis mapper file do not need to be restarted, and the system will automatically hot load.


#### Description of template development rules:

- The template with ergodic property is used:

> For example, the variable #{entityName} exists on the template path, and multiple files are generated during rendering.

|Template syntax(for example)|Multiple {modulename} directories or files are generated|Multiple {entityname} directories or files are generated|
|-|-|-|
|#{moduleName}.ftl|√||
|/#{moduleName}/|√||
|#{entityName}.ftl||√|
|/#{entityName}/||√|
|/#{moduleName}/#{entityName}.ftl|√|√|
|/#{entityName}/#{entityName}.ftl||√|
|/#{entityName}/#{moduleName}.ftl|√|√|
|/#{moduleName}/#{moduleName}.ftl|√||

- Template with 'if' directive uses:

> 1. Usage1 (simple variable): there is  @if-entityName! On the template path. When the variable entityName exists in the rendering model, the template will generate the corresponding file.

```
For example: com/myproject/example/@if-entityName!#{entityName}Controller.java.ftl
```

> 2. Usage2 (SPEL expression): The template path contains @if-#{javaSpecs.isConf(extraOption,'gen.build.assets-type','MvnAssTar')} , the template will generate the corresponding file only when the expression returns true.

```
For example: com/myproject/example/@if-#{javaSpecs.isConf(extraOption,'gen.build.assets-type','MvnAssTar')}!#{entityName}Controller.java.ftl
```



##### <font color=red>Note:</font>
> <font color=red>Write code in template path as long as possible, because the operating system has restrictions on file directory and path, such as the maximum directory name length of Linux is 255 characters, the maximum length of file path is 4096 characters, usually only 1 to 2 expressions will exist on one template path, which is generally enough.</font>

> <font color=red>If it is too long, the following measures can be used to optimize:</font>
> - <font color=red>a. When the directory name expression is too long, it is recommended to split it into multi-level directories;</font>
> - <font color=red>b. When the overall path is too long, it is recommended to use relevant tool classes such as javaspecs;</font>
