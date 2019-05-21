Shell-cli 它是一个基于SpringCloud服务的开源命令行工具，运行方式类似于spark-shell。

English version goes [here](README_EN.md).

# 快速开始

## 源码编译
```
cd super-devops-shell
mvn clean install -DskipTests 
```

## 启动

### 方式一
指定服务的端口，然后以客户端运行（适用于客户端模式，通常临时用于连接应用服务使用）：

```
java -Dservpoint=60120 -Dprompt=console -Dtimeout=5000 -jar shell-cli-master-executable.jar
```

在上面的命令中 -Dservpoint 表示要连接的SpringCloud服务侦听地址和端口。

### 方式二
指定服务的名称，然后直接作为客户端运行（适用于本地模式，通常作为应用服务的内置控制台使用）。

```
java -Dservname=devopsShellExample -Dprompt=console -Dtimeout=5000 -jar shell-cli-master-executable.jar
```

上面的命令中 -Dservname 表示服务端SpringCloud应用名称（对应spring.application.name），它会依据servname在本地自动查找服务端口建立连接（注意大小写）.
也可使用 [方式一](#方式一) 以-Dservpoint 来显示指定服务端点，其中使用 -Dprompt 来设置shell控制台的命令行提示符，
-Dtimeout 指定等待结果返回超时时间（默认:10_000ms）。


## 特性

- 按TAB键自动补全
实战：
![tab自动补全](use_tab.jpg)
- Ctrl+A 光标跳至行首、Ctrl+E 光标跳至行尾、Ctrl+C 退出控制台（遵循GNU）

## 内置命令 
- clear/cls    清理控制台
- exit/ex/quit/qu    退出控制台
- history/his    查看历史命令（持久文件：$USER_HOME/.devops/shell/history）
- stacktrace/st    查看上一次异常的堆栈信息（若有）
- help/he    使用帮助，用法如：help/help sumTest/sumTest --help/sumTest --he/  其中 sumTest 为一个求和的测试命令
实战：
![help帮助](use_help.jpg)

## 自定义命令

[完整示例](super-devops-shell-example/src/main/java/com/wl4g/devops/shell/exporter/ExampleExporter.java)
