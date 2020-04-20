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
java -Dservpoint=127.0.0.1:60103 -Dprompt=my-shell -Dtimeout=5000 -jar shell-cli-master-executable.jar
```

In the above command, the -Dservpoint indicates the SpringBoot/Cloud service listening address and port to connect to.

### Way2
Specify the name of the service and then run directly as a client (for local mode, usually as a built-in console for application services).

```
java -Dservname=shell-example -Dprompt=my-shell -Dtimeout=5000 -jar shell-cli-master-executable.jar
```

In the above command, the -Dservname represents the Spring Cloud application name on the server side 
(corresponding to 'spring.application.name'), which automatically finds the service port locally based on the servname
and establishes the connection (pay attention to case). You can also use Mode 1 (#Way1) to display the specified service
endpoint with -Dservpoint, where -Dprompt is used to set the command line prompt of the shell console. -Dtimeout specifies
the time-out for waiting results to return (default: 180_000ms), and can print debugging information using -Dxdebug.

## Features
- Ctrl+A cursor jumps to the beginning of the line, Ctrl+E cursor jumps to the end of the line, Ctrl+C exits the console (follow GNU)
![tab auto-completion](shots/use_tab.jpg)
- Connect use serverpoint
![Connect use serverpoint](shots/use_servpoint.jpg)
- help
![help](shots/use_help.jpg)
- Forced interruption of tasks running
![Forced interruption of tasks running](shots/force_interrupt.jpg)
- Support interactive operation of real-time progress bar and interrupt confirmation
![progress and interrupt confirm](shots/progress_interrupt.jpg)

## Built-in commands:
- clear/cls    Cleaning console
- exit/ex/quit/qu    Exit console
- history/his    View the history command (persistent file: $USER_HOME/.devops/shell/history)
- stacktrace/st    View stack information for the last exception (if any)
- help/he    Use help, such as: help/help sumTest/sumTest --help/sumTest --he/ where sumTest is a summation test command

## Custom commands

[For perfect example](super-devops-shell-example/src/main/java/com/wl4g/devops/shell/console/ExampleConsole.java)
	