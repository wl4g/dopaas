Shell-cli is an open source command-line tool based on spring cloud service, which is similar to the way spark-shell works.

[中文说明/中文文档](README_CN.md)

# Quick start

## Source code compilation
```
cd super-devops-shell
mvn clean install -DskipTests 
```

### Way1
Specify the port of the service and then run as a client (for client mode, usually temporarily used to connect application services):

```
java -Dservpoint=10.0.0.160:60120 -Dtimeout=5000 -jar shell-cli-master-executable.jar
```
	
In the above command -Dservpoint=10.0.0.160:60120 indicates the Spring Cloud service 
listening address and port to connect to.

### Way2
Specify the name of the service and then run directly as a client (for local mode, usually as a built-in console for application services).

```
java -Dservname=devopsShellExample -Dprompt=console -Dtimeout=5000 -jar shell-cli-master-executable.jar 
```

In the above command, the -Dservname represents the Spring Cloud application name on the server side 
(corresponding to 'spring.application.name'), which automatically finds the service port locally based on the servname
and establishes the connection (pay attention to case). You can also use Mode 1 (#Way1) to display the specified service
endpoint with -Dservpoint, where -Dprompt is used to set the command line prompt of the shell console. -Dtimeout specifies
the time-out for waiting results to return (default: 10_000ms).

## Features
- Press TAB key to complete automatically
Actual combat:
![tab complete](use_tab.jpg)
- Ctrl+A cursor jumps to the beginning of the line, Ctrl+E cursor jumps to the end of the line, Ctrl+C exits the console (follow GNU)

## Built-in commands:
- clear/cls    Cleaning console
- exit/ex/quit/qu    Exit console
- history/his    View the history command (persistent file: $USER_HOME/.devops/shell/history)
- stacktrace/st    View stack information for the last exception (if any)
- help/he    Use help, such as: help/help sumTest/sumTest --help/sumTest --he/ where sumTest is a summation test command
Actual combat:
![help](use_help.jpg)

## Custom commands

[For perfect example](super-devops-shell-example/src/main/java/com/wl4g/devops/shell/exporter/ExampleExporter.java)
	